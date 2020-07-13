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
import com.hwj.tieba.service.ModuleService;
import com.hwj.tieba.util.DateUtil;
import com.hwj.tieba.util.FigureUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.ModuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ModuleServiceImpl implements ModuleService {
    @Autowired
    private ModuleMapper moduleMapper;
    @Autowired
    private ModuleTypeMapper moduleTypeMapper;
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
    public ServerResponse<String> moduleSignIn(String sessionId) {
        Account account = getAccount(sessionId);
        //获取当前用户喜欢的模块
        String subscribeKey = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        List<ModuleVo> moduleVoList = null;
        List<Subscribe> subscribeList = null;
        if(redisUtil.hasKey(subscribeKey)){
            moduleVoList = redisUtil.getArray(subscribeKey, ModuleVo.class);
        }else {
            subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(account.getUserId(),Constants.SubscribeType.MODULE);
        }

        if(moduleVoList == null && subscribeList.size() == 0){
            throw new TieBaException("您还没有喜欢的模块哦");
        }

        //取出喜欢的模块的Id
        List<String> moduleIdList = new ArrayList<String>();
        if(moduleVoList != null){
            for (ModuleVo moduleVo : moduleVoList){
                moduleIdList.add(moduleVo.getModuleId());
            }
        }else {
            for (Subscribe subscribe : subscribeList){
                moduleIdList.add(subscribe.getObjectId());
            }
        }

        //组装签到key
        String signInKey = Constants.POST_TOKEN_PREFIX+"SIGN_IN:"+account.getUserId();
        //签到过的模块Id
        List<String> signInModuleIdList = null;
        //若存在签到过的模块Id，则对签到过的模块进行过滤，避免重复签到
        if(redisUtil.hasKey(signInKey)){
            signInModuleIdList = redisUtil.getArray(signInKey,String.class);
            for(int i = 0; i < moduleIdList.size(); i++){
                for(int j = 0; j < signInModuleIdList.size(); j++){
                    if(moduleIdList.get(i).equals(signInModuleIdList.get(j))){
                        moduleIdList.remove(i);
                        i--;
                        break;
                    }
                }
            }
        }

        //若要签到的模块Id集合长度为0则说明今天已经全部签到过了
        if(moduleIdList.size() == 0){
            throw new TieBaException("您今天已经全部签到过了哦");
        }

        //更新模块经验值
        moduleMapper.updateModuleExpByModuleId(moduleIdList,Constants.SING_IN_EXP);

        //将签到过的模块Id合并
        if(signInModuleIdList == null){
            signInModuleIdList = moduleIdList;
        }else {
            for(int i =0; i<moduleIdList.size(); i++){
                signInModuleIdList.add(moduleIdList.get(i));
            }
        }

        //获取今天还剩多少秒
        int seconds = DateUtil.getSeconds();
        //将签到过的模块Id存缓存，用于判断是否重复签到
        redisUtil.set(signInKey,seconds,JSON.toJSON(signInModuleIdList));

        //拼接用于增加账号经验值的token Key，帮助判断增加账号经验值的请求是不是系统发出的
        String increaseAccountExpKey = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+account.getUserId();
        String token = UUIDUtil.getStringUUID();
        redisUtil.setStr(increaseAccountExpKey,60,token);
        //调用user-service服务，为当前账号增加经验值
        ServerResponse serverResponse = userService.increaseAccountExp(moduleIdList.size() * 3,token,account.getUserId());
        serverResponse.setMsg(serverResponse.getMsg()+",成功帮助"+moduleIdList.size()+"个模块增加等级经验值");
        return serverResponse;
    }

    @Override
    public ServerResponse<String> singleModuleSignIn(String sessionId, String moduleId) {
        String userId = getAccount(sessionId).getUserId();
        //获取当前用户喜欢的模块
        String subscribeKey = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+userId;
        List<ModuleVo> moduleVoList = null;
        List<Subscribe> subscribeList = null;
        if(redisUtil.hasKey(subscribeKey)){
            moduleVoList = redisUtil.getArray(subscribeKey, ModuleVo.class);
        }else {
            subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(userId,Constants.SubscribeType.MODULE);
        }

        if(moduleVoList == null && subscribeList.size() == 0){
            throw new TieBaException("您还没有喜欢的模块哦");
        }

        //判断是否喜欢了这个模块，只有喜欢才能签到
        if(moduleVoList != null){
            boolean isOk = false;
            for (ModuleVo moduleVo : moduleVoList){
                if(moduleVo.getModuleId().equals(moduleId)){
                    isOk = true;
                }
            }
            if(!isOk){
                throw new TieBaException("您还未喜欢当前模块哦");
            }
        }else {
            boolean isOk = false;
            for (Subscribe subscribe : subscribeList){
                if(subscribe.getObjectId().equals(moduleId)){
                    isOk = true;
                    break;
                }
            }
            if(!isOk){
                throw new TieBaException("您还未喜欢当前模块哦");
            }
        }

        String signInKey = Constants.POST_TOKEN_PREFIX+"SIGN_IN:"+userId;
        //若存在签到过的模块Id，则判断要签到模块Id是否已经签到过了
        List<String> signInModuleIdList = null;
        if(redisUtil.hasKey(signInKey)){
            signInModuleIdList = redisUtil.getArray(signInKey,String.class);
            for(int i = 0; i < signInModuleIdList.size(); i++){
                if(signInModuleIdList.get(i).equals(moduleId)){
                    throw  new TieBaException("您今天已经签到过了");
                }
            }
        }
        List<String> moduleIdList = new ArrayList<String>();
        moduleIdList.add(moduleId);
        //修改签到的模块经验值
        moduleMapper.updateModuleExpByModuleId(moduleIdList,Constants.SING_IN_EXP);

        //合并签到的模块Id
        if(signInModuleIdList == null){
            signInModuleIdList = moduleIdList;
        }else {
            signInModuleIdList.add(moduleIdList.get(0));
        }

        //获取今天还剩多少秒
        int seconds = DateUtil.getSeconds();
        //将签到过的模块Id存缓存，用于判断是否重复签到
        redisUtil.set(signInKey,seconds,JSON.toJSON(signInModuleIdList));

        //拼接用于增加账号经验值的token Key，帮助判断增加账号经验值的请求是不是系统发出的
        String increaseAccountExpKey = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+userId;
        String token = UUIDUtil.getStringUUID();
        redisUtil.setStr(increaseAccountExpKey,60,token);
        //调用user-service服务，为当前账号增加经验值
        ServerResponse serverResponse = userService.increaseAccountExp(moduleIdList.size() * 3,token,userId);
        serverResponse.setMsg(serverResponse.getMsg()+",成功帮助"+moduleIdList.size()+"个模块增加等级经验值");
        return serverResponse;
    }

    @Override
    public ServerResponse<String> verificationSignIn(String sessionId, String moduleId) {
        String signInKey = Constants.POST_TOKEN_PREFIX+"SIGN_IN:"+getAccount(sessionId).getUserId();
        if(redisUtil.hasKey(signInKey)){
            List<String> signInModuleIdList = redisUtil.getArray(signInKey,String.class);
            for(int i = 0; i < signInModuleIdList.size(); i++){
                if(signInModuleIdList.get(i).equals(moduleId)){
                    throw  new TieBaException(2,"已签到");
                }
            }
        }
        return ServerResponse.createBySuccess("签到",null);
    }

    @Override
    public ServerResponse<PageInfo<ModuleVo>> moduleList(int pageNumber, String typeId) {
        //判断字符串是否为空，并判断是否是数字
        if(StringUtils.isEmpty(typeId) || !FigureUtil.isNumeric(typeId)){
            throw new TieBaException("参数有误");
        }
        String key = Constants.POST_TOKEN_PREFIX + "MODULE_LIST:"+ typeId + ":" + pageNumber;
        if(redisUtil.hasKey(key)){
            PageInfo<ModuleVo> pageInfo = redisUtil.get(key,PageInfo.class);
            return ServerResponse.createBySuccess(pageInfo);
        }

        //查询类型Id为此类型Id的模块
        Page<Object> page = PageHelper.startPage(pageNumber,12,true);
        List<Module> moduleList = moduleMapper.queryModuleByTypeId(typeId);
        if(moduleList.size() == 0){
            return ServerResponse.createByErrorMessage("没有更多数据了");
        }
        //取出模块Id和头像Id
        List<String> moduleIdList = new ArrayList<String>();
        List<String> headPictureList = new ArrayList<String>();
        for(Module module : moduleList){
            moduleIdList.add(module.getModuleId());
            headPictureList.add(module.getHeadPictureId());
        }
        //查询模块对应的喜欢数量
        List<Map<String,Object>> countSubscribeMap = subscribeMapper.countSubscribe(moduleIdList);
        //查询模块对应的帖子数量
        List<Map<String,Object>> countPostMap = postMapper.countPost(moduleIdList);
        //查询模块对应的头像图片路径
        List<File> imageList = fileMapper.queryFileListById(headPictureList);
        List<ModuleVo> moduleVoList = new ArrayList<ModuleVo>();
        for(int i = 0; i < moduleList.size(); i++){
            Module module = moduleList.get(i);
            ModuleVo moduleVo = new ModuleVo();
            moduleVo.setModuleName(module.getModuleName());
            moduleVo.setModuleId(module.getModuleId());
            moduleVo.setIntroduceOmit(module.getIntroduce());

            for(int j = 0; j < countSubscribeMap.size(); j++){
                if(module.getModuleId().equals(countSubscribeMap.get(j).get("objectId"))){
                    moduleVo.setCountSubscribe(String.valueOf(countSubscribeMap.get(j).get("count")));
                    break;
                }
            }

            for (int j = 0; j < countPostMap.size(); j++){
                if(module.getModuleId().equals(countPostMap.get(j).get("moduleId"))){
                    moduleVo.setCountPost(String.valueOf(countPostMap.get(j).get("count")));
                    break;
                }
            }

            for (int j = 0; j < imageList.size(); j++){
                if(module.getHeadPictureId().equals(imageList.get(j).getId())){
                    moduleVo.setHeadPictureSrc(imageList.get(j).getSrc().substring(imageList.get(j).getSrc().indexOf("file")-1));
                    break;
                }
            }

            moduleVoList.add(moduleVo);
        }

        PageInfo<ModuleVo> pageInfo = new PageInfo(page);
        pageInfo.setList(moduleVoList);
        redisUtil.set(key,Constants.KEY_EXPIRES,pageInfo);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ModuleVo> moduleInfo(String id) {
        if(StringUtils.isEmpty(id)){
            throw new TieBaException("参数错误");
        }
        String key = Constants.POST_TOKEN_PREFIX + "TIE_BA_INFO:" + id;
        if(redisUtil.hasKey(key)){
            ModuleVo moduleVo = redisUtil.get(key, ModuleVo.class);
            return ServerResponse.createBySuccess(moduleVo);
        }
        //根据模块Id查询模块信息
        Module module = moduleMapper.queryModuleByModuleId(id);
        if(module == null){
            throw new TieBaException("没有这个模块");
        }
        List<String> moduleIdList = new ArrayList<String>();
        moduleIdList.add(module.getModuleId());
        //查询模块对应的被喜欢的数量数量
        List<Map<String,Object>> countSubscribeMapList = subscribeMapper.countSubscribe(moduleIdList);
        //查询模块对应的帖子数量
        List<Map<String,Object>> countPostMapList = postMapper.countPost(moduleIdList);
        //查询模块的类型名称
        ModuleSonType sonType = moduleTypeMapper.querySonTypeById(module.getTypeId());

        List<String> pictureIdList = new ArrayList<String>();
        pictureIdList.add(module.getHeadPictureId());
        pictureIdList.add(module.getBackgroundPictureId());
        //查询模块对应的头像图片路径,和背景图片路径
        List<File> imageList = fileMapper.queryFileListById(pictureIdList);

        //计算模块等级
        int level = 1;
        Long exp = module.getExp();
        //若当前模块的经验值大于每级经验值所需经验，则计算模块等级，并算出经验值余数
        if(exp >= Constants.LEVEL){
            level = (int)( exp / Constants.LEVEL);
            exp = exp % Constants.LEVEL;
        }

        //组装返回数据
        ModuleVo moduleVo = new ModuleVo();
        moduleVo.setModuleId(module.getModuleId());
        moduleVo.setIntroduce(module.getIntroduce());
        moduleVo.setModuleName(module.getModuleName());
        moduleVo.setTypeId(String.valueOf(module.getTypeId()));
        moduleVo.setTypeName(sonType.getTypeName());
        moduleVo.setExp(exp);
        moduleVo.setLevel(level);
        moduleVo.setEnrollDate(module.getEnrollDate());

        for(int i = 0; i < imageList.size(); i++){
            String path = imageList.get(i).getSrc().substring(imageList.get(i).getSrc().indexOf("file") - 1);
            if(module.getHeadPictureId().equals(imageList.get(i).getId())){
                moduleVo.setHeadPictureSrc(path);
            }else if(module.getBackgroundPictureId().equals(imageList.get(i).getId())){
                moduleVo.setModuleBackgroundPictureSrc(path);
            }
        }

        if(countSubscribeMapList.size() > 0){
            moduleVo.setCountSubscribe(countSubscribeMapList.get(0).get("count").toString());
        }else {
            moduleVo.setCountSubscribe("0");
        }

        if(countPostMapList.size() > 0){
            moduleVo.setCountPost(countPostMapList.get(0).get("count").toString());
        }else {
            moduleVo.setCountPost("0");
        }

        redisUtil.set(key,Constants.KEY_EXPIRES,moduleVo);

        return ServerResponse.createBySuccess(moduleVo);
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
