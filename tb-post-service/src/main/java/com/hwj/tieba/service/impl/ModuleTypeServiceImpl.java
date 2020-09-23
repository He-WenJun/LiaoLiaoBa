package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.hwj.tieba.common.Constants;
import com.hwj.tieba.dao.ModuleTypeMapper;
import com.hwj.tieba.entity.ModuleParentType;
import com.hwj.tieba.entity.ModuleSonType;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.ModuleTypeService;
import com.hwj.tieba.util.FigureUtil;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.ModuleTypeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
public class ModuleTypeServiceImpl implements ModuleTypeService {
    @Autowired
    private ModuleTypeMapper moduleTypeMapper;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public ServerResponse<PageInfo<ModuleTypeVo>> getBaType(int pageNumber) {
        //组装key
        String key = Constants.POST_TOKEN_PREFIX+"TYPE:"+pageNumber;
        if(redisUtil.hasKey(key)){
           PageInfo<ModuleTypeVo> pageInfo = redisUtil.get(key,PageInfo.class);
           return ServerResponse.createBySuccess(pageInfo);
        }

        //设置页码和每页显示的条数
        Page<Object> page = PageHelper.startPage(pageNumber, Constants.pageCountSize);
        //查询模块的父类型
        List<ModuleParentType> moduleParentTypeList = moduleTypeMapper.queryBaParentType();

        //取出父类型id
        List<Integer> moduleSonTypeIdList = new ArrayList<Integer>(moduleParentTypeList.size());
        for (ModuleParentType moduleParentType : moduleParentTypeList){
            moduleSonTypeIdList.add(moduleParentType.getId());
        }

        //根据父类型id查询对应的子类型
        List<ModuleSonType> moduleSonTypeList = moduleTypeMapper.queryBaSonTypeByParentId(moduleSonTypeIdList);

        //组装BaTypeVo
        List<ModuleTypeVo> moduleTypeVoList = new ArrayList<ModuleTypeVo>(moduleSonTypeIdList.size());
        for (int i = 0; i < moduleParentTypeList.size(); i++){
            //子类型过度集合
            List<ModuleSonType> moduleSonTypeListTemp = new ArrayList<ModuleSonType>();
            for (int j = 0; j < moduleSonTypeList.size(); j++){
                //若父类型id与子类型的父类型id相同，则为一个类型
                if(moduleParentTypeList.get(i).getId() == moduleSonTypeList.get(j).getParentId()){
                    //加入子类型过度集合中
                    moduleSonTypeListTemp.add(moduleSonTypeList.get(j));
                }
            }
            //父类型和其对应的子类型集合组成一个完整的类型
            moduleTypeVoList.add(new ModuleTypeVo(moduleParentTypeList.get(i),moduleSonTypeListTemp));
        }

        PageInfo<ModuleTypeVo> pageInfo = new PageInfo(page);
        pageInfo.setList(moduleTypeVoList);

        //存入redis
        redisUtil.set(key,500,pageInfo);

        return ServerResponse.createBySuccess(pageInfo);
    }

    @Override
    public ServerResponse<ModuleTypeVo> getSonType(String sonTypeId) {
        if(StringUtils.isEmpty(sonTypeId) || !FigureUtil.isNumeric(sonTypeId)){
            throw new TieBaException("参数有误");
        }
        String key = Constants.POST_TOKEN_PREFIX +"TYPE:BA_LIST"+sonTypeId;
        if(redisUtil.hasKey(key)){
            ModuleTypeVo moduleTypeVo = redisUtil.get(key, ModuleTypeVo.class);
            return ServerResponse.createBySuccess(moduleTypeVo);
        }
        //查询这个子类型Id对应的父类型Id
        int parentId = moduleTypeMapper.queryParentIdBySonTypeId(sonTypeId);
        //查询父类型
        ModuleParentType moduleParentType = moduleTypeMapper.queryParentTypeById(parentId);
        List<Integer> parentIdList = new ArrayList<Integer>();
        parentIdList.add(parentId);
        //查询这个父类型Id下的所有子类型
        List<ModuleSonType> moduleSonTypeList = moduleTypeMapper.queryBaSonTypeByParentId(parentIdList);
        ModuleTypeVo moduleTypeVo = new ModuleTypeVo(moduleParentType,moduleSonTypeList);
        redisUtil.set(key,60,moduleTypeVo);
        return ServerResponse.createBySuccess(moduleTypeVo);
    }
}
