package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.CommentMapper;
import com.hwj.tieba.dao.PostMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Post;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.feign.UserService;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.PostService;
import com.hwj.tieba.util.DateUtil;
import com.hwj.tieba.util.FigureUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.util.UUIDUtil;
import com.hwj.tieba.vo.AccountVo;
import com.hwj.tieba.vo.MessageVo;
import com.hwj.tieba.vo.PostItemVo;
import com.hwj.tieba.vo.PostVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {
    @Autowired
    private PostMapper postMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private UserService userService;


    @Override
    public ServerResponse<PageInfo<PostItemVo>> getPostList(Post searchPost, String pageNumber) {
        if(StringUtils.isEmpty(searchPost) || !FigureUtil.isNumeric(pageNumber)){
            throw new TieBaException("参数有误");
        }

        Page  page = PageHelper.startPage(Integer.valueOf(pageNumber), Constants.pageCountSize, true);
        //查询这个贴模块Id下的帖子
        List<Post> postList = postMapper.queryPostByModuleIdAndPostName(searchPost);
        if(postList.size() == 0){
            return ServerResponse.createByErrorMessage("没有找到帖子");
        }

        //取出用户Id，调用user-service服务，查询发帖人信息
        List<String> userIdList = new ArrayList<>(postList.size());
        for(Post post : postList){
            userIdList.add(post.getUserId());
        }
        ServerResponse<List<AccountVo>> response = userService.getUserInfoList(userIdList);
        if(response.getStatus() !=0 ){
            return ServerResponse.createByErrorMessage("查询发帖人信息时出现异常");
        }
        PageInfo<PostItemVo> pageInfo = new PageInfo(page);
        pageInfo.setList(postAssemble(postList,response.getData()));

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    @Transactional
    public ServerResponse<String> commitPost(Post post, String sessionId) {
        Account account = getAccount(sessionId);
        Date nowDate = new Date();
        post.setEnrollDate(nowDate);
        post.setUpdateDate(nowDate);
        post.setUserId(account.getUserId());
        post.setPostType(1);
        post.setReadCount(0l);
        post.setPostId(UUIDUtil.getStringUUID());
        post.setStateId(Constants.StateType.TAKE_EFFECT);
        postMapper.insertPost(post);

        //删除这个贴模块下的第一页帖子缓存
        redisUtil.del(Constants.POST_TOKEN_PREFIX + post.getModuleId() + ":" +1);
        //删除我的帖子列表缓存
        redisUtil.del(Constants.POST_TOKEN_PREFIX + "MY_POST_NAME" + account.getUserId());
        //拼接用于增加账号经验值的token Key，帮助判断增加账号经验值的请求是不是系统发出的
        String increaseAccountExpKey = Constants.POST_TOKEN_PREFIX+"INCREASE_ACCOUNT_EXP_TOKEN:"+post.getUserId();
        String token = UUIDUtil.getStringUUID();
        redisUtil.setStr(increaseAccountExpKey,60,token);
        //调用user-service服务，为当前账号增加经验值
        ServerResponse serverResponse = userService.increaseAccountExp(20,token,post.getUserId());
        serverResponse.setMsg(serverResponse.getMsg()+"，发帖成功");
        return serverResponse;
    }

    @Override
    public ServerResponse<PostItemVo> getPost(String postId) {
        if(StringUtils.isEmpty(postId)){
            throw new TieBaException("参数错误");
        }
        postMapper.updateReadContentByPostId(postId);
        String key = Constants.POST_TOKEN_PREFIX + "POST_INFO:" + postId;
        if(redisUtil.hasKey(key)){
            PostItemVo postItemVo = redisUtil.get(key, PostItemVo.class);
            return ServerResponse.createBySuccess(postItemVo);
        }

        //查询帖子
        Post resultPost =  postMapper.queryPostByPostId(postId);
        List<String> userIdList = new ArrayList<>();
        userIdList.add(resultPost.getUserId());
        //查询帖子所属的用户账号
        ServerResponse resultServerResponse = userService.getUserInfoList(userIdList);
        PostItemVo postItemVo = new PostItemVo(resultPost,((List<AccountVo>)resultServerResponse.getData()).get(0));
        redisUtil.set(key, Constants.KEY_EXPIRES, postItemVo);
        return ServerResponse.createBySuccess(postItemVo);
    }

    @Override
    public ServerResponse<List<Post>> getMyPostName(String sessionId) {
        Account account = getAccount(sessionId);
        String key = Constants.POST_TOKEN_PREFIX + "MY_POST_NAME" + account.getUserId();
        if(redisUtil.hasKey(key)){
            List<Post> posts = redisUtil.getArray(key, Post.class);
            return ServerResponse.createBySuccess(posts);
        }
        List<Post> posts = postMapper.queryPostNameByUserId(account.getUserId());
        redisUtil.set(key,10, posts);
        return ServerResponse.createBySuccess(posts);
    }

    @Override
    public ServerResponse<String> updatePost(Post post, String sessionId) {
        Account account = getAccount(sessionId);
        post.setUserId(account.getUserId());
        post.setUpdateDate(new Date());
        postMapper.updatePostByPostId(post);
        redisUtil.del(Constants.POST_TOKEN_PREFIX + "MY_POST_NAME" + account.getUserId());
        redisUtil.del(Constants.POST_TOKEN_PREFIX + "POST_INFO:" + post.getPostId());
        return ServerResponse.createBySuccessMessage("帖子已保存修改");
    }

    @Override
    public ServerResponse<PageInfo> getPostListByUserId(Account account, int pageNumber) {
        if(account == null || account.getUserId() == null){
            throw new TieBaException("参数错误");
        }
        String key = Constants.POST_TOKEN_PREFIX + account.getUserId() + ":" + pageNumber;
        if(redisUtil.hasKey(key)){
            PageInfo<PostItemVo> postPageInfo = redisUtil.get(key,PageInfo.class);
            return ServerResponse.createBySuccess(postPageInfo);
        }
        Page page = PageHelper.startPage(pageNumber,Constants.pageCountSize,true);
        List<Post> postList = postMapper.queryPostNameByUserId(account.getUserId());
        if(postList.size() == 0){
            return ServerResponse.createByErrorMessage("Ta还没有发布过帖子哦");
        }

        PageInfo<PostItemVo> pageInfo = new PageInfo(page);
        pageInfo.setList(postAssemble(postList,null));
        redisUtil.set(key, 60, pageInfo);
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<String> moduleAdminDelPost(Post post, String deleteReason) {
        postMapper.updateStateIdByPostId(post, Constants.StateType.LOSE_EFFICACY);
        redisUtil.listRightPush(Constants.POST_TOKEN_PREFIX +"MESSAGE_LIST:" + post.getUserId(),new MessageVo<Post>(UUIDUtil.getStringUUID(),"你的帖子被模块管理员删除了<br/>原因："+deleteReason, post, Constants.MessageType.DELETE_POST, new Date()));
        return ServerResponse.createBySuccessMessage("删除成功");
    }

    @Override
    public ServerResponse<List<MessageVo>> getMessage(String sessionId) {
        Account account = getAccount(sessionId);
        String key = Constants.POST_TOKEN_PREFIX +"MESSAGE_LIST:"+account.getUserId();
        if(!redisUtil.hasKey(key)){
            return ServerResponse.createByErrorMessage("没有新消息哦");
        }

        List<MessageVo> messageVoList = redisUtil.listRange(key,0, -1, MessageVo.class);
        return ServerResponse.createBySuccess(messageVoList);
    }

    @Override
    public ServerResponse<String> delMessage(String sessionId, Long messageIndex) {
        Account account = getAccount(sessionId);
        String key = Constants.POST_TOKEN_PREFIX +"MESSAGE_LIST:"+account.getUserId();
        redisUtil.getRedisTemplate().opsForList().remove(key,0,redisUtil.getRedisTemplate().opsForList().index(key, messageIndex));
        return ServerResponse.createBySuccessMessage("成功删除");
    }

    @Override
    public ServerResponse<PageInfo> getNewestPost(int pageNumber) {
        String key = Constants.POST_TOKEN_PREFIX + ":NEWEST_POST:" + pageNumber;

        if(redisUtil.hasKey(key)){
            PageInfo pageInfo = redisUtil.get(key, PageInfo.class);
            return ServerResponse.createBySuccess(pageInfo);
        }

        Page page = PageHelper.startPage(pageNumber, Constants.pageCountSize, true);
        List<Post> resultPost = postMapper.queryPostByModuleIdAndPostName(new Post());

        //取出用户Id，调用user-service服务，查询发帖人信息
        List<String> userIdList = new ArrayList<>(Constants.pageCountSize);
        for(Post post : resultPost){
            userIdList.add(post.getUserId());
        }
        ServerResponse<List<AccountVo>> response = userService.getUserInfoList(userIdList);
        if(response.getStatus() != 0 ){
            return ServerResponse.createByErrorMessage("查询发帖人信息时出现异常");
        }

        PageInfo<PostItemVo> pageInfo = new PageInfo<>(page);
        pageInfo.setList(postAssemble(resultPost, response.getData()));
        redisUtil.set(key, 10, pageInfo);
        return ServerResponse.createBySuccess(pageInfo);
    }

    private Account getAccount(String sessionId){
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //判断当前session当中是否存在账户号json，若不存在则未登录
        if(sessionMap.get("Account") == null){
            throw new TieBaException("未登录");
        }
        return JSON.parseObject(sessionMap.get("Account"),Account.class);
    }

    //查询帖子的回复时间和评论数量，并组装返回
    private List<PostItemVo> postAssemble(List<Post> postList, List<AccountVo> accountVoList) {
        //取出帖子的Id
        List<String> postIdList = new ArrayList<String>();
        for (Post post : postList) {
            postIdList.add(post.getPostId());
        }
        //查询帖子的最新一条回复的回复时间
        List<Map<String, Object>> latestCommentDateList = commentMapper.latestCommentDate(postIdList);
        //查询帖子中的评论数量
        List<Map<String, Object>> commentCountList = commentMapper.commentCount(postIdList);

        //组装返回数据
        List<PostItemVo> postItemVOList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            PostItemVo postItemVo = new PostItemVo();
            PostVo postVo = new PostVo(postList.get(i));
            for (Map map : latestCommentDateList) {
                if (postList.get(i).getPostId().equals(map.get("postId").toString())) {
                    postVo.setLatestCommentDate(DateUtil.dateInterval(DateUtil.getDistanceTime((Date) map.get("latestCommentDate"), new Date())));
                    break;
                }
            }
            for (Map map : commentCountList) {
                if (postList.get(i).getPostId().equals(map.get("postId").toString())) {
                    postVo.setCommentCount(Long.valueOf(map.get("count").toString()));
                    break;
                }
            }
            postItemVo.setPostVo(postVo);
            postItemVOList.add(postItemVo);
        }

        if(accountVoList != null && accountVoList.size() >0 ){
            for (int i = 0; i < postItemVOList.size(); i++) {
                for (AccountVo accountVo : accountVoList) {
                    if (postItemVOList.get(i).getPostVo().getUserId().equals(accountVo.getUserId())) {
                        postItemVOList.get(i).setAccountVo(accountVo);
                        break;
                    }
                }
            }
        }

        return postItemVOList;
    }
}
