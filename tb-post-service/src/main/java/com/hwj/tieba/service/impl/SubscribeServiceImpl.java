package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.BaMapper;
import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.dao.SubscribeMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.Ba;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.entity.Subscribe;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.SubscribeService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.BaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class SubscribeServiceImpl implements SubscribeService {
    @Autowired
    private SubscribeMapper subscribeMapper;
    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private BaMapper baMapper;


    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<List<BaVO>> getNowUserSubscribeBa(String sessionId) {
        Map<String,String> sessionMap = redisUtil.hget(sessionId);
        //判断当前session当中是否存在账户号json，若不存在则未登录
        if(sessionMap.get("Account") == null){
            throw new TieBaException(2,"未登录");
        }

        Account account = JSON.parseObject(sessionMap.get("Account"),Account.class);

        String key = Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserID();
        if(redisUtil.hasKey(key)){
            return ServerResponse.createBySuccess(redisUtil.getArray(key,BaVO.class));
        }

        //查询当前登录账号的订阅Id
        List<Subscribe> subscribeList = subscribeMapper.quitSubscribeByUserIdAndTypeId(account.getUserID(), Constants.SubscribeType.BA);

        if(subscribeList.size() == 0){
            throw new TieBaException("您还没有订阅的贴吧哦");
        }

        List<String> subscribeIdList = new ArrayList<String>(subscribeList.size());
        for(Subscribe subscribe : subscribeList){
            subscribeIdList.add(subscribe.getObjectId());
        }

        //根据订阅Id查询吧信息
        List<Ba> baList = baMapper.queryBaByIdList(subscribeIdList);

        //获取吧的头像Id
        List<String> headPictureIdList = new ArrayList<String>(baList.size());
        for (Ba ba : baList){
            headPictureIdList.add(ba.getHeadPictureId());
        }
        //根据头像Id查询图片
        List<File> headPictureList =  fileMapper.queryFileListById(headPictureIdList);

        List<BaVO> baVOList = new ArrayList<BaVO>(baList.size());

        //计算吧等级并组装信息
        for (int i = 0; i < baList.size(); i++ ){
            int level = 1;
            Long exp = baList.get(i).getExp();
            String headPictureSrc = null;

            //若当前吧的经验值大于每级经验值所需经验，则计算吧等级，并算出经验值余数
            if(baList.get(i).getExp() >= Constants.LEVEL){
                level = (int)( baList.get(i).getExp() / Constants.LEVEL);
                exp = baList.get(i).getExp() % Constants.LEVEL;
            }

            //循环取出头像路径
            for (int j = 0; j < headPictureList.size(); j++){
                //若吧的头像id与头像id相同，则获取头像路径
                if(baList.get(i).getHeadPictureId().equals(headPictureList.get(j).getId())){
                    headPictureSrc = headPictureList.get(j).getSrc();
                    break;
                }
            }

            baVOList.add(new BaVO(baList.get(i).getBaId(),null,baList.get(i).getBaName(),
                    baList.get(i).getIntroduce(),level,exp,headPictureSrc,null,baList.get(i).getEnrollDate()));
        }


        //将订阅的吧存入redis
        redisUtil.set(Constants.POST_TOKEN_PREFIX+"SUBSCRIBE:"+account.getUserID(),Constants.KEY_EXPIRES,baVOList);

        return ServerResponse.createBySuccess(baVOList);
    }
}
