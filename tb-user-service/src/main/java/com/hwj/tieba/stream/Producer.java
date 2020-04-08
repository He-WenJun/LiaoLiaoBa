package com.hwj.tieba.stream;

import com.hwj.tieba.stream.producer.EnrollMailVerificationProducer;
import com.hwj.tieba.stream.producer.LoginIpAddressProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;

@EnableBinding(value = {LoginIpAddressProducer.class, EnrollMailVerificationProducer.class})
public class Producer {


}
