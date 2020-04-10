package com.hwj.tieba.controller;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountInfoService;
import com.hwj.tieba.service.AccountService;
import com.hwj.tieba.service.MenuService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.AccountVO;
import com.hwj.tieba.vo.MenuVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AccountController {
    private Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountService accountService;
    @Autowired
    private MenuService menuService;
    @Autowired
    private AccountInfoService accountInfoService;

    @ResponseBody
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ServerResponse<String> login(String accountNumber, String password,String loinVerifyCode, HttpServletRequest request,HttpServletResponse response){
        log.info("User-Service端SessionId ："+request.getHeader("SessionId"));
        ServerResponse<String> serverResponse = accountService.login(accountNumber,password,loinVerifyCode,request,response);
        return serverResponse;
    }

    @RequestMapping(value = "/verificationCode")
    public void verificationCode(HttpServletRequest request, HttpServletResponse response){
        accountService.verificationCode(request,response);
    }

    @ResponseBody
    @RequestMapping(value = "/enrollHold",method = RequestMethod.POST)
    public ServerResponse<String> enrollHold(Account account){
        ServerResponse<String> serverResponse = accountService.enrollHold(account);
        return serverResponse;
    }

    @RequestMapping(value = "/enroll",method = RequestMethod.GET)
    public String enroll(String userName,HttpServletRequest request){
        ServerResponse<String> serverResponse = accountService.enroll(userName);
        request.setAttribute("serverResponse",serverResponse);
        return "login/enrollResult";
    }

    @ResponseBody
    @RequestMapping(value = "/enrollVerification",method = RequestMethod.GET)
    public ServerResponse<String> enrollVerification(String userName){
        ServerResponse<String> serverResponse = accountService.enrollVerification(userName);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/menu",method = RequestMethod.GET)
    public ServerResponse<MenuVO> menu(HttpServletRequest request){
        ServerResponse<MenuVO> serverResponse = menuService.getMenu(request.getHeader("SessionId"));
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/userInfo",method = RequestMethod.GET)
    public ServerResponse<AccountVO> userInfo(HttpServletRequest request){
        ServerResponse<AccountVO> serverResponse = accountInfoService.getUserInfo(request.getHeader("SessionId"));
        return serverResponse;
    }
}
