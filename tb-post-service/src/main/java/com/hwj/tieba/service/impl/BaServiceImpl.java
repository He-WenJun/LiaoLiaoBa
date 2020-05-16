package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.*;
import com.hwj.tieba.entity.*;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.BaService;
import com.hwj.tieba.util.DateUtil;
import com.hwj.tieba.util.FigureUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.BaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BaServiceImpl implements BaService {
    @Autowired
    private BaMapper baMapper;
    @Autowired
    private BaTypeMapper baTypeMapper;
    @Autowired
    private SubscribeMapper subscribeMapper;
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public ServerResponse<String> baSignIn(String sessionId) {
        Account account = getAccount(sessionId);
        //获取当前用户订阅的贴吧
        String subscribeKey = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        List<BaVO> baVOList = null;
        List<Subscribe> subscribeList = null;
        if(redisUtil.hasKey(subscribeKey)){
            baVOList = redisUtil.getArray(subscribeKey,BaVO.class);
        }else {
            subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(account.getUserId(),Constants.SubscribeType.BA);
        }

        if(baVOList == null && subscribeList.size() == 0){
            throw new TieBaException("您还没有订阅贴吧哦");
        }

        //取出订阅贴吧的Id
        List<String> baIdList = new ArrayList<String>();
        if(baVOList != null){
            for (BaVO baVO : baVOList){
                baIdList.add(baVO.getBaId());
            }
        }else {
            for (Subscribe subscribe : subscribeList){
                baIdList.add(subscribe.getObjectId());
            }
        }

        //组装签到key
        String signInKey = Constants.POST_TOKEN_PREFIX+"SIGN_IN:"+account.getUserId();
        //签到过的贴吧Id
        List<String> signInBaIdList = null;
        //若存在签到过的贴吧Id，则对签到过的贴吧进行过滤，避免重复签到
        if(redisUtil.hasKey(signInKey)){
            signInBaIdList = redisUtil.getArray(signInKey,String.class);
            for(int i = 0; i < baIdList.size(); i++){
                for(int j = 0; j < signInBaIdList.size(); j++){
                    if(baIdList.get(i).equals(signInBaIdList.get(j))){
                        baIdList.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }

        //若要签到的贴吧Id集合长度为0则说明今天已经全部签到过了
        if(baIdList.size() == 0){
            throw new TieBaException("您今天已经全部签到过了哦");
        }

        //更新贴吧经验值
        baMapper.updateBaExpByBaId(baIdList,Constants.SING_IN_EXP);

        //将签到过的贴吧Id合并
        if(signInBaIdList == null){
            signInBaIdList = baIdList;
        }else {
            for(int i =0; i<baIdList.size(); i++){
                signInBaIdList.add(baIdList.get(i));
            }
        }

        //获取今天还剩多少秒
        int seconds = DateUtil.getSeconds();
        //将签到过的贴吧Id存缓存，用于判断是否重复签到
        redisUtil.set(signInKey,seconds,JSON.toJSON(signInBaIdList));

        //拼接用于增加账号经验值的token Key，帮助判断增加账号经验值的请求是不是系统发出的
        String increaseAccountExpKey = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+account.getUserId();
        String token = UUIDUtil.getStringUUID();
        redisUtil.setStr(increaseAccountExpKey,60,token);
        //调用user-service服务，为当前账号增加经验值
        ServerResponse serverResponse = userService.increaseAccountExp(baIdList.size() * 3,token,account.getUserId());
        System.out.println(JSON.toJSONString(serverResponse));
        serverResponse.setMsg(serverResponse.getMsg()+",成功帮助"+baIdList.size()+"个贴吧增加等级经验值");
        return serverResponse;
    }

    @Override
    public ServerResponse<String> singleBaSignIn(String sessionId, String baId) {
        String userId = getAccount(sessionId).getUserId();
        //获取当前用户订阅的贴吧
        String subscribeKey = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+userId;
        List<BaVO> baVOList = null;
        List<Subscribe> subscribeList = null;
        if(redisUtil.hasKey(subscribeKey)){
            baVOList = redisUtil.getArray(subscribeKey,BaVO.class);
        }else {
            subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(userId,Constants.SubscribeType.BA);
        }

        if(baVOList == null && subscribeList.size() == 0){
            throw new TieBaException("您还没有订阅贴吧哦");
        }

        //判断是否订阅了这个贴吧，只有订阅才能签到
        if(baVOList != null){
            boolean isOk = false;
            for (BaVO baVO : baVOList){
                if(baVO.getBaId().equals(baId)){
                    isOk = true;
                }
            }
            if(!isOk){
                throw new TieBaException("您还未订阅当前贴吧哦");
            }
        }else {
            boolean isOk = false;
            for (Subscribe subscribe : subscribeList){
                if(subscribe.getObjectId().equals(baId)){
                    isOk = true;
                    break;
                }
            }
            if(!isOk){
                throw new TieBaException("您还未订阅当前贴吧哦");
            }
        }

        String signInKey = Constants.POST_TOKEN_PREFIX+"SIGN_IN:"+userId;
        //若存在签到过的贴吧Id，则判断要签到贴吧Id是否已经签到过了
        List<String> signInBaIdList = null;
        if(redisUtil.hasKey(signInKey)){
            signInBaIdList = redisUtil.getArray(signInKey,String.class);
            for(int i = 0; i < signInBaIdList.size(); i++){
                if(signInBaIdList.get(i).equals(baId)){
                    throw  new TieBaException("您今天已经签到过了");
                }
            }
        }
        List<String> baIdList = new ArrayList<String>();
        baIdList.add(baId);
        //修改签到的贴吧经验值
        baMapper.updateBaExpByBaId(baIdList,Constants.SING_IN_EXP);

        //合并签到的贴吧Id
        if(signInBaIdList == null){
            signInBaIdList = baIdList;
        }else {
            signInBaIdList.add(baIdList.get(0));
        }

        //获取今天还剩多少秒
        int seconds = DateUtil.getSeconds();
        //将签到过的贴吧Id存缓存，用于判断是否重复签到
        redisUtil.set(signInKey,seconds,JSON.toJSON(signInBaIdList));

        //拼接用于增加账号经验值的token Key，帮助判断增加账号经验值的请求是不是系统发出的
        String increaseAccountExpKey = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+userId;
        String token = UUIDUtil.getStringUUID();
        redisUtil.setStr(increaseAccountExpKey,60,token);
        //调用user-service服务，为当前账号增加经验值
        ServerResponse serverResponse = userService.increaseAccountExp(baIdList.size() * 3,token,userId);
        serverResponse.setMsg(serverResponse.getMsg()+",成功帮助"+baIdList.size()+"个贴吧增加等级经验值");
        return serverResponse;
    }

    @Override
    public ServerResponse<String> verificationSignIn(String sessionId, String baId) {
        String signInKey = Constants.POST_TOKEN_PREFIX+"SIGN_IN:"+getAccount(sessionId).getUserId();
        if(redisUtil.hasKey(signInKey)){
            List<String> signInBaIdList = redisUtil.getArray(signInKey,String.class);
            for(int i = 0; i < signInBaIdList.size(); i++){
                if(signInBaIdList.get(i).equals(baId)){
                    throw  new TieBaException(2,"已签到");
                }
            }
        }
        return ServerResponse.createBySuccess("签到",null);
    }

    @Override
    public ServerResponse<PageInfo<BaVO>> baList(int pageNumber,String typeId) {
        //判断字符串是否为空，并判断是否是数字
        if(StringUtils.isEmpty(typeId) || !FigureUtil.isNumeric(typeId)){
            throw new TieBaException("参数有误");
        }
        String key = Constants.POST_TOKEN_PREFIX + "BA_LIST:"+ typeId + ":" + pageNumber;
        if(redisUtil.hasKey(key)){
            PageInfo<BaVO> pageInfo = redisUtil.get(key,PageInfo.class);
            return ServerResponse.createBySuccess(pageInfo);
        }

        //查询类型Id为此类型Id的贴吧
        Page<Object> page = PageHelper.startPage(pageNumber,12,true);
        List<Ba> baList = baMapper.queryBaByTypeId(typeId);
        if(baList.size() == 0){
            return ServerResponse.createByErrorMessage("没有更多数据了");
        }
        //取出贴吧Id和头像Id
        List<String> baIdList = new ArrayList<String>();
        List<String> headPictureList = new ArrayList<String>();
        for(Ba ba : baList){
            baIdList.add(ba.getBaId());
            headPictureList.add(ba.getHeadPictureId());
        }
        //查询吧对应的订阅数量
        List<Map<String,Object>> countSubscribeMap = subscribeMapper.countSubscribe(baIdList);
        //查询吧对应的帖子数量
        List<Map<String,Object>> countPostMap = postMapper.countPost(baIdList);
        //查询吧对应的头像图片路径
        List<File> imageList = fileMapper.queryFileListById(headPictureList);
        List<BaVO> baVOList = new ArrayList<BaVO>();
        for(int i = 0; i < baList.size(); i++){
            Ba ba = baList.get(i);
            BaVO baVO = new BaVO();
            baVO.setBaName(ba.getBaName());
            baVO.setBaId(ba.getBaId());
            baVO.setIntroduce(ba.getIntroduce());

            for(int j = 0; j < countSubscribeMap.size(); j++){
                if(ba.getBaId().equals(countSubscribeMap.get(j).get("objectId"))){
                    baVO.setCountSubscribe(String.valueOf(countSubscribeMap.get(j).get("count")));
                    break;
                }
            }

            for (int j = 0; j < countPostMap.size(); j++){
                if(ba.getBaId().equals(countPostMap.get(j).get("baId"))){
                    baVO.setCountPost(String.valueOf(countPostMap.get(j).get("count")));
                    break;
                }
            }

            for (int j = 0; j < imageList.size(); j++){
                if(ba.getHeadPictureId().equals(imageList.get(j).getId())){
                    baVO.setHeadPictureSrc(imageList.get(j).getSrc().substring(imageList.get(j).getSrc().indexOf("file")-1));
                    break;
                }
            }

            baVOList.add(baVO);
        }

        PageInfo<BaVO> pageInfo = new PageInfo(page);
        pageInfo.setList(baVOList);
        redisUtil.set(key,Constants.KEY_EXPIRES,pageInfo);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<BaVO> baInfo(String id) {
        if(StringUtils.isEmpty(id)){
            throw new TieBaException("参数错误");
        }
        String key = Constants.POST_TOKEN_PREFIX + "TIE_BA_INFO:" + id;
        if(redisUtil.hasKey(key)){
            BaVO baVO = redisUtil.get(key,BaVO.class);
            return ServerResponse.createBySuccess(baVO);
        }
        //根据贴吧Id查询贴吧信息
        Ba ba = baMapper.queryBaByBaId(id);
        if(ba == null){
            throw new TieBaException("没有这个贴吧");
        }
        List<String> baIdList = new ArrayList<String>();
        baIdList.add(ba.getBaId());
        //查询吧对应的订阅数量
        List<Map<String,Object>> countSubscribeMapList = subscribeMapper.countSubscribe(baIdList);
        //查询吧对应的帖子数量
        List<Map<String,Object>> countPostMapList = postMapper.countPost(baIdList);
        //查询吧的类型名称
        BaSonType sonType = baTypeMapper.querySonTypeById(ba.getTypeId());

        List<String> pictureIdList = new ArrayList<String>();
        pictureIdList.add(ba.getHeadPictureId());
        pictureIdList.add(ba.getBackgroundPictureId());
        //查询吧对应的头像图片路径,和背景图片路径
        List<File> imageList = fileMapper.queryFileListById(pictureIdList);

        //计算贴吧等级
        int level = 1;
        Long exp = ba.getExp();
        //若当前吧的经验值大于每级经验值所需经验，则计算吧等级，并算出经验值余数
        if(exp >= Constants.LEVEL){
            level = (int)( exp / Constants.LEVEL);
            exp = exp % Constants.LEVEL;
        }

        //组装返回数据
        BaVO baVO = new BaVO();
        baVO.setBaId(ba.getBaId());
        baVO.setIntroduce(ba.getIntroduce());
        baVO.setBaName(ba.getBaName());
        baVO.setTypeId(String.valueOf(ba.getTypeId()));
        baVO.setTypeName(sonType.getTypeName());
        baVO.setExp(exp);
        baVO.setLevel(level);
        baVO.setEnrollDate(ba.getEnrollDate());

        for(int i = 0; i < imageList.size(); i++){
            String path = imageList.get(i).getSrc().substring(imageList.get(i).getSrc().indexOf("file") - 1);
            if(ba.getHeadPictureId().equals(imageList.get(i).getId())){
                baVO.setHeadPictureSrc(path);
            }else if(ba.getBackgroundPictureId().equals(imageList.get(i).getId())){
                baVO.setBackgroundPictureSrc(path);
            }
        }

        if(countSubscribeMapList.size() > 0){
            baVO.setCountSubscribe(countSubscribeMapList.get(0).get("count").toString());
        }else {
            baVO.setCountSubscribe("0");
        }

        if(countPostMapList.size() > 0){
            baVO.setCountPost(countPostMapList.get(0).get("count").toString());
        }else {
            baVO.setCountPost("0");
        }

        redisUtil.set(key,Constants.KEY_EXPIRES,baVO);

        return ServerResponse.createBySuccess(baVO);
    }



    private Account getAccount(String sessionId){
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //判断当前session当中是否存在账户号json，若不存在则未登录
        if(sessionMap.get("Account") == null){
            throw new TieBaException("未登录");
        }
        return JSON.parseObject(sessionMap.get("Account"),Account.class);
    }

}
