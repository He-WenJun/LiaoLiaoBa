package com.hwj.tieba.controller;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.Comment;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.entity.Post;
import com.hwj.tieba.entity.Reply;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.*;
import com.hwj.tieba.vo.ModuleTypeVo;
import com.hwj.tieba.vo.ModuleVo;
import com.hwj.tieba.vo.CommentItemVo;
import com.hwj.tieba.vo.PostItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class PostController {
    @Autowired
    private SubscribeService subscribeService;
    @Autowired
    private ModuleTypeService moduleTypeService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;


    @ResponseBody
    @PostMapping(value = "/uploadImg")
    public ServerResponse<String> uploadImg(HttpServletRequest request){
        File image = new File();
        image.setName(request.getParameter("crowd_file_name"));
        image.setSuffix(request.getParameter("crowd_file_content_type"));
        image.setSrc(request.getParameter("crowd_file_path"));
        return fileService.uploadImage(image);
    }

    @ResponseBody
    @GetMapping(value = "/getSubscribeModule")
    public ServerResponse<List<ModuleVo>> getSubscribeBa(HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.getSubscribeModule(request.getHeader("SessionId"));
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/delSubscribe")
    public ServerResponse<String> delSubscribeBa(String objectId, HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.delSubscribe(request.getHeader("SessionId"),objectId);
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/addSubscribeModule")
    public ServerResponse<String> addSubscribeBa(String moduleName, HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.addSubscribeModule(request.getHeader("SessionId"),moduleName);
        return serverResponse;
    }

    @ResponseBody
    @GetMapping(value = "/moduleType")
    public ServerResponse<PageInfo<ModuleTypeVo>> moduleType(@RequestParam(value = "pageNumber", defaultValue = "1") Integer pageNumber){
        ServerResponse<PageInfo<ModuleTypeVo>> serverResponse = moduleTypeService.getBaType(pageNumber);
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/singleModuleSignIn")
    public  ServerResponse<String> moduleSignIn(String moduleId, HttpServletRequest request){
        ServerResponse serverResponse = moduleService.singleModuleSignIn(request.getHeader("SessionId"),moduleId);
        return serverResponse;
    }

    @ResponseBody
    @GetMapping(value = "/verificationSignIn")
    public  ServerResponse<String> verificationSignIn(String moduleId, HttpServletRequest request){
        ServerResponse serverResponse = moduleService.verificationSignIn(request.getHeader("SessionId"),moduleId);
        return serverResponse;
    }

    @ResponseBody
    @GetMapping(value = "/moduleSignIn")
    public  ServerResponse<String> moduleSignIn(HttpServletRequest request){
        ServerResponse serverResponse = moduleService.moduleSignIn(request.getHeader("SessionId"));
        return serverResponse;
    }


    @ResponseBody
    @GetMapping("/moduleList/{typeId}")
    public  ServerResponse<ModuleTypeVo> moduleType(@PathVariable(value = "typeId") String typeId){
        ServerResponse serverResponse = moduleTypeService.getSonType(typeId);
        return serverResponse;
    }

    @GetMapping("/moduleList/{typeId}/{pageNumber}")
    public String moduleList(@PathVariable(value = "typeId") String typeId, @PathVariable(value = "pageNumber") int pageNumber, HttpServletRequest request){
        ServerResponse serverResponse = moduleService.moduleList(pageNumber,typeId);
        request.setAttribute("serverResponse",JSON.toJSONString(serverResponse));
        System.out.println(JSON.toJSONString(serverResponse));
        return "moduleList";
    }

    @GetMapping("/moduleInfo/dispatcher/{Id}")
    public String dispatcherTieBaInfo(@PathVariable("Id") String id, HttpServletRequest request){
        request.setAttribute("serverResponse",ServerResponse.createBySuccess(id));
        return "moduleInfo";
    }

    @ResponseBody
    @GetMapping("/moduleInfo")
    public ServerResponse<ModuleVo> moduleInfo(String id){
        return moduleService.moduleInfo(id);
    }

    @ResponseBody
    @GetMapping("/postList")
    public ServerResponse<PageInfo<PostItemVo>> postList(String moduleId, String pageNumber){
        return postService.getPostList(moduleId, pageNumber);
    }

    @ResponseBody
    @PostMapping("/commitPost")
    public ServerResponse commitPost(Post post, HttpServletRequest request){
        return postService.commitPost(post,request.getHeader("SessionId"));
    }

    @GetMapping("/postInfo/dispatcher/{Id}")
    public String dispatcherPostInfo(@PathVariable("Id") String id, HttpServletRequest request){
        request.setAttribute("serverResponse",ServerResponse.createBySuccess(id));
        return "postInfo";
    }

    @ResponseBody
    @GetMapping("/postInfo")
    public ServerResponse<PostItemVo> post(String postId){
        return postService.getPost(postId);
    }

    @ResponseBody
    @GetMapping("/commentInfo")
    public ServerResponse<PageInfo<CommentItemVo>> comment(@RequestParam(value = "postId") String postId, @RequestParam(value = "pageNumber", defaultValue = "1") int pageNumber){
        return commentService.getComment(postId, pageNumber);
    }

    @ResponseBody
    @PostMapping(value = "/commitComment")
    public ServerResponse<String> commitComment(Comment comment, @RequestParam(value = "uploadIds", required = false ) List<String> uploadIds, HttpServletRequest request){
        return commentService.insertComment(comment, uploadIds, request.getHeader("SessionId"));
    }
    @ResponseBody
    @PostMapping(value = "/commitReply")
    public ServerResponse<String> commitReply(Reply reply,String targetUserId,HttpServletRequest request){
        return commentService.insertReply(reply,targetUserId,request.getHeader("SessionId"));
    }

    @ResponseBody
    @GetMapping("/getMyPostName")
    public ServerResponse<List<Post>> getMyPostName(HttpServletRequest request){
        return postService.getMyPostName(request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping("/updatePost")
    public  ServerResponse<String> updatePost(Post post,HttpServletRequest request){
        System.out.println(post.getPostContent());
        return postService.updatePost(post,request.getHeader("SessionId"));
    }


}
