package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.CommentMapper;
import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.dao.ReplyMapper;
import com.hwj.tieba.entity.*;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.CommentService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.CommentItemVo;
import com.hwj.tieba.vo.ReplyItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private ReplyMapper replyMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;


    @Override
    public ServerResponse<PageInfo<CommentItemVo>> getComment(String postId, int pageNumber) {
        if(StringUtils.isEmpty(postId)){
            throw new TieBaException("参数错误");
        }

        String key = Constants.POST_TOKEN_PREFIX + "COMMENT:" + postId +":" + pageNumber;
        if(redisUtil.hasKey(key)){
            PageInfo<CommentItemVo> pageInfo = redisUtil.get(key, PageInfo.class);
            return ServerResponse.createBySuccess(pageInfo);
        }

        Page page = PageHelper.startPage(pageNumber, Constants.pageCountSize, true);
        //查询评论
        List<Comment> resultCommentList = commentMapper.queryComment(postId);
        if(resultCommentList.size() == 0){
            throw new TieBaException("没有更多评论哦");
        }
        //查询回复
        List<Reply> resultReplyList = replyMapper.queryReplyListByCommentId(resultCommentList);

        //取出评论Id查询评论中的图片Id
        List<String> commentIdList = new ArrayList<>();
        for(Comment comment : resultCommentList){
            commentIdList.add(comment.getCommentId());
        }
        List<Map<String, Object>> resultImageMapList = fileMapper.queryPostCommentFile(commentIdList);

        //取出图片Id查询图片信息
        List<File> resultImageList = null;
        if(resultImageMapList.size() > 0){
            List<String> imageIdList = new ArrayList<>();
            for(Map imageMap : resultImageMapList){
                imageIdList.add(imageMap.get("fileId").toString());
            }
            resultImageList = fileMapper.queryFileListById(imageIdList);
        }

        //取出评论和回复的用户id,调用userService的接口查询账号的信息
        List<String> userIdList = new ArrayList<>();
        for (Comment comment : resultCommentList){
            userIdList.add(comment.getUserId());
        }
        for(Reply reply : resultReplyList){
            userIdList.add(reply.getUserId());
        }
        ServerResponse<List<AccountVo>> resultServerResponse = userService.getUserInfoList(userIdList);

        //组装返回数据
        List<AccountVo> resultAccountVOList = resultServerResponse.getData();
        List<CommentItemVo> commentVOList = new ArrayList<>();
        for (int i = 0; i < resultCommentList.size(); i++){
            CommentItemVo commentItemVO = new CommentItemVo(resultCommentList.get(i));
             for(int j = 0; j < resultAccountVOList.size(); j++){
                if(resultCommentList.get(i).getUserId().equals(resultAccountVOList.get(j).getUserId())){
                    commentItemVO.setAccountVO(resultAccountVOList.get(j));
                    break;
                }
            }
            List<File> tempImageList = null;
            for(int j = 0; j < resultImageMapList.size(); j++){
                if(resultCommentList.get(i).getCommentId().equals(resultImageMapList.get(j).get("objectId").toString())){
                    if(tempImageList == null){
                        tempImageList = new ArrayList<>();
                    }
                    for(int a = 0; a < resultImageList.size(); a++){
                        if(resultImageMapList.get(j).get("fileId").toString().equals(resultImageList.get(a).getId())){
                            tempImageList.add(resultImageList.get(a));
                        }
                    }
                }
            }
            if(tempImageList != null){
                commentItemVO.setFileList(tempImageList);
            }

            List<ReplyItemVo> replyVOList = null;
            for(int j = 0; j < resultReplyList.size(); j++){
                if(resultCommentList.get(i).getCommentId().equals(resultReplyList.get(j).getCommentId())){
                    ReplyItemVo replyVO = new ReplyItemVo(resultReplyList.get(j));
                    for (int a = 0; a < resultAccountVOList.size(); a++ ){
                        if (resultReplyList.get(j).getUserId().equals(resultAccountVOList.get(a).getUserId())){
                            replyVO.setAccountVO(resultAccountVOList.get(a));
                            break;
                        }
                    }
                    if(replyVOList == null){
                        replyVOList = new ArrayList<>();
                    }
                    replyVOList.add(replyVO);
                }
            }
            if(replyVOList != null){
                commentItemVO.setReply(replyVOList);
            }
            commentVOList.add(commentItemVO);
        }

        PageInfo<CommentItemVo> pageInfo = new PageInfo<>(page);
        pageInfo.setList(commentVOList);
        redisUtil.set(key, Constants.KEY_EXPIRES, pageInfo);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<String> insertComment(Comment comment, List<String> uploadIdList, String sessionId) {
        if(comment == null || comment.getPostId() == null || comment.getContent() == null){
            throw new TieBaException("参数错误");
        }

        Account account = getAccount(sessionId);
        Date nowDate = new Date();
        comment.setEnrollDate(nowDate);
        comment.setUpdateDate(nowDate);
        comment.setCommentId(UUIDUtil.getStringUUID());
        comment.setUserId(account.getUserId());
        //插入评论
        commentMapper.insertComment(comment);

        if(uploadIdList != null && uploadIdList.size() > 0){
            List<PostFileRelation> postFileRelationList = new ArrayList<>();
            for(String uploadId : uploadIdList){
                postFileRelationList.add(new PostFileRelation(UUIDUtil.getStringUUID(), comment.getCommentId(), uploadId, nowDate, nowDate));
            }
            //插入图片
            fileMapper.insertPostCommentFile(postFileRelationList);
        }

        //删除当前评论帖子的评论缓存
        redisUtil.deletes(Constants.POST_TOKEN_PREFIX + "COMMENT:" + comment.getPostId()+":*");

        //拼接用于增加账号经验值的token Key，帮助判断增加账号经验值的请求是不是系统发出的
        String increaseAccountExpKey = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+account.getUserId();
        String token = UUIDUtil.getStringUUID();
        redisUtil.setStr(increaseAccountExpKey,60,token);
        //调用user-service服务，为当前账号增加经验值
        ServerResponse serverResponse = userService.increaseAccountExp(Constants.COMMENT_EXP,token,account.getUserId());
        serverResponse.setMsg("评论成功，"+serverResponse.getMsg());
        return serverResponse;
    }

    @Override
    public ServerResponse insertReply(Reply reply, String targetUserId, String sessionId) {
        Account account = getAccount(sessionId);
        Date nowDate = new Date();
        reply.setEnrollDate(nowDate);
        reply.setUpdateDate(nowDate);
        reply.setUserId(account.getUserId());
        reply.setReplyId(UUIDUtil.getStringUUID());
        replyMapper.insertReply(reply);
        redisUtil.del(Constants.POST_TOKEN_PREFIX + "COMMENT:" + reply.getPostId() +":" + reply.getPageNumber());
        //将新产生的回复id存入redis,以接收回复账号Id为Key
        String key = Constants.POST_TOKEN_PREFIX + "REPLY:" + targetUserId;
        redisUtil.listRightPush(key,reply.getCommentId());
        return ServerResponse.createBySuccess();
    }

    private Account getAccount(String sessionId){
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //判断当前session当中是否存在账户号json，若不存在则未登录
        if(sessionMap.get("Account") == null){
            throw new TieBaException("未登录");
        }
        return JSON.parseObject(sessionMap.get("Account"),Account.class);
    }
}
