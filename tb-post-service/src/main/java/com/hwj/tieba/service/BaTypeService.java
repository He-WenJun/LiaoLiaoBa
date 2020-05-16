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
    ServerResponse<PageInfo<BaTypeVO>> getBaType(int pageNumber);

    /**
     * 根据子类型Id获取一个父类型下的所有子类型
     * @param sonTypeId 子类型Id
     * @return
     */
    ServerResponse<BaTypeVO> getSonType(String sonTypeId);
}
