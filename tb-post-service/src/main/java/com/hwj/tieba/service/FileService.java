package com.hwj.tieba.service;

import com.hwj.tieba.entity.File;
import com.hwj.tieba.resp.ServerResponse;
import org.springframework.stereotype.Service;

public interface FileService {
    /**
     * 上传图片
     * @param image 图片实例
     * @return 上传结果
     */
    ServerResponse<String> uploadImage(File image);
}
