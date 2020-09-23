package com.hwj.tieba.service.impl;

import com.alibaba.fastjson.JSON;
import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.dao.ModuleMapper;
import com.hwj.tieba.dao.ModuleUserRoleMapper;
import com.hwj.tieba.entity.Account;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.entity.Module;
import com.hwj.tieba.entity.ModuleUserRole;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.ModuleUserRoleService;
import com.hwj.tieba.util.RedisUtil;
import com.hwj.tieba.vo.ModuleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ModuleUserRoleServiceImpl implements ModuleUserRoleService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ModuleUserRoleMapper moduleUserRoleMapper;
    @Autowired
    private ModuleMapper moduleMapper;
    @Autowired
    private FileMapper fileMapper;

    @Override
    public ServerResponse<List<ModuleVo>> getMyManagementModule(String sessionId) {
        Account account = getAccount(sessionId);
        //查询出当前登录账号管理的模块
        List<ModuleUserRole> resultModuleUserRoleList = moduleUserRoleMapper.queryModuleIdByUserId(new ModuleUserRole(account.getUserId()));
        if(resultModuleUserRoleList.size() == 0){
            return ServerResponse.createByErrorMessage("你没有可以管理的模块");
        }
        //取出模块Id，查询模块信息
        List<String> moduleIdList = new ArrayList<>(resultModuleUserRoleList.size());
        for(int i = 0; i < resultModuleUserRoleList.size(); i++){
            moduleIdList.add(resultModuleUserRoleList.get(i).getModuleId());
        }

        List<Module> resultModuleList = moduleMapper.queryModuleByIdList(moduleIdList);

        //取出图片Id
        List<String> imageId = new ArrayList<>(resultModuleList.size());
        for(Module module : resultModuleList){
            imageId.add(module.getHeadPictureId());
            imageId.add(module.getBackgroundPictureId());
        }
        //查询模块对应的头像图片路径,和背景图片路径
        List<File> imageList = fileMapper.queryFileListById(imageId);

        List<ModuleVo> moduleVoList = new ArrayList<>(resultModuleList.size());
        for(int i = 0; i < resultModuleList.size(); i++){
            //组装返回数据
            ModuleVo moduleVo = new ModuleVo();
            moduleVo.setModuleId(resultModuleList.get(i).getModuleId());
            moduleVo.setIntroduce(resultModuleList.get(i).getIntroduce());
            moduleVo.setModuleName(resultModuleList.get(i).getModuleName());
            for(int j = 0; j < imageList.size(); j++){
                String path = imageList.get(j).getSrc().substring(imageList.get(j).getSrc().indexOf("file") - 1);
                if(resultModuleList.get(i).getHeadPictureId().equals(imageList.get(j).getId())){
                    moduleVo.setHeadPictureId(imageList.get(j).getId());
                    moduleVo.setHeadPictureSrc(path);
                }else if(resultModuleList.get(i).getBackgroundPictureId().equals(imageList.get(j).getId())){
                    moduleVo.setModuleBackgroundPictureId(imageList.get(j).getId());
                    moduleVo.setModuleBackgroundPictureSrc(path);
                }
            }
            moduleVoList.add(moduleVo);
        }
        return ServerResponse.createBySuccess(moduleVoList);
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
