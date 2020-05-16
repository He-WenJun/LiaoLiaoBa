package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.BaTypeMapper;
import com.hwj.tieba.entity.BaParentType;
import com.hwj.tieba.entity.BaSonType;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.BaTypeService;
import com.hwj.tieba.util.FigureUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.BaTypeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class BaTypeServiceImpl implements BaTypeService {
    @Autowired
    private BaTypeMapper baTypeMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<PageInfo<BaTypeVO>> getBaType(int pageNumber) {
        //组装key
        String key = Constants.POST_TOKEN_PREFIX+"TYPE:"+pageNumber;
        if(redisUtil.hasKey(key)){
           PageInfo<BaTypeVO> pageInfo = redisUtil.get(key,PageInfo.class);
           return ServerResponse.createBySuccess(pageInfo);
        }

        //设置页码和每页显示的条数
        Page<Object> page = PageHelper.startPage(pageNumber, Constants.pageCountSize);
        //查询吧的父类型
        List<BaParentType> baParentTypeList = baTypeMapper.queryBaParentType();

        //取出父类型id
        List<Integer> baSonTypeIdList = new ArrayList<Integer>(baParentTypeList.size());
        for (BaParentType baParentType : baParentTypeList){
            baSonTypeIdList.add(baParentType.getId());
        }

        //根据父类型id查询对应的子类型
        List<BaSonType> baSonTypeList = baTypeMapper.queryBaSonTypeByParentId(baSonTypeIdList);

        //组装BaTypeVo
        List<BaTypeVO> baTypeVoList = new ArrayList<BaTypeVO>(baSonTypeIdList.size());
        for (int i = 0; i < baParentTypeList.size(); i++){
            //子类型过度集合
            List<BaSonType> baSonTypeListTemp = new ArrayList<BaSonType>();
            for (int j = 0; j < baSonTypeList.size(); j++){
                //若父类型id与子类型的父类型id相同，则为一个类型
                if(baParentTypeList.get(i).getId() == baSonTypeList.get(j).getParentId()){
                    //加入子类型过度集合中
                    baSonTypeListTemp.add(baSonTypeList.get(j));
                }
            }
            //父类型和其对应的子类型集合组成一个完整的类型
            baTypeVoList.add(new BaTypeVO(baParentTypeList.get(i),baSonTypeListTemp));
        }

        PageInfo<BaTypeVO> pageInfo = new PageInfo(page);
        pageInfo.setList(baTypeVoList);

        //存入redis
        redisUtil.set(key,500,pageInfo);

        System.out.println("PageInfo："+JSON.toJSONString(ServerResponse.createBySuccess(pageInfo)));
        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<BaTypeVO> getSonType(String sonTypeId) {
        if(StringUtils.isEmpty(sonTypeId) || !FigureUtil.isNumeric(sonTypeId)){
            throw new TieBaException("参数有误");
        }
        String key = Constants.POST_TOKEN_PREFIX +"TYPE:BA_LIST"+sonTypeId;
        if(redisUtil.hasKey(key)){
            BaTypeVO baTypeVO = redisUtil.get(key,BaTypeVO.class);
            return ServerResponse.createBySuccess(baTypeVO);
        }
        //查询这个子类型Id对应的父类型Id
        int parentId = baTypeMapper.queryParentIdBySonTypeId(sonTypeId);
        //查询父类型
        BaParentType baParentType = baTypeMapper.queryParentTypeById(parentId);
        List<Integer> parentIdList = new ArrayList<Integer>();
        parentIdList.add(parentId);
        //查询这个父类型Id下的所有子类型
        List<BaSonType> baSonTypeList = baTypeMapper.queryBaSonTypeByParentId(parentIdList);
        BaTypeVO baTypeVO = new BaTypeVO(baParentType,baSonTypeList);
        redisUtil.set(key,60,baTypeVO);
        return ServerResponse.createBySuccess(baTypeVO);
    }
}
