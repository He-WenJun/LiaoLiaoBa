package com.hwj.tieba.controller;

import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@RequestMapping
public class VerificationController {
    @RequestMapping(value = "/")
    public void verificationCode(HttpServletRequest request){

    }
}
