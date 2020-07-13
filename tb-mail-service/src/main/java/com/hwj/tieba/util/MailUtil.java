package com.hwj.tieba.util;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@ConfigurationProperties(prefix = "mail")
@Configuration
public class MailUtil {
    private static String mailHost;
    private static String account;
    private static String authorizationCode;

    public static String getMailHost() {
        return mailHost;
    }

    public static void setMailHost(String mailHost) {
        MailUtil.mailHost = mailHost;
    }

    public static String getAccount() {
        return account;
    }

    public static void setAccount(String account) {
        MailUtil.account = account;
    }

    public static String getAuthorizationCode() {
        return authorizationCode;
    }

    public static void setAuthorizationCode(String authorizationCode) {
        MailUtil.authorizationCode = authorizationCode;
    }

    public static void sendMail(String receiveMail, String title, String content){
        //确定连接连接位置
        Properties  props = new Properties();
        //qq邮箱的SMTP服务器地址
        props.setProperty("mail.host",mailHost);
        //是否进行权限验证
        props.setProperty("mail.smtp.auth","true");

        //确定权限（账号和密码）
        Authenticator authenticator = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account,authorizationCode);
            }
        };

        //获取连接
        Session session = Session.getDefaultInstance(props,authenticator);

        //创建消息
        Message message = new MimeMessage(session);
        try {
            //发送人
            message.setFrom(new InternetAddress(account));
            /**
             *收件人
             * 第一个参数
             *      RecipientType.TO 代表收件人
             *      RecipientType.cc 抄送
             *      RecipientType.Bcc 暗送
             * 第二个参数
             *      收件人地址，可以是个Address{}，用来装抄送或暗送人名单，可以用来群发
             */
            message.setRecipient(Message.RecipientType.TO,new InternetAddress(receiveMail));
            //设置标题
            message.setSubject(title);
            //设置正文
            message.setContent(content,"text/html;charset=UTF-8");
            //发送邮件
            Transport.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

    }
}
