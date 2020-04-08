package com.hwj.tieba.stream.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface LoginIpAddressConsumer {
    String INPUT_LOGIN_IP_ADDRESS ="input_login_ip_address";

    @Input(INPUT_LOGIN_IP_ADDRESS)
    SubscribableChannel receiveLoginIpMessage();

}
