package com.hwj.tieba.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.*;
import com.hwj.tieba.vo.BaTypeVO;
import com.hwj.tieba.vo.BaVO;
import com.hwj.tieba.vo.PostItemVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

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
    @Autowired
    private PostService postService;


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
    @RequestMapping(value = "/singleBaSignIn")
    public  ServerResponse<String> baSignIn(String baId, HttpServletRequest request){
        ServerResponse serverResponse = baService.singleBaSignIn(request.getHeader("SessionId"),baId);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/verificationSignIn")
    public  ServerResponse<String> verificationSignIn(String baId, HttpServletRequest request){
        ServerResponse serverResponse = baService.verificationSignIn(request.getHeader("SessionId"),baId);
        return serverResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/baSignIn")
    public  ServerResponse<String> baSignIn(HttpServletRequest request){
        ServerResponse serverResponse = baService.baSignIn(request.getHeader("SessionId"));
        return serverResponse;
    }


    @ResponseBody
    @RequestMapping("/baList/{typeId}")
    public  ServerResponse<BaTypeVO> baType(@PathVariable(value = "typeId") String typeId){
        ServerResponse serverResponse = baTypeService.getSonType(typeId);
        return serverResponse;
    }

    @RequestMapping("/baList/{typeId}/{pageNumber}")
    public String baList(@PathVariable(value = "typeId") String typeId, @PathVariable(value = "pageNumber") int pageNumber, HttpServletRequest request){
        ServerResponse serverResponse = baService.baList(pageNumber,typeId);
        request.setAttribute("serverResponse",JSON.toJSONString(serverResponse));
        System.out.println(JSON.toJSONString(serverResponse));
        return "tieBaList";
    }

    @RequestMapping("/tieBaInfo/dispatcher/{Id}")
    public String dispatcherTieBaInfo(@PathVariable("Id") String Id, HttpServletRequest request){
        request.setAttribute("serverResponse",ServerResponse.createBySuccess(Id));
        return "tiebaInfo";
    }

    @ResponseBody
    @RequestMapping("/tieBaInfo")
    public ServerResponse<BaVO> tieBaInfo(String id){
        return baService.baInfo(id);
    }

    @ResponseBody
    @RequestMapping("/postList")
    public  ServerResponse<PageInfo<PostItemVO>> postList(String baId, String pageNumber){
        return postService.getPost(baId, pageNumber);
    }

}
