package com.hwj.tieba.service;

import com.github.pagehelper.PageInfo;
import com.hwj.tieba.resp.ServerResponse;
import com.hwj.tieba.vo.ModuleTypeVo;

public interface ModuleTypeService {
    /**
     * 获取吧类型
     * @param pageNumber
     * @return
     */
    ServerResponse<PageInfo<ModuleTypeVo>> getBaType(int pageNumber);

    /**
     * 根据子类型Id获取一个父类型下的所有子类型
     * @param sonTypeId 子类型Id
     * @return
     */
    ServerResponse<ModuleTypeVo> getSonType(String sonTypeId);
}
