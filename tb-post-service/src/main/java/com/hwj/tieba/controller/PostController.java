package com.hwj.tieba.controller;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.BaTypeService;
import com.hwj.tieba.service.SubscribeService;
import com.hwj.tieba.vo.BaTypeVO;
import com.hwj.tieba.vo.BaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PostController {
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private BaTypeService baTypeService;

    @ResponseBody
    @RequestMapping(value = "/uploadImg")
    public String uploadImg(HttpServletRequest request){
        HttpServletRequest httpServletRequest = request;
        System.out.println(httpServletRequest.getParameter("crowd_file_name"));
        System.out.println(httpServletRequest.getParameter("crowd_file_content_type"));
        System.out.println(httpServletRequest.getParameter("crowd_file_path"));
        System.out.println(httpServletRequest.getParameter("crowd_file_md5"));
        System.out.println("请求进来了");
        return "null";
    }

    @ResponseBody
    @RequestMapping(value = "/subscribeBa")
    public ServerResponse<List<BaVO>> subscribeBa(HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.getNowUserSubscribeBa(request.getHeader("SessionId"));
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/baType")
    public ServerResponse<PageInfo<BaTypeVO>> baType(@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber){
        ServerResponse<PageInfo<BaTypeVO>> serverResponse = baTypeService.getBaType(pageNumber);
        return serverResponse;
    }
}
