package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.BaTypeVO;

public interface BaTypeService {
    /**
     * 获取吧类型
     * @param pageNumber
     * @return
     */
    ServerResponse<PageInfo<BaTypeVO>> getBaType(Integer pageNumber);
}
