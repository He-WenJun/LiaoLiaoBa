package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Objects;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.LoginLocationMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.LoginLocation;
import com.hwj.tieba.service.LoginLocationService;
import com.hwj.tieba.util.MailUtil;
import com.hwj.tieba.util.RedisUtil;
import org.apache.http.client.utils.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.hwj.tieba.util.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class LoginLocationServiceImpl implements LoginLocationService {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private LoginLocationMapper loginIpMapper;

    @Autowired
    private RedisUtil redisUtil;


    /*进行异地检查
     *如果用户在最近一个月时间中，有过在当前位置登录，则不算作异地登录
     *如果在最近一个月中没有在当前位置登录，则寻找出一年内登录次数最多的位置，若不为当前位置，则作为异地登录
     * */
    @Override
    public void loginIPVerification(LoginLocation loginLocation,String sessionId) {

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
        //最近一年
        Date oneYeardate = null;
        //最近一个月
        Date oneMonthDate = null;
        try {
            oneYeardate = sf.parse(DateUtil.getOneYear());
            oneMonthDate = sf.parse(DateUtil.getOneMonth());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //查询用户近一年登录记录
        List<LoginLocation> loginLocationList = loginIpMapper.queryLoginIpList(loginLocation.getUserID(),oneYeardate,loginLocation.getEnrollDate());
        System.out.println(loginLocationList.size());

        if(loginLocationList.size() > 0){
            for (int i =0; i<loginLocationList.size(); i++){
                //判断登录时间是否是最近一个月之内
                if(oneMonthDate.before(loginLocationList.get(i).getEnrollDate())){
                    //判断登录省市是否相同
                    if(loginLocationList.get(i).getProvince().equals(loginLocation.getProvince()) &&
                            loginLocationList.get(i).getCity().equals(loginLocation.getCity())){
                         log.info("不算作异地");
                         return;
                    }
                }
            }

            //将loginLocations集合中的数据存入loginLocationSet，进行去重
            Set<LoginLocation> loginLocationSet = new HashSet<LoginLocation>();
            for (int i = 0; i<loginLocationList.size(); i++){
                loginLocationSet.add(loginLocationList.get(i));
            }

            //将loginLocationSet存入Object数组中
            Object[] loginLocationArray = loginLocationSet.toArray();
            int[] countArray = new int[loginLocationArray.length];
            //最频繁登录位置的登录次数
            int max = 0;

            for(int i = 0; i<loginLocationArray.length; i++) {
                int count = 0;
                for (int j = 0; j < loginLocationList.size(); j++) {
                    if (loginLocationArray[i].equals(loginLocationList.get(j))) {
                        count++;
                    }
                }
                //此时countArray数组的下标与loginLocationArray一一对应，记录当前循环位置的登录次数
                countArray[i] = count;
                //若最频繁登录位置的登录次数小于当前位置登录次数，则取而代之
                if (max < count) {
                    max = count;
                }
            }
            System.out.println("max = "+max);
            boolean abnormal = false;
            //寻找countArray数组中的登录次数与max相同的下标，这个下标就是登录次数最多的登录实例
            for (int i=0; i<countArray.length;i++){
                if(countArray[i] == max){
                    LoginLocation frequentlyLoginLocation = (LoginLocation)loginLocationArray[i];
                    //判断当前登录实例与最频繁的登录实例的登录省市是否相同，不同则视为异地登录
                    if(!frequentlyLoginLocation.getProvince().equals(loginLocation.getProvince()) ||
                            !frequentlyLoginLocation.getCity().equals(loginLocation.getCity())){
                        abnormal = true;
                    }
                }
            }

            //若结果为异地，则从redis中取出用户信息，发送提醒邮件
            if(abnormal){
                log.info("异地登录，已发送提醒邮件");
                //获取session对象
                Map<String,String> sessionMap =(Map<String, String>) redisUtil.hget(sessionId);
                //获取当前登录账号实例,包含用户绑定的邮箱账号
                Account account = JSON.parseObject(sessionMap.get(loginLocation.getUserID()),Account.class);
                //邮件标题
                String title = "异地登录提醒";
                //邮件内容
                String content ="<!DOCTYPE html>\n" +
                        "<html>\n" +
                        "\t<head>\n" +
                        "\t\t<meta charset=\"UTF-8\">\n" +
                        "\t\t<title>异地登录提醒</title>\n" +
                        "\t</head>\n" +
                        "\t<body>\t\n" +
                        "\t\t<h3>异地登录提醒</h3>\n" +
                        "\t\t<p>您好，您的账号&nbsp;"+account.getUserName()+"&nbsp;在"+loginLocation.getProvince()+"&nbsp;"+loginLocation.getCity()+"登录，IP为&nbsp;"+loginLocation.getiPAddress()+",请注意账号安全！</p>\n" +
                        "\t</body>\n" +
                        "</html>";

                //发送邮件
                MailUtil.sendMail(account.getEmail(),title,content);
            }
        }
    }

    @Override
    public int insertLoginIp(LoginLocation loginLocation) {
        return loginIpMapper.insertLoginIp(loginLocation);
    }


    public static void main(String[] args) throws ParseException {

    }
}
