package com.hwj.tieba.controller;

import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.service.WeatherService;
import com.hwj.tieba.util.IpAddressUtil;
import com.hwj.tieba.vo.WeatherVo;
import com.hwj.tieba.resp.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class MailController {
    @Autowired
    private WeatherService weatherService;

    @ResponseBody
    @GetMapping(value = "/weather")
    public ServerResponse<WeatherVo> weather(HttpServletRequest request){
        //获取登录的ip地址
        String ipAddress = null;
        try {
            ipAddress = IpAddressUtil.getIpAddress(request);
        } catch (Exception e) {
            throw new TieBaException("登录IP获取异常： "+e.getMessage());
        }
        return weatherService.queryWeather(ipAddress);
    }
}
