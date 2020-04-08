package com.hwj.tieba.stream.consumer;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface EnrollMailVerificationConsumer {
    String INPUT_ENROLL_MAIL_VERIFICATION = "input_enroll_mail_verification";

    @Input(INPUT_ENROLL_MAIL_VERIFICATION)
    SubscribableChannel sendEnrollMailVerification();
}
