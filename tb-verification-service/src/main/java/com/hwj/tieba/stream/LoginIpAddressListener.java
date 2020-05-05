package com.hwj.tieba.stream;

import com.alibaba.fastjson.JSONObject;
import com.hwj.tieba.dto.LoginIPDTO;
import com.hwj.tieba.entity.LoginLocation;
import com.hwj.tieba.service.LoginLocationService;
import com.hwj.tieba.util.QueryIPUtil;
import com.hwj.tieba.util.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding(value = {com.hwj.tieba.stream.consumer.LoginIpAddressConsumer.class})
public class LoginIpAddressListener {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private LoginLocationService loginLocationService;

    @Autowired
    private QueryIPUtil queryIPUtil;

    @StreamListener(com.hwj.tieba.stream.consumer.LoginIpAddressConsumer.INPUT_LOGIN_IP_ADDRESS)
    public void receiveLoginIpMessage(LoginIPDTO logInMessage){
        //测试ip 110.179.228.16山西太原 120.243.244.245安徽马鞍山 218.195.219.255新疆乌鲁木齐
        logInMessage.setLoginIpAddress("120.243.244.245");

        log.info("verification-service端input_login_ip_address输入通道 : "+logInMessage.toString());
        //查询登录的IP地址所在位置
        String locationJson = queryIPUtil.qureyIP(QueryIPUtil.KEY,logInMessage.getLoginIpAddress(),QueryIPUtil.OUTPUT_TYPE.JSON);
        JSONObject jsonObject = JSONObject.parseObject(locationJson);
        //取出省
        String province = (String) jsonObject.get("province");
        //取出市
        String city = (String) jsonObject.get("city");
        //根据登录信息实例化登录对象
        LoginLocation loginLocation = new LoginLocation(
                UUIDUtil.getStringUUID(),logInMessage.getUserID(),logInMessage.getLoginIpAddress(),
                province,city,logInMessage.getLoginDate(),logInMessage.getLoginDate());

        //对登录位置进行异地验证
        loginLocationService.loginIPVerification(loginLocation,logInMessage.getSessionId());
        //插入登录记录
        loginLocationService.insertLoginIp(loginLocation);
    }
}
