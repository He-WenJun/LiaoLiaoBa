package com.hwj.tieba.stream.producer;


import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface LoginIpAddressProducer {
    String OUTPUT_LOGIN_IP_ADDRESS ="output_login_ip_address";

    @Output(OUTPUT_LOGIN_IP_ADDRESS)
    MessageChannel sendLoginIpMessage();

}
