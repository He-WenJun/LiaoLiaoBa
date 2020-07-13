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

        //查询用户头像ID和经验值
        AccountInfo resultAccountInfo = accountInfoMapper.queryAccountInfo(account.getUserId());
        //查询出头像路径
        File resultImage = fileMapper.queryImageById(resultAccountInfo.getHeadPictureId());

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
        accountVO.setExp(exp);
        accountVO.setLevel(level);
        accountVO.setHeadPictureSrc(resultImage.getSrc());
        //accountVO.setEnrollDate(account.getEnrollDate());

       /* //若邮箱不为空则进行过滤
        if(!StringUtils.isEmpty(account.getEmail())){
            StringBuffer sbEmail = new StringBuffer(account.getEmail());
            int index = sbEmail.lastIndexOf("@");
            sbEmail.replace(index-5,index-1,"****");
            accountVO.setEmail(sbEmail.toString());
        }*/

        /*//若手机号不为空则也进行过滤
        if(!StringUtils.isEmpty(account.getPhone())){
            StringBuffer sbPhone = new StringBuffer(account.getPhone());
            sbPhone.replace(4,7,"****");
            accountVO.setPhone(sbPhone.toString());
        }

        //将无用信息置空，把账号信息存入redis保存的session对象当中
        account.setPassword(null);
        account.setPhone(null);
        account.setUpdateDate(null);
        account.setEnrollDate(null);*/

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

        redisUtil.del(key);
        redisUtil.del(Constants.TOKEN_PREFIX+"INFO:"+userId);

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


}
