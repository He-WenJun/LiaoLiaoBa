package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.AccountInfoMapper;
import com.hwj.tieba.dao.AccountMapper;
import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.AccountInfo;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountInfoService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.AccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AccountInfoServiceImpl implements AccountInfoService {
    @Autowired
    private AccountInfoMapper accountInfoMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<AccountVO> getUserInfo(String sessionId) {
        //获取redis中的session对象
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        Account account = JSON.parseObject(sessionMap.get("Account"),Account.class);
        if(StringUtils.isEmpty(account)){
            throw new TieBaException("未登录");
        }

        String key = Constants.TOKEN_PREFIX+"INFO:"+account.getUserId();

        if(redisUtil.hasKey(key)){
            AccountVO accountVO = redisUtil.get(key,AccountVO.class);
            return ServerResponse.createBySuccess(accountVO);
        }

        AccountInfo resultAccountInfo = accountInfoMapper.queryAccountInfo(account.getUserId());

        List<String> imageIdList = new ArrayList<>(2);
        imageIdList.add(resultAccountInfo.getHeadPictureId());
        imageIdList.add(resultAccountInfo.getBackgroundPictureId());

        List<File> resultImage = fileMapper.queryImageListById(imageIdList);

        //计算账户等级
        int level = 1;
        Long exp = resultAccountInfo.getExp();
        if(resultAccountInfo.getExp() >= Constants.LEVEL){
            level += (int) (resultAccountInfo.getExp() / Constants.LEVEL);
            exp = resultAccountInfo.getExp() % Constants.LEVEL;
        }

        //将信息装入AccountOV中
        AccountVO accountVO = new AccountVO();
        accountVO.setUserName(account.getUserName());
        accountVO.setUserId(account.getUserId());
        accountVO.setExp(exp);
        accountVO.setLevel(level);
        accountVO.setAccountInfo(resultAccountInfo);

        if(resultImage.size() > 1){
            if(resultImage.get(0).getId().equals(resultAccountInfo.getHeadPictureId())){
                accountVO.setHeadPictureSrc(resultImage.get(0).getSrc());
                accountVO.setBackgroundPicture(resultImage.get(1).getSrc());
            }else {
                accountVO.setBackgroundPicture(resultImage.get(0).getSrc());
                accountVO.setHeadPictureSrc(resultImage.get(1).getSrc());
            }
        }else {
            accountVO.setHeadPictureSrc(resultImage.get(0).getSrc());
        }


        redisUtil.set(Constants.TOKEN_PREFIX+"INFO:"+account.getUserId(),60,accountVO);
        return ServerResponse.createBySuccess(accountVO);
    }

    @Override
    public ServerResponse<String> increaseAccountExp(Integer increaseExp, String token, String userId) {
        if(StringUtils.isEmpty(increaseExp) || StringUtils.isEmpty(token) || StringUtils.isEmpty(userId)){
            throw new TieBaException("参数有误");
        }

        String key = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+userId;
        if(redisUtil.hasKey(key)){
            System.out.println( redisUtil.getStr(key).equals(token));
            if(! token.equals(redisUtil.getStr(key))){
                throw new TieBaException("令牌错误");
            }
        }else {
            throw new TieBaException("令牌错误");
        }

        accountInfoMapper.updateAccountExp(increaseExp,userId);

        redisUtil.sDel(key);
        redisUtil.sDel(Constants.TOKEN_PREFIX+"INFO:"+userId);

        return ServerResponse.createBySuccess("账号经验+"+increaseExp,null);
    }

    @Override
    public ServerResponse<List<AccountVO>> getUserInfoList(List<String> userIdList) {
        if(userIdList == null || userIdList.size() == 0){
            throw new TieBaException("参数错误");
        }

        List<Account> accountList = new ArrayList<>();
        for (String userId : userIdList){
            Account account = new Account();
            account.setUserId(userId);
            accountList.add(account);
        }
        List<Account> resultAccountList = accountMapper.queryAccountList(accountList);
        List<AccountInfo> resultAccountInfoList =  accountInfoMapper.queryAccountInfoList(userIdList);

        List<String> imgIdList = new ArrayList<>();
        for (AccountInfo accountInfo : resultAccountInfoList){
            imgIdList.add(accountInfo.getHeadPictureId());
            imgIdList.add(accountInfo.getBackgroundPictureId());
        }
        List<File> resultImageList = fileMapper.queryImageListById(imgIdList);


        List<AccountVO> accountVOList = new ArrayList<>();
        for(int i = 0; i < resultAccountList.size(); i++){
            for (int j = 0; j < resultAccountInfoList.size(); j++) {
                if (resultAccountList.get(i).getUserId().equals(resultAccountInfoList.get(j).getUserId())) {
                    AccountVO accountVO = new AccountVO(resultAccountList.get(i), resultAccountInfoList.get(j));
                    for (int a = 0; a < resultImageList.size(); a++) {
                        if (accountVO.getHeadPictureSrc() != null && accountVO.getBackgroundPicture() != null) {
                            break;
                        }
                        if (resultAccountInfoList.get(j).getHeadPictureId().equals(resultImageList.get(a).getId())) {
                            accountVO.setHeadPictureSrc(resultImageList.get(a).getSrc().substring(resultImageList.get(a).getSrc().indexOf("file") - 1));
                        } else if (resultAccountInfoList.get(j).getBackgroundPictureId().equals(resultImageList.get(a).getId())) {
                            accountVO.setBackgroundPicture(resultImageList.get(a).getSrc().substring(resultImageList.get(a).getSrc().indexOf("file") - 1));
                        }
                    }
                    accountVOList.add(accountVO);
                    break;
                }
            }
        }

        return ServerResponse.createBySuccess(accountVOList);
    }

    @Transactional
    @Override
    public ServerResponse<String> updateUserInfo(AccountInfo accountInfo,  Account account, String sessionId) {
        Map<String, String> sessionMap = redisUtil.hget(sessionId);
        if(sessionMap.get("Account") == null){
            throw new TieBaException("未登录");
        }
        Account logInAccount = JSON.parseObject(sessionMap.get("Account"),Account.class);

        List<Account> accountList = accountMapper.queryAccount(account);
        if(accountList.size() > 0){
            if(! accountList.get(0).getUserId().equals(logInAccount.getUserId())){
                return ServerResponse.createByErrorMessage("用户名以被使用");
            }
        }

        account.setUserId(logInAccount.getUserId());
        accountMapper.updateUserByUserId(account);
        accountInfo.setUserId(logInAccount.getUserId());
        accountInfoMapper.updateAccountInfoByUserId(accountInfo);

        logInAccount.setUserName(account.getUserName());
        sessionMap.put("Account", JSON.toJSONString(logInAccount));
        redisUtil.hmset(sessionId, sessionMap);
        redisUtil.sDel(Constants.TOKEN_PREFIX+"INFO:"+logInAccount.getUserId());
        return ServerResponse.createBySuccessMessage("保存成功");
    }



}
