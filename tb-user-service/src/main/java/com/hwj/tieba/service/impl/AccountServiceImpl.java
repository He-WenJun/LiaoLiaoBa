package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.AccountInfoMapper;
import com.hwj.tieba.dao.BindingMapper;
import com.hwj.tieba.dao.PunishmentMapper;
import com.hwj.tieba.entity.AccountInfo;
import com.hwj.tieba.entity.Binding;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.stream.producer.EnrollMailVerificationProducer;
import com.hwj.tieba.stream.producer.LoginIpAddressProducer;
import com.hwj.tieba.dao.AccountMapper;
import com.hwj.tieba.dto.LoginIPDto;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Punishment;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountService;
import com.hwj.tieba.service.PunishmentService;
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
    public ServerResponse<AccountVO> login(String accountNumber, String password,String loinVerifyCode ,HttpServletRequest request,HttpServletResponse response) {
        //在请求头中获取网关写入的sessionId
        final String sessionId = request.getHeader("SessionId");

        //获取redis中的session对象
        Map<String,String> sessionMap = (Map<String, String>) redisUtil.hget(sessionId);
        String verifyCode = JSON.parseObject(sessionMap.get("LoginVerifyCode"),String.class);

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

        //到达此处说明账号和密码正确,检查此账号的惩罚记录
        //删除登录验证码
        redisUtil.hdel(sessionId,"LoginVerifyCode");
        //创建数组存入封禁和冻结的ID
        Integer[] stateIdArray =new Integer[]{Constants.StateType.STATE_BAN,Constants.StateType.STATE_FREEZE};
        Date nowDate = new Date();
        List<Punishment> punishmentList = punishmentMapper.queryPunishmentList(account.getUserID(),stateIdArray,nowDate);

        //遍历检查惩罚类型
        for (Punishment punishment : punishmentList) {
            if(punishment.getStateID() == Constants.StateType.STATE_BAN){
                throw new TieBaException("账号以被封禁");
            }
            else if(punishment.getStateID() == Constants.StateType.STATE_FREEZE){
                throw new TieBaException("账号以被冻结");
            }
        }

        //将信息装入AccountOV中，过滤敏感信息
        AccountVO accountVO = new AccountVO();
        accountVO.setUserName(account.getUserName());
        accountVO.setEnrollDate(account.getEnrollDate());

        //若邮箱不为空则进行过滤
        if(!StringUtils.isEmpty(account.getEmail())){
            StringBuffer sbEmail = new StringBuffer(account.getEmail());
            int index = sbEmail.lastIndexOf("@");
            sbEmail.replace(index-5,index-1,"****");
            accountVO.setEmail(sbEmail.toString());
        }

        //若手机号不为空则也进行过滤
        if(!StringUtils.isEmpty(account.getPhone())){
            StringBuffer sbPhone = new StringBuffer(account.getPhone());
            sbPhone.replace(4,7,"****");
            accountVO.setPhone(sbPhone.toString());
        }

        //将无用信息置空，把账号信息存入redis保存的session对象当中
        account.setPassword(null);
        account.setPhone(null);
        account.setUpdateDate(null);
        account.setEnrollDate(null);

        //将账号实例序列化成json字符串，将用户实例存入
        sessionMap.put("Account", JSON.toJSONString(account));
        //将session重新存入redis
        redisUtil.hmset(sessionId,sessionMap);

        //生成登录token，保证账号登录的唯一性，存入redis
        String loginToken = UUIDUtil.getStringUUID();
        String loginNameKey = Constants.TOKEN_PREFIX+"LOGINTNAME:"+account.getUserID();
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
        LoginIPDto loginIPDto = new LoginIPDto(account.getUserID(),IpAddress,nowDate,sessionId);
        //发送到消息中间件中
        loginIpAddressProducer.sendLoginIpMessage().send(MessageBuilder.withPayload(loginIPDto).build());

        return ServerResponse.createBySuccess("登录成功",accountVO);
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
        Map<String,String> sessionMap = (Map<String, String>) redisUtil.hget(sessionId);

        //存入验证码
        sessionMap.put("LoginVerifyCode",JSON.toJSONString(verifyCode));
        //将session重新存入redis
        redisUtil.hmset(sessionId,sessionMap);

    }

    @Override
    public ServerResponse enrollHold(Account account) {
        if(StringUtils.isEmpty(account)){
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
        //拼接key
        String key = Constants.TOKEN_PREFIX+"ENROLL:"+account.getUserName();
        //存入redis
        redisUtil.set(key,60*30,account);
        //将注册信息发送到消息中间件
        enrollMailVerificationProducer.sendEnrollMailVerification().send(MessageBuilder.withPayload(account).build());
        //返回成功响应
        return ServerResponse.createBySuccess("我们以向您的邮箱发送邮件，请在30分钟内验证邮件，以激活账号",account.getUserName());
    }

    @Override
    @Transactional
    public ServerResponse enroll(String userName) {
        String key = Constants.TOKEN_PREFIX+"ENROLL:"+userName;
        //取出redis中的账号注册信息
        Account account = redisUtil.get(key,Account.class);
        if(StringUtils.isEmpty(account)){
            return ServerResponse.createByErrorMessage("注册信息不存在");
        }
        //删除redis中暂存的注册信息
        redisUtil.del(key);
        //存入注册时间和用户ID和角色id
        Date nowDate = new Date();
        account.setEnrollDate(nowDate);
        account.setUpdateDate(nowDate);
        account.setUserID(UUIDUtil.getStringUUID());
        account.setRoleID(Constants.RoleType.ORDINARY+"");

        //此时用户绑定了邮箱，实例化一个绑定实例，插入绑定记录表
        Binding binding = new Binding();
        binding.setAccount(account.getEmail());
        binding.setBindingId(UUIDUtil.getStringUUID());
        binding.setBindingType(Constants.BindingType.MAIL);
        binding.setUserId(account.getUserID());
        binding.setEnrollDate(nowDate);
        binding.setUpdateDate(nowDate);

        //初始化账号详细信息
        AccountInfo accountInfo = new AccountInfo();
        accountInfo.setUserId(account.getUserID());
        accountInfo.setEnrollDate(account.getEnrollDate());
        accountInfo.setUpdateDate(account.getUpdateDate());
        accountInfo.setExp(0L);

         accountMapper.insertAccount(account);
         bindingMapper.insertBinding(binding);
         accountInfoMapper.insertAccountInfo(accountInfo);

        return ServerResponse.createBySuccess("注册成功",null);
    }

    @Override
    public ServerResponse enrollVerification(String userName) {
        Account account = new Account();
        account.setUserName(userName);
        List<Account> resultAccountList = accountMapper.queryAccountByInfo(account);
        if(StringUtils.isEmpty(resultAccountList)){
            throw new TieBaException("未查询到账号");
        }
        return ServerResponse.createBySuccess("注册成功",null);
    }
}
