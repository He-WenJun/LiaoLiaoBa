package com.hwj.tieba.stream;

import com.hwj.tieba.dto.AccountEnrollDTO;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.stream.consumer.EnrollMailVerificationConsumer;
import com.hwj.tieba.util.MailUtil;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;

@EnableBinding( EnrollMailVerificationConsumer.class)
public class EnrollMailVerificationListener {

    @StreamListener(EnrollMailVerificationConsumer.INPUT_ENROLL_MAIL_VERIFICATION)
    public void receiveEnrollMailVerification(AccountEnrollDTO accountEnrollDTO){
        //邮件标题
        String title = "激活邮件";
        //邮件内容
        String content ="<!DOCTYPE html>\n" +
                "<html>\n" +
                "\t<head>\n" +
                "\t\t<meta charset=\"UTF-8\">\n" +
                "\t\t<title>激活邮件</title>\n" +
                "\t</head>\n" +
                "\t<body>\t\n" +
                "\t\t<h3>激活邮件</h3>\n" +
                "\t\t<p>您好，点击链接以激活账号<br/>"+"http://localhost/api/user/enroll?token="+accountEnrollDTO.getToken()+"\n" +
                "\t</body>\n" +
                "</html>";

        //发送邮件
        MailUtil.sendMail(accountEnrollDTO.getAccount().getEmail(),title,content);
    }
}
