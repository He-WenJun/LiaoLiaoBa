package com.hwj.tieba.controller;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.config.AlipayConfig;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.AccountInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountInfoService;
import com.hwj.tieba.service.AccountService;
import com.hwj.tieba.service.MenuService;
import com.hwj.tieba.vo.AccountVO;
import com.hwj.tieba.vo.MenuVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

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
    public ServerResponse<String> login(String accountNumber, String password, String loinVerifyCode, HttpServletRequest request,HttpServletResponse response){
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
        ServerResponse<String> serverResponse = accountService.enrollHold(account,null);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/zhiFuBaoEnrollHold",method = RequestMethod.POST)
    public ServerResponse<String> zhiFuBaoEnrollHold(@ModelAttribute("acc") Account account,@ModelAttribute("accIf") AccountInfo accountInfo){
        ServerResponse<String> serverResponse = accountService.enrollHold(account,accountInfo);
        return serverResponse;
    }

    @RequestMapping(value = "/enroll",method = RequestMethod.GET)
    public String enroll(String token,String type,HttpServletRequest request){
        ServerResponse<String> serverResponse = accountService.enroll(token);
        request.setAttribute("serverResponse",serverResponse);
        return "login/enrollResult";
    }

    @ResponseBody
    @RequestMapping(value = "/enrollVerification",method = RequestMethod.GET)
    public ServerResponse<String> enrollVerification(String userName,HttpServletRequest request){
        ServerResponse<String> serverResponse = accountService.enrollVerification(userName,request.getHeader("SessionId"));
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

    @RequestMapping(value = "/zhifubao")
    public String zhiFuBaoLogin(){
        //回调地址必须经encode
        String backUrl = null;
        try {
            backUrl = URLEncoder.encode(AlipayConfig.LOGIN_BACK,"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //重定向到授权页面
        return "redirect:"+ AlipayConfig.ALIPAY_URL+"?app_id=" + AlipayConfig.APP_ID + "&scope=auth_user&redirect_uri=" + backUrl;
    }

    @RequestMapping("/zhifubao/back")
    public String zhiFuBaoLoginBack(HttpServletRequest request,HttpServletResponse response){
        ServerResponse serverResponse = accountService.zhiFuBaoLogin(request,response);
        request.setAttribute("serverResponse",serverResponse);
        System.out.println(JSON.toJSONString(serverResponse));
        return "login/zhiFuBaoEnroll";
    }

    @ResponseBody
    @RequestMapping(value = "/increaseAccountExp", method = RequestMethod.POST)
    public ServerResponse<String> increaseAccountExp(Integer increaseExp,String token,String userId){
        ServerResponse serverResponse = accountInfoService.increaseAccountExp(increaseExp,token,userId);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/getUserInfoList", method = RequestMethod.POST)
    public ServerResponse<String> getUserInfoList(@RequestBody List<String> userIdList){
        ServerResponse serverResponse = accountInfoService.getUserInfoList(userIdList);
        return serverResponse;
    }

    public ServerResponse<List<AccountVO>> getAccountInfoList(List<String> userIdList){
        return null;
    }

    @InitBinder("acc")
    public void accountBinder(WebDataBinder webDataBinder){
        webDataBinder.setFieldDefaultPrefix("a.");
    }
    @InitBinder("accIf")
    public void accountInfoBinder(WebDataBinder webDataBinder){
        webDataBinder.setFieldDefaultPrefix("a_f.");
    }

}
