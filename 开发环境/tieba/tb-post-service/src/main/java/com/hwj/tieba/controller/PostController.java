package com.hwj.tieba.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.entity.*;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.*;
import com.hwj.tieba.vo.*;
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
    private ModuleTypeService moduleTypeService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ModuleService moduleService;
    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private PrivateMessageService privateMessageService;
    @Autowired
    private ModuleBlackUserService moduleBlackUserService;
    @Autowired
    private ModuleUserRoleService moduleUserRoleService;


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
    public ServerResponse<List<ModuleVo>> getSubscribeModule(HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.getSubscribeModule(request.getHeader("SessionId"));
        return serverResponse;
    }

    @ResponseBody
    @GetMapping(value = "/getSubscribeModuleByUserId")
    public ServerResponse<List<ModuleVo>> getSubscribeModule(Account account){
        ServerResponse serverResponse = subscribeService.getSubscribeModuleByUserId(account);
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/delSubscribe")
    public ServerResponse<String> delSubscribeModule(String objectId, HttpServletRequest request){
        ServerResponse serverResponse = subscribeService.delSubscribe(request.getHeader("SessionId"),objectId);
        return serverResponse;
    }

    @ResponseBody
    @PostMapping(value = "/addSubscribeModule")
    public ServerResponse<String> addSubscribeModule(String moduleName, HttpServletRequest request){
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
    public String dispatcherTieModuleInfo(@PathVariable("Id") String id, HttpServletRequest request){
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
    public ServerResponse<PageInfo<PostItemVo>> postList(Post post, String pageNumber){
        return postService.getPostList(post, pageNumber);
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
    public ServerResponse<String> commitComment(Comment comment,String targetUserId, @RequestParam(value = "uploadIds", required = false ) List<String> uploadIds, HttpServletRequest request){
        return commentService.insertComment(comment, targetUserId, uploadIds, request.getHeader("SessionId"));
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
    @GetMapping("/getPostListByUserId")
    public ServerResponse<PageInfo> getPostListByUserId(Account account, int pageNumber){
        return postService.getPostListByUserId(account, pageNumber);
    }

    @ResponseBody
    @PostMapping("/updatePost")
    public  ServerResponse<String> updatePost(Post post,HttpServletRequest request){
        return postService.updatePost(post,request.getHeader("SessionId"));
    }

    @ResponseBody
    @GetMapping(value = "/concernHimList")
    public ServerResponse<PageInfo<AccountVo>> concernHimList(Account account, int pageNumber){
        return subscribeService.concernHimList(account, pageNumber);
    }

    @ResponseBody
    @GetMapping(value = "/himConcernList")
    public ServerResponse<PageInfo<AccountVo>> himConcernList(Account account, int pageNumber){
        return subscribeService.himConcernList(account, pageNumber);
    }

    @ResponseBody
    @PostMapping(value = "/addSubscribeUser")
    public ServerResponse<String> addSubscribeUser(Subscribe subscribe, HttpServletRequest request){
        return subscribeService.addSubscribeUser(request.getHeader("SessionId"),subscribe);
    }

    @ResponseBody
    @PostMapping(value = "/addUserGoToPrivateMessageList")
    public ServerResponse<String> addUserGoToPrivateMessageList(Account targetAccount, HttpServletRequest request){
        return privateMessageService.addUserGoToPrivateMessageList(targetAccount.getUserId(), request.getHeader("SessionId"));
    }

    @ResponseBody
    @GetMapping(value = "/getPrivateMessageList")
    public ServerResponse<List<AccountVo>> getPrivateMessageList(HttpServletRequest request){
        return privateMessageService.getPrivateMessageList(request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping(value = "/getOldMessage")
    public ServerResponse<PrivateMessageVo> getOldMessage(Account targetAccount, Long startIndex, HttpServletRequest request){
        return privateMessageService.getOldMessage(targetAccount, startIndex, request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping(value = "/delPrivateMessage")
    public ServerResponse<String> delPrivateMessage(String targetUserId, HttpServletRequest request){
        return privateMessageService.delPrivateMessage(targetUserId, request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping(value = "/moduleAdmin/queryModuleBlackUserList")
    public ServerResponse<PageInfo<ModuleBlackUserVo>> queryModuleBlackUserList(ModuleBlackUser moduleBlackUser, Integer pageNumber, Account searchUserName){
        return moduleBlackUserService.queryModuleBlackUserList(moduleBlackUser, pageNumber, searchUserName);
    }

    @ResponseBody
    @PostMapping(value = "/moduleAdmin/deleteModuleBlackUser")
    public ServerResponse<String> deleteModuleBlackUser (ModuleBlackUser moduleBlackUser){
        return moduleBlackUserService.deleteModuleBlackUser(moduleBlackUser);
    }

    @ResponseBody
    @PostMapping(value = "/moduleAdmin/addModuleBlackUser")
    public ServerResponse<String> addModuleBlackUser (ModuleBlackUser moduleBlackUser, Account searchAccount){
        return moduleBlackUserService.addModuleBlackUser(moduleBlackUser, searchAccount);
    }

    @ResponseBody
    @GetMapping(value = "/getMyManagementModule")
    public ServerResponse<List<ModuleVo>> getMyManagementModule (HttpServletRequest request){
        return moduleUserRoleService.getMyManagementModule(request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping(value = "/moduleAdmin/updateModuleInfo")
    public ServerResponse<String> updateModuleInfo (Module module){
        return moduleService.updateModule(module);
    }

    @ResponseBody
    @PostMapping(value = "/moduleAdmin/deletePost")
    public ServerResponse<String> moduleAdminDelPost(Post post, String deleteReason){
        return postService.moduleAdminDelPost(post,deleteReason);
    }

    @ResponseBody
    @GetMapping(value = "/getMessage")
    public ServerResponse<List<MessageVo>> getMessage(HttpServletRequest request){
        return postService.getMessage(request.getHeader("SessionId"));
    }

    @ResponseBody
    @PostMapping("/delMessage")
    public ServerResponse<String> delMessage(HttpServletRequest request,@RequestParam("messageIndex") Long messageIndex){
        return postService.delMessage(request.getHeader("SessionId"), messageIndex);
    }

    @GetMapping("/search/{postName}/{pageNumber}")
    public String searchPost(Post post, @PathVariable("pageNumber") String pageNumber, HttpServletRequest request){
        ServerResponse serverResponse = postService.getPostList(post, pageNumber);
        request.setAttribute("serverResponse",JSON.toJSONString(serverResponse,SerializerFeature.DisableCircularReferenceDetect));
        return "searchResult";
    }

    @ResponseBody
    @PostMapping("/mkdirModule")
    public ServerResponse<String> mkdirModule(Module module, HttpServletRequest request){
        return moduleService.mkdirModule(module, request.getHeader("SessionId"));
    }

    @ResponseBody
    @GetMapping("/lastMessage")
    public ServerResponse<String> lastMessage(HttpServletRequest request){
        return privateMessageService.lastMessage(request.getHeader("SessionId"));
    }

    @ResponseBody
    @GetMapping("/moduleRanking")
    public ServerResponse<PageInfo<ModuleVo>> moduleRanking(){
        return moduleService.moduleRanking();
    }

    @ResponseBody
    @GetMapping("/getNewestPost")
    public ServerResponse<PageInfo> getNewestPost(Integer pageNumber) {
        return postService.getNewestPost(pageNumber);
    }
}
