package com.hwj.tieba.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class PostController {

    @ResponseBody
    @RequestMapping("/uploadImg")
    public String uploadImg(HttpServletRequest request){
        HttpServletRequest httpServletRequest = request;
        System.out.println(httpServletRequest.getParameter("crowd_file_name"));
        System.out.println(httpServletRequest.getParameter("crowd_file_content_type"));
        System.out.println(httpServletRequest.getParameter("crowd_file_path"));
        System.out.println(httpServletRequest.getParameter("crowd_file_md5"));
        System.out.println("请求进来了");
        return "null";
    }
}
