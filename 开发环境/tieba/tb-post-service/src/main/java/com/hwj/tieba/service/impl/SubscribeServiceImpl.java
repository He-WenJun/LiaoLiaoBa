package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.ModuleMapper;
import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.dao.SubscribeMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Module;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.entity.Subscribe;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.SubscribeService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.ModuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SubscribeServiceImpl implements SubscribeService {
    @Autowired
    private SubscribeMapper subscribeMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private ModuleMapper moduleMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<List<ModuleVo>> getSubscribeModule(String sessionId) {
        Account account = getAccount(sessionId);
        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        if(redisUtil.hasKey(key)){
            return ServerResponse.createBySuccess(redisUtil.getArray(key, ModuleVo.class));
        }
        //查询当前登录账号的订阅Id
        List<Subscribe> subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(account.getUserId(), Constants.SubscribeType.MODULE);
        if(subscribeList.size() == 0){
            throw new TieBaException(2,"您还没有订阅的模块哦");
        }
        //获取模块完整信息
        List<ModuleVo> moduleVOList =  moduleVoAssemble(subscribeList);
        //将订阅的模块存入redis
        redisUtil.set(Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId(),Constants.KEY_EXPIRES,moduleVOList);
        return ServerResponse.createBySuccess(moduleVOList);
    }

    @Override
    public ServerResponse<List<ModuleVo>> getSubscribeModuleByUserId(Account account) {
        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        if(redisUtil.hasKey(key)){
            return ServerResponse.createBySuccess(redisUtil.getArray(key, ModuleVo.class));
        }
        List<Subscribe> subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(account.getUserId(), Constants.SubscribeType.MODULE);
        if(subscribeList.size() == 0){
            throw new TieBaException(2,"Ta还没有喜欢的模块哦");
        }
        List<ModuleVo> moduleVOList =  moduleVoAssemble(subscribeList);
        redisUtil.set(Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId(),Constants.KEY_EXPIRES,moduleVOList);
        return ServerResponse.createBySuccess(moduleVOList);
    }

    @Override
    public ServerResponse<String> delSubscribe(String sessionId,String objectId) {
        if(StringUtils.isEmpty(objectId)){
            throw new TieBaException("参数错误");
        }

        Account account = getAccount(sessionId);

        subscribeMapper.deleteSubscribeByObjectIdAndUserId(objectId,account.getUserId());

        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        //删除缓存当中的订阅缓存
        redisUtil.del(key);

        return ServerResponse.createBySuccess("成功取消订阅",null );
    }

    @Override
    public ServerResponse<String> addSubscribeModule(String sessionId, String moduleName) {

        if(StringUtils.isEmpty(moduleName)){
            throw new TieBaException("参数有误");
        }

        Account account = getAccount(sessionId);
        //验证用户是否已经订阅了这个模块，不能重复订阅
        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserId();
        Module subscribeModule = null;
        if(redisUtil.hasKey(key)){
            List<ModuleVo> moduleVOList = redisUtil.getArray(key, ModuleVo.class);
            for(ModuleVo moduleVO : moduleVOList){
                if(moduleVO.getModuleName().equals(moduleName)){
                    throw new TieBaException("你已经订阅过这个模块了");
                }
            }
        }else {
            subscribeModule = moduleMapper.queryModuleByModuleName(moduleName);
            if(subscribeModule == null){
                throw new TieBaException("没有找到这个模块");
            }
            Subscribe resultSubscribe = subscribeMapper.querySubscribeByObjectIdAndUserId(subscribeModule.getModuleId(),account.getUserId());
            if(resultSubscribe != null){
                throw new TieBaException("你已经订阅过这个模块了");
            }
        }

        if(subscribeModule == null){
            subscribeModule = moduleMapper.queryModuleByModuleName(moduleName);
            if(subscribeModule == null){
                throw new TieBaException("没有找到这个模块");
            }
        }

        //实例化订阅对象，插入订阅表
        Date nowDate = new Date();
        Subscribe subscribe = new Subscribe(UUIDUtil.getStringUUID(),Constants.SubscribeType.MODULE,subscribeModule.getModuleId(),account.getUserId(),nowDate,nowDate);
        subscribeMapper.insertSubscribe(subscribe);
        //删除订阅的模块的缓存
        redisUtil.del(key);
        return ServerResponse.createBySuccess("订阅成功",null);
    }

    @Override
    public ServerResponse<String> addSubscribeUser(String sessionId, Subscribe subscribe) {
        if(subscribe == null | StringUtils.isEmpty(subscribe.getObjectId())){
            throw new TieBaException("参数错误");
        }

        Account account = getAccount(sessionId);
        if(account.getUserId().equals(subscribe.getObjectId())){
            throw new TieBaException(2,"不能自己关注自己哦");
        }

        Subscribe subscribeResult = subscribeMapper.querySubscribeByObjectIdAndUserId(subscribe.getObjectId(), account.getUserId());
        if(subscribeResult != null){
            subscribeMapper.deleteSubscribeByObjectIdAndUserId(subscribe.getObjectId(), account.getUserId());
            //删除缓存
            String objectConcern = Constants.POST_TOKEN_PREFIX + "SUBSCRIBE:CONCERN_HIM:" + subscribe.getObjectId()+ ":" + 1;
            String userConcern = Constants.POST_TOKEN_PREFIX + "SUBSCRIBE:HIM_CONCERN:" + account.getUserId()+ ":" + 1;
            redisUtil.del(objectConcern);
            redisUtil.del(userConcern);
            return ServerResponse.createBySuccessMessage("你不再关注Ta了");
        }

        Date nowDate = new Date();
        subscribe.setEnrollDate(nowDate);
        subscribe.setUpdateDate(nowDate);
        subscribe.setId(UUIDUtil.getStringUUID());
        subscribe.setUserId(account.getUserId());
        subscribe.setTypeId(Constants.SubscribeType.USER);

        subscribeMapper.insertSubscribe(subscribe);

        return ServerResponse.createBySuccessMessage("关注成功");
    }

    @Override
    public ServerResponse<PageInfo<AccountVo>> himConcernList(Account account, int pageNumber) {
        if(account == null || account.getUserId() == null){
            throw new TieBaException("参数错误");
        }

        String key = Constants.POST_TOKEN_PREFIX + "SUBSCRIBE:HIM_CONCERN:" + account.getUserId()+ ":" + pageNumber;
        if(redisUtil.hasKey(key)){
            PageInfo<AccountVo> pageInfo = redisUtil.get(key,PageInfo.class);
            return ServerResponse.createBySuccess(pageInfo);
        }

        Page page = PageHelper.startPage(pageNumber, Constants.pageCountSize,true);
        List<Subscribe> subscribeList = subscribeMapper.querySubscribeByUserIdAndTypeId(account.getUserId(), Constants.SubscribeType.USER);
        if(subscribeList.size() == 0){
            throw new TieBaException("Ta还没有关注的用户哦");
        }
        List<String> userIdList = new ArrayList<>(10);
        for(Subscribe subscribe : subscribeList){
            userIdList.add(subscribe.getObjectId());
        }
        ServerResponse<List<AccountVo>> serverResponse = userService.getUserInfoList(userIdList);
        PageInfo<AccountVo> pageInfo = new PageInfo<>(page);
        pageInfo.setList(serverResponse.getData());

        redisUtil.set(key, 60, pageInfo);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<PageInfo<AccountVo>> concernHimList(Account account, int pageNumber) {
        if(account == null || account.getUserId() == null){
            throw new TieBaException("参数错误");
        }
        String key = Constants.POST_TOKEN_PREFIX + "SUBSCRIBE:CONCERN_HIM:" + account.getUserId()+ ":" + pageNumber;
        if(redisUtil.hasKey(key)){
            PageInfo<AccountVo> pageInfo = redisUtil.get(key, PageInfo.class);
            return ServerResponse.createBySuccess(pageInfo);
        }

        Page page = PageHelper.startPage(pageNumber, Constants.pageCountSize,true);
        List<Subscribe> subscribeList = subscribeMapper.querySubscribeByObjectAndTypeId(account.getUserId(), Constants.SubscribeType.USER);
        if(subscribeList.size() == 0){
            throw new TieBaException("还没有关注Ta的用户哦");
        }
        List<String> userIdList = new ArrayList<>(10);
        for(Subscribe subscribe : subscribeList){
            userIdList.add(subscribe.getUserId());
        }
        ServerResponse<List<AccountVo>> serverResponse = userService.getUserInfoList(userIdList);
        PageInfo<AccountVo> pageInfo = new PageInfo<>(page);
        pageInfo.setList(serverResponse.getData());

        redisUtil.set(key, 60, pageInfo);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private Account getAccount(String sessionId){
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //判断当前session当中是否存在账户号json，若不存在则未登录
        if(sessionMap.get("Account") == null){
            throw new TieBaException("未登录");
        }
        return JSON.parseObject(sessionMap.get("Account"),Account.class);
    }

    //根据模块Id组装对应信息返回
    private List<ModuleVo> moduleVoAssemble(List<Subscribe> subscribeList){
        List<String> subscribeIdList = new ArrayList<String>(subscribeList.size());
        for(Subscribe subscribe : subscribeList){
            subscribeIdList.add(subscribe.getObjectId());
        }

        //根据订阅Id查询模块信息
        List<Module> moduleList = moduleMapper.queryModuleByIdList(subscribeIdList);

        //获取模块的头像Id
        List<String> headPictureIdList = new ArrayList<String>(moduleList.size());
        for (Module module : moduleList){
            headPictureIdList.add(module.getHeadPictureId());
        }
        //根据头像Id查询图片
        List<File> headPictureList =  fileMapper.queryFileListById(headPictureIdList);

        List<ModuleVo> moduleVOList = new ArrayList<ModuleVo>(moduleList.size());

        //计算模块等级并组装信息
        for (int i = 0; i < moduleList.size(); i++ ){
            int level = 1;
            Long exp = moduleList.get(i).getExp();
            String headPictureSrc = null;

            //若当前模块的经验值大于每级经验值所需经验，则计算模块等级，并算出经验值余数
            if(moduleList.get(i).getExp() >= Constants.LEVEL){
                level = (int)( moduleList.get(i).getExp() / Constants.LEVEL);
                exp = moduleList.get(i).getExp() % Constants.LEVEL;
            }

            //循环取出头像路径
            for (int j = 0; j < headPictureList.size(); j++){
                //若模块的头像id与头像id相同，则获取头像路径
                if(moduleList.get(i).getHeadPictureId().equals(headPictureList.get(j).getId())){
                    headPictureSrc = headPictureList.get(j).getSrc();
                    break;
                }
            }

            moduleVOList.add(new ModuleVo(moduleList.get(i).getModuleId(),null,moduleList.get(i).getModuleName(),
                    moduleList.get(i).getIntroduce(),level,exp,headPictureSrc,null,moduleList.get(i).getEnrollDate()));
        }
        return moduleVOList;
    }
}
