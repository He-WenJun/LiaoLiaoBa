package com.hwj.tieba.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.CommentMapper;
import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.dao.PostMapper;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.entity.Post;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.PostService;
import com.hwj.tieba.util.DateUtil;
import com.hwj.tieba.util.FigureUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.PostItemVO;
import com.hwj.tieba.vo.PostVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
    private FileMapper fileMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<PageInfo<PostItemVO>> getPost(String baId, String pageNumber) {
        if(StringUtils.isEmpty(baId) || !FigureUtil.isNumeric(pageNumber)){
            throw new TieBaException("参数有误");
        }
        String key = Constants.POST_TOKEN_PREFIX + baId + ":" +pageNumber;
        if(redisUtil.hasKey(key)){
            PageInfo<PostItemVO> pageInfo = redisUtil.get(key,PageInfo.class);
            return ServerResponse.createBySuccess(pageInfo);
        }
        Page  page = PageHelper.startPage(Integer.valueOf(pageNumber), Constants.pageCountSize, true);
        //查询这个贴吧Id下的帖子
        List<Post> postList = postMapper.queryPostByBaId(baId);
        if(postList.size() == 0){
            throw new TieBaException("没有更多帖子啦");
        }
        //取出帖子的Id
        List<String> postIdList = new ArrayList<String>();
        for (Post post : postList){
            postIdList.add(post.getPostId());
        }
        //查询帖子的最新一条回复的回复时间
        List<Map<String, Object>> latestCommentDateList = commentMapper.latestCommentDate(postIdList);
        //查询帖子包含的图片Id
        List<Map<String,Object>> imageMapList = fileMapper.queryPostCommentFile(postIdList);
        //查询帖子中的评论数量
        List<Map<String,Object>> commentCountList = commentMapper.commentCount(postIdList);

        //取出图片Id
        List<String> imageIdList = new ArrayList<String>();
        for(Map map : imageMapList){
            imageIdList.add(map.get("fileId").toString());
        }

        //查询图片信息
        List<File> fileList = new ArrayList<>();
        if(imageIdList.size() > 0){
            fileList  = fileMapper.queryFileListById(imageIdList);
        }

        //组装返回数据
        List<PostItemVO> postItemVOList = new ArrayList<>();
        for(int i = 0; i < postList.size(); i++){
            PostItemVO postItemVO = new PostItemVO();
            PostVO postVO = new PostVO(postList.get(i));
            for(Map map : latestCommentDateList){
                if(postList.get(i).getPostId().equals(map.get("postId").toString())){
                    postVO.setLatestCommentDate(DateUtil.dateInterval(DateUtil.getDistanceTime((Date) map.get("latestCommentDate"),new Date())));
                    break;
                }
            }
            for (Map map : commentCountList){
                if(postList.get(i).getPostId().equals(map.get("postId").toString())){
                    postVO.setCommentCount(Long.valueOf(map.get("count").toString()));
                    break;
                }
            }
            for(Map map : imageMapList){
                if(postList.get(i).getPostId().equals(map.get("objectId").toString())){
                    List<File> tempImageFileList = new ArrayList<>();
                    for (File file : fileList){
                        if(map.get("fileId").toString().equals(file.getId())){
                            file.setSrc(file.getSrc().substring(file.getSuffix().indexOf("file")-1));
                            tempImageFileList.add(file);
                        }
                    }
                    postItemVO.setFileList(tempImageFileList);
                }
            }
            postItemVO.setPostVO(postVO);
            postItemVOList.add(postItemVO);
        }
        PageInfo<PostItemVO> pageInfo = new PageInfo(page);
        pageInfo.setList(postItemVOList);
        redisUtil.set(key, Constants.KEY_EXPIRES, pageInfo);
        return ServerResponse.createBySuccess(pageInfo);
    }

    public static void main(String[] args) throws InterruptedException {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date nowDate = new Date();
        //Thread.sleep(5000);
        try {
            Date date = simpleDateFormat.parse("2020-5-5 21:00:00");
            System.out.println(DateUtil.dateInterval(DateUtil.getDistanceTime(date,new Date())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(731/24);
    }
}
