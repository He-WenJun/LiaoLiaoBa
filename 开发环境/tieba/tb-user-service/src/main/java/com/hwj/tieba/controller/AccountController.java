package com.hwj.tieba.controller;

import com.alibaba.fastjson.JSON;
import com.alipay.api.domain.PageInfo;
import com.hwj.tieba.config.AlipayConfig;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.AccountInfo;
import com.hwj.tieba.entity.OthersSetting;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.AccountInfoService;
import com.hwj.tieba.service.AccountService;
import com.hwj.tieba.service.AccountSetting;
import com.hwj.tieba.service.MenuService;
import com.hwj.tieba.vo.AccountVO;
import com.hwj.tieba.vo.MenuVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    @Autowired
    private AccountSetting accountSetting;

    @ResponseBody
    @PostMapping(value = "/login")
    public ServerResponse<String> login(String accountNumber, String password, String loinVerifyCode, HttpServletRequest request,HttpServletResponse response){
        ServerResponse<String> serverResponse = accountService.login(accountNumber,password,loinVerifyCode,request,response);
        return serverResponse;
    }

    @GetMapping(value = "/verificationCode")
    public void verificationCode(HttpServletRequest request, HttpServletResponse response){
        accountService.verificationCode(request,response);
    }

    @ResponseBody
    @PostMapping(value = "/enrollHold")
    public ServerResponse<String> enrollHold(Account account){
        ServerResponse<String> serverResponse = accountService.enrollHold(account,null);
        System.out.println(JSON.toJSONString(serverResponse));
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/zhiFuBaoEnrollHold")
    public ServerResponse<String> zhiFuBaoEnrollHold(@ModelAttribute("acc") Account account,@ModelAttribute("accIf") AccountInfo accountInfo){
        ServerResponse<String> serverResponse = accountService.enrollHold(account,accountInfo);
        return serverResponse;
    }

    @GetMapping(value = "/enroll")
    public String enroll(String token,String type,HttpServletRequest request){
        ServerResponse<String> serverResponse = accountService.enroll(token);
        request.setAttribute("serverResponse",serverResponse);
        return "login/enrollResult";
    }

    @ResponseBody
    @GetMapping(value = "/enrollVerification")
    public ServerResponse<String> enrollVerification(String userName,HttpServletRequest request){
        ServerResponse<String> serverResponse = accountService.enrollVerification(userName,request.getHeader("SessionId"));
        return serverResponse;
    }

    @ResponseBody
    @GetMapping(value = "/menu")
    public ServerResponse<MenuVO> menu(HttpServletRequest request){
        ServerResponse<MenuVO> serverResponse = menuService.getMenu(request.getHeader("SessionId"));
        return serverResponse;
    }

    @ResponseBody
    @GetMapping(value = "/userInfo")
    public ServerResponse<AccountVO> userInfo(HttpServletRequest request){
        ServerResponse<AccountVO> serverResponse = accountInfoService.getUserInfo(request.getHeader("SessionId"));
        return serverResponse;
    }

    @GetMapping(value = "/zhifubao")
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

    @GetMapping("/zhifubao/back")
    public String zhiFuBaoLoginBack(HttpServletRequest request,HttpServletResponse response){
        ServerResponse serverResponse = accountService.zhiFuBaoLogin(request,response);
        request.setAttribute("serverResponse",serverResponse);
        System.out.println(JSON.toJSONString(serverResponse));
        return "login/zhiFuBaoEnroll";
    }

    @ResponseBody
    @PostMapping(value = "/increaseAccountExp")
    public ServerResponse<String> increaseAccountExp(Integer increaseExp,String token,String userId){
        ServerResponse serverResponse = accountInfoService.increaseAccountExp(increaseExp,token,userId);
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/getUserInfoList")
    public ServerResponse<List<AccountVO>> getUserInfoList(@RequestBody List<String> userIdList){
        ServerResponse serverResponse = accountInfoService.getUserInfoList(userIdList);
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/getAccountList")
    public ServerResponse<List<Account>> getAccountList(@RequestBody Account account){
        return  accountService.getAccountList(account);
    }

    @GetMapping(value = "/dispatcher/userInfo/{userId}")
    public String dispatcherUserInfo(@PathVariable String userId, HttpServletRequest request){
        request.setAttribute("serverResponse",ServerResponse.createBySuccess(userId));
        return "myCenter";
    }

    @ResponseBody
    @PostMapping(value =  "/addOthersUserSetting")
    public ServerResponse<String> addOthersUserSetting(OthersSetting othersSetting, HttpServletRequest request){
        return accountSetting.addOthersUserSetting(othersSetting, request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping(value =  "/getOthersUserSetting")
    public ServerResponse<OthersSetting> getOthersUserSetting(OthersSetting othersSetting, HttpServletRequest request){
        return accountSetting.getOthersUserSetting(othersSetting, request.getHeader("SessionId"));
    }

    @ResponseBody
    @GetMapping(value = "/getSessionId")
    public ServerResponse<String> getSessionId(HttpServletRequest request){
        return ServerResponse.createBySuccess(request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping(value = "/updateUserInfo")
    public ServerResponse<String> updateUserInfo(AccountInfo accountInfo, Account account, HttpServletRequest request){
        return accountInfoService.updateUserInfo(accountInfo, account, request.getHeader("SessionId"));
    }

    @ResponseBody
    @RequestMapping(value = "/updateAccountRoleId", method = RequestMethod.POST)
    public ServerResponse<String> updateAccountRoleId(@RequestBody Account account){
        return accountService.updateAccountRoleId(account);
    }

    @GetMapping(value = "/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response){
        accountService.logout(request, response);
        return "redirect:http://liaoliaoba.com/resources/index.html";
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
