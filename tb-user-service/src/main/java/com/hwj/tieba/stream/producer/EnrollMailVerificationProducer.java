package com.hwj.tieba.stream.producer;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface EnrollMailVerificationProducer {
    String OUTPUT_ENROLL_MAIL_VERIFICATION = "output_enroll_mail_verification";

    @Output(OUTPUT_ENROLL_MAIL_VERIFICATION)
    MessageChannel sendEnrollMailVerification();
}
