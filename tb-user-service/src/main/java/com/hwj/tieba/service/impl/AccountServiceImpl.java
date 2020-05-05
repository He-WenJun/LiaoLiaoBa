package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayApiException;
import com.alipay.api.AlipayClient;
import com.alipay.api.DefaultAlipayClient;
import com.alipay.api.request.AlipaySystemOauthTokenRequest;
import com.alipay.api.request.AlipayUserInfoShareRequest;
import com.alipay.api.response.AlipaySystemOauthTokenResponse;
import com.alipay.api.response.AlipayUserInfoShareResponse;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.config.AlipayConfig;
import com.hwj.tieba.dao.AccountInfoMapper;
import com.hwj.tieba.dao.BindingMapper;
import com.hwj.tieba.dao.PunishmentMapper;
import com.hwj.tieba.dto.AccountEnrollDTO;
import com.hwj.tieba.entity.AccountInfo;
import com.hwj.tieba.entity.Binding;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.stream.producer.EnrollMailVerificationProducer;
import com.hwj.tieba.stream.producer.LoginIpAddressProducer;
import com.hwj.tieba.dao.AccountMapper;
import com.hwj.tieba.dto.LoginIPDTO;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Punishment;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountService;
import com.hwj.tieba.util.*;
import com.hwj.tieba.vo.AccountVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountMapper accountMapper;
    @Autowired
    private PunishmentMapper punishmentMapper;
    @Autowired
    private BindingMapper bindingMapper;
    @Autowired
    private AccountInfoMapper accountInfoMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private LoginIpAddressProducer loginIpAddressProducer;
    @Autowired
    private EnrollMailVerificationProducer enrollMailVerificationProducer;

    private Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public ServerResponse<String> login(String accountNumber, String password,String loinVerifyCode ,HttpServletRequest request,HttpServletResponse response) {
        //在请求头中获取网关写入的sessionId
        final String sessionId = request.getHeader("SessionId");

        //获取redis中的session对象
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        String verifyCode = JSON.parseObject(sessionMap.get("LoginVerifyCode"),String.class);

        System.out.println(verifyCode +" "+loinVerifyCode );

        if(StringUtils.isEmpty(verifyCode) || !verifyCode.equals(loinVerifyCode)){
            throw new TieBaException("验证码错误！");
        }
        else if (StringUtils.isEmpty(accountNumber.trim()) || StringUtils.isEmpty(password.trim())){
            throw new TieBaException("登录参数有误!");
        }
        //将密码加密,然后查询账号
        String md5Password = MD5Util.MD5EncodeUtf8(password, Constants.MD5_SALT);
        Account account = accountMapper.queryAccountByAccountAndPassword(accountNumber,md5Password);

        //校验查询结果
        if(StringUtils.isEmpty(account)){
            throw new TieBaException("账号或密码错误！");
        }

        //删除登录验证码
        redisUtil.hdel(sessionId,"LoginVerifyCode");

        //验证账号状态
        return accountVerify(sessionMap,sessionId,account,request,response);
    }

    @Override
    public void verificationCode(HttpServletRequest request,  HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-cache");
        //生成验证码
        String verifyCode = ValidateCodeUtil.generateTextCode(ValidateCodeUtil.TYPE_NUM_LOWER, 4, null);
        //响应验证码图片
        response.setContentType("image/jpeg");
        BufferedImage bim = ValidateCodeUtil.generateImageCode(verifyCode, 90, 30, 5, true, Color.WHITE, null, null);
        try {
            ImageIO.write(bim, "JPEG", response.getOutputStream());
        } catch (IOException e) {
            log.error("响应验证码图片异常： "+e.getMessage());
        }

        //verifyCode = "\""+verifyCode+"\"";
        //在请求头中获取网关写入的sessionId
        //verifyCode = verifyCode+"";
        final String sessionId = request.getHeader("SessionId");
        System.out.println("验证码sessionId:"+sessionId);
        //获取redis中的session对象
        Map<String,String> sessionMap = redisUtil.hget(sessionId);

        //存入验证码
        sessionMap.put("LoginVerifyCode",JSON.toJSONString(verifyCode));
        //将session重新存入redis
        redisUtil.hmset(sessionId,sessionMap);

    }

    @Override
    public ServerResponse<String> enrollHold(Account account, AccountInfo accountInfo) {
        if(StringUtils.isEmpty(account.getEmail()) || StringUtils.isEmpty(account.getUserName()) || StringUtils.isEmpty(account.getPassword())){
            throw new TieBaException("参数有误");
        }
        //验证用户名和邮箱是否已被使用
        Account verificationAccount = new Account();
        verificationAccount.setEmail(account.getEmail());
        verificationAccount.setUserName(account.getUserName());

        List<Account> resultAccountList = accountMapper.queryAccountByInfo(verificationAccount);

        if(!StringUtils.isEmpty(resultAccountList)){
            for (Account resultAccount : resultAccountList ){
                if(resultAccount.getUserName().equals(account.getUserName())){
                    throw new TieBaException("用户名以被使用");
                }else if(resultAccount.getEmail().equals(account.getEmail())){
                    throw new TieBaException("邮箱以被使用");
                }
            }
        }

        //到这里说明注册信息正确，将注册信息存入redis
        //使用md5将密码加密
        account.setPassword(MD5Util.MD5EncodeUtf8(account.getPassword(),Constants.MD5_SALT));
        //生成注册token
        String token = UUIDUtil.getStringUUID();
        //拼接key
        String key_Account = Constants.TOKEN_PREFIX+"ENROLL:"+token+":ACCOUNT";
        //存入redis
        redisUtil.set(key_Account,60*30,account);
        if(accountInfo !=null){
            //拼接key
            String key_AccountInfo = Constants.TOKEN_PREFIX+"ENROLL:"+token+":ACCOUNT_INFO";
            //存入redis
            redisUtil.set(key_AccountInfo,60*30,accountInfo);
        }

        //将注册信息发送到消息中间件
        enrollMailVerificationProducer.sendEnrollMailVerification().send(MessageBuilder.withPayload(new AccountEnrollDTO(token,account)).build());
        //返回成功响应
        return ServerResponse.createBySuccess("我们以向您的邮箱发送邮件，请在30分钟内验证邮件，以激活账号",account.getUserId());
    }

    @Override
    @Transactional
    public ServerResponse<String> enroll(String token) {
        //拼接key
        String key_Account = Constants.TOKEN_PREFIX+"ENROLL:"+token+":ACCOUNT";


        if(!redisUtil.hasKey(key_Account)){
            return ServerResponse.createByErrorMessage("注册信息不存在");
        }

        //取出redis中的账号注册信息
        Account account = redisUtil.get(key_Account,Account.class);
        //删除redis中暂存的注册信息
        redisUtil.del(key_Account);
        //存入注册时间和角色id
        Date nowDate = new Date();
        account.setEnrollDate(nowDate);
        account.setUpdateDate(nowDate);
        account.setRoleId(Constants.RoleType.ORDINARY+"");

        //此时用户绑定了邮箱，实例化一个绑定实例，插入绑定记录表
        Binding binding = new Binding();
        binding.setAccount(account.getEmail());
        binding.setBindingId(UUIDUtil.getStringUUID());
        binding.setBindingType(Constants.BindingType.MAIL);
        binding.setUserId(account.getUserId());
        binding.setEnrollDate(nowDate);
        binding.setUpdateDate(nowDate);


        //初始化账号详细信息
        AccountInfo accountInfo = null;
        //拼接key
        String key_AccountInfo = Constants.TOKEN_PREFIX+"ENROLL:"+token+":ACCOUNT_INFO";
        //若账号详细信息存在则不使用默认信息
        if(redisUtil.hasKey(key_AccountInfo)){
            //取出账号详细信息
            accountInfo = redisUtil.get(key_AccountInfo,AccountInfo.class);
        }else{
            accountInfo = new AccountInfo();
            accountInfo.setHeadPictureId("0031cab2f3234845bfdd41ba7e93de38");
        }

        accountInfo.setUserId(account.getUserId());
        accountInfo.setEnrollDate(account.getEnrollDate());
        accountInfo.setUpdateDate(account.getUpdateDate());
        accountInfo.setExp(0L);

        //插入账号信息
        accountMapper.insertAccount(account);
        //插入邮箱绑定记录
        bindingMapper.insertBinding(binding);
        //插入账号初始信息
        accountInfoMapper.insertAccountInfo(accountInfo);

        return ServerResponse.createBySuccess("注册成功");
    }

    @Override
    public ServerResponse<String> enrollVerification(String userId,String sessionId) {

        Account account = new Account();
        account.setUserId(userId);
        List<Account> accountList = accountMapper.queryAccountByInfo(account);
        if(accountList.size() == 0){
           throw new TieBaException(1,"注册结果验证中...");
        }

        //获取redis中的session对象
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //存入验证码
        sessionMap.put("Account",JSON.toJSONString(accountList.get(0)));
        //将session重新存入redis
        redisUtil.hmset(sessionId,sessionMap);
        return ServerResponse.createBySuccess("注册成功，即将跳转到首页",null);
    }

    @Override
    public ServerResponse zhiFuBaoLogin(HttpServletRequest request,HttpServletResponse response) {
        //使用auth_code换取accessToken，用来获取用户信息
        AlipayClient client_accessToken = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", AlipayConfig.APP_ID, AlipayConfig.APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, "RSA2");
        AlipaySystemOauthTokenRequest request_getAccessToken = new AlipaySystemOauthTokenRequest();
        request_getAccessToken.setCode(request.getParameter("auth_code"));
        request_getAccessToken.setGrantType("authorization_code");
        System.out.println(request.getParameter("auth_code"));
        String accessToken = null;
        try {
            AlipaySystemOauthTokenResponse oauthTokenResponse = client_accessToken.execute(request_getAccessToken);
            System.out.println(oauthTokenResponse.getAccessToken());
            accessToken = oauthTokenResponse.getAccessToken();
        } catch (AlipayApiException e) {
            //处理异常
            throw new TieBaException(e.getErrMsg());
        }

        //获取用户信息
        AlipayClient client_userInfo = new DefaultAlipayClient("https://openapi.alipay.com/gateway.do", AlipayConfig.APP_ID, AlipayConfig.APP_PRIVATE_KEY, "json", AlipayConfig.CHARSET, AlipayConfig.ALIPAY_PUBLIC_KEY, "RSA2");
        AlipayUserInfoShareRequest request_getUserInfo = new AlipayUserInfoShareRequest();
        JSONObject accountJson = null;
        try {
            AlipayUserInfoShareResponse userInfoShareResponse = client_userInfo.execute(request_getUserInfo, accessToken);
            JSONObject jsonObject = JSON.parseObject(userInfoShareResponse.getBody());
            accountJson = JSON.parseObject(jsonObject.getString("alipay_user_info_share_response"));
            System.out.println(jsonObject.toJSONString());
        } catch (AlipayApiException e) {
            throw new TieBaException(e.getErrMsg());
        }

        if(accountJson.getString("user_id") == null){
            throw new TieBaException(accountJson.getString("sub_msg"));
        }

        //若查询到账号，则不是第一次使用支付宝登录，不用注册账号
        Account account = new Account();
        account.setUserId(accountJson.getString("user_id"));
        List<Account> accountList = accountMapper.queryAccountByInfo(account);
        if(accountList.size() > 0){
            final String sessionId = request.getHeader("SessionId");
            //获取redis中的session对象
            Map<String,String> sessionMap = redisUtil.hget(sessionId);
            //验证账号状态
            return accountVerify(sessionMap,sessionId,accountList.get(0),request,response);
        }

        AccountVO accountVO = new AccountVO();
        accountVO.setUserId(accountJson.getString("user_id"));
        accountVO.setUserName(accountJson.getString("nick_name"));
        accountVO.setHeadPictureSrc(accountJson.getString("avatar"));
        //第一次使用支付宝登录，注册账号，返回账号信息
        ServerResponse<AccountVO> serverResponse = ServerResponse.createBySuccess("未注册",accountVO);
        serverResponse.setStatus(1);
        return  serverResponse;
    }

    /**
     * 验证账号状态
     * @param sessionMap redis中的session对象
     * @param sessionId 当前sessionId
     * @param account 登录的账号实例
     * @param request
     * @param response
     * @return 返回验证结果
     */
    private ServerResponse<String> accountVerify(Map<String,String> sessionMap,String sessionId, Account account, HttpServletRequest request,HttpServletResponse response){
        //创建数组存入封禁和冻结的ID
        Integer[] stateIdArray =new Integer[]{Constants.StateType.STATE_BAN,Constants.StateType.STATE_FREEZE};
        Date nowDate = new Date();
        List<Punishment> punishmentList = punishmentMapper.queryPunishmentList(account.getUserId(),stateIdArray,nowDate);

        //遍历检查惩罚类型
        for (Punishment punishment : punishmentList) {
            if(punishment.getStateID() == Constants.StateType.STATE_BAN){
                throw new TieBaException("账号以被封禁");
            }
            else if(punishment.getStateID() == Constants.StateType.STATE_FREEZE){
                throw new TieBaException("账号以被冻结");
            }
        }

        //将账号实例序列化成json字符串，将用户实例存入
        sessionMap.put("Account", JSON.toJSONString(account));
        //将session重新存入redis
        redisUtil.hmset(sessionId,sessionMap);

        //生成登录token，保证账号登录的唯一性，存入redis
        String loginToken = UUIDUtil.getStringUUID();
        String loginNameKey = Constants.TOKEN_PREFIX+"LOGINTNAME:"+account.getUserId();
        redisUtil.set(loginNameKey,Constants.KEY_EXPIRES,loginToken);

        //将key和token存入cookie，为了让网关的过滤器对登录的唯一性进行验证
        Cookie loginNameKeyCookie = new Cookie("LoginNameKey",loginNameKey);
        Cookie loginTokenCookie = new Cookie("LoginToken",loginToken);
        loginNameKeyCookie.setPath("/api/");
        loginTokenCookie.setPath("/api/");
        loginNameKeyCookie.setMaxAge(60*30);
        loginTokenCookie.setMaxAge(60*30);

        response.addCookie(loginNameKeyCookie);
        response.addCookie(loginTokenCookie);

        //获取登录的ip
        String IpAddress = null;
        try {
            IpAddress = IpAddressUtil.getIpAddress(request);
            log.info("登录IP： "+IpAddress);
        } catch (Exception e) {
            log.info("登录IP获取异常： "+e.getMessage());
        }
        //创建LoginIPDto对象，存入当前用户id,登录的IP地址，登录时间，为验证服务提供数据
        LoginIPDTO loginIPDto = new LoginIPDTO(account.getUserId(),IpAddress,nowDate,sessionId);
        //发送到消息中间件中
        loginIpAddressProducer.sendLoginIpMessage().send(MessageBuilder.withPayload(loginIPDto).build());

        return ServerResponse.createBySuccess("登录成功",null);
    }

}
