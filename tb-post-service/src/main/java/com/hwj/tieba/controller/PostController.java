package com.hwj.tieba.controller;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.BaService;
import com.hwj.tieba.service.BaTypeService;
import com.hwj.tieba.service.FileService;
import com.hwj.tieba.service.SubscribeService;
import com.hwj.tieba.vo.BaTypeVO;
import com.hwj.tieba.vo.BaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PostController {
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private BaTypeService baTypeService;
    @Autowired
    private FileService fileService;
    @Autowired
    private BaService baService;


    @ResponseBody
    @RequestMapping(value = "/uploadImg")
    public ServerResponse<String> uploadImg(HttpServletRequest request){
        File image = new File();
        image.setName(request.getParameter("crowd_file_name"));
        image.setSuffix(request.getParameter("crowd_file_content_type"));
        image.setSrc(request.getParameter("crowd_file_path"));
        return fileService.uploadImage(image);
    }

    @ResponseBody
    @RequestMapping(value = "/getSubscribeBa")
    public ServerResponse<List<BaVO>> getSubscribeBa(HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.getSubscribeBa(request.getHeader("SessionId"));
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/delSubscribe")
    public ServerResponse<String> delSubscribeBa(String objectId, HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.delSubscribe(request.getHeader("SessionId"),objectId);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/addSubscribeBa", method = RequestMethod.POST)
    public ServerResponse<String> addSubscribeBa(String baName, HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.addSubscribeBa(request.getHeader("SessionId"),baName);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/baType")
    public ServerResponse<PageInfo<BaTypeVO>> baType(@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber){
        ServerResponse<PageInfo<BaTypeVO>> serverResponse = baTypeService.getBaType(pageNumber);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/baSignIn")
    public  ServerResponse<String> baSignIn(HttpServletRequest request){
        ServerResponse serverResponse = baService.baSignIn(request.getHeader("SessionId"));
        return serverResponse;
    }
}
