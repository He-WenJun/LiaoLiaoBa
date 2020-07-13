package com.hwj.tieba.service.impl;

import com.hwj.tieba.dao.FileMapper;
import com.hwj.tieba.entity.File;
import com.hwj.tieba.exception.TieBaException;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.service.FileService;
import com.hwj.tieba.util.UUIDUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class FileServiceImpl implements FileService {
    @Autowired
    private FileMapper fileMapper;

    @Override
    public ServerResponse<String> uploadImage(File image) {
        if(image == null){
            throw new TieBaException("参数有误");
        }
        System.out.println(image.getSrc());
        //拼接储存路径
        image.setSrc(image.getSrc().substring(image.getSrc().indexOf("file")-1));
        //修改后缀
        image.setSuffix(image.getSuffix().substring(image.getSuffix().lastIndexOf("/")+1));
        //生成图片Id
        image.setId(UUIDUtil.getStringUUID());
        //文件类型id,1为图片
        image.setTypeId(1);
        //插入时间
        Date nowDate = new Date();
        image.setEnrollDate(nowDate);
        image.setUpdateDate(nowDate);
        //插入图片
        fileMapper.insertFile(image);

        ServerResponse serverResponse = ServerResponse.createBySuccess("上传成功",image);
        return serverResponse;
    }


}
