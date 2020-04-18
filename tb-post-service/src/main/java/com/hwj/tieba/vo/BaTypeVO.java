package com.hwj.tieba.vo;

import com.hwj.tieba.entity.BaParentType;
import com.hwj.tieba.entity.BaSonType;

import java.util.List;

public class BaTypeVO {
    private BaParentType baParentType;
    private List<BaSonType> baSonTypeList;

    public BaTypeVO(BaParentType baParentType, List<BaSonType> baSonTypeList) {
        this.baParentType = baParentType;
        this.baSonTypeList = baSonTypeList;
    }

    public BaParentType getBaParentType() {
        return baParentType;
    }

    public void setBaParentType(BaParentType baParentType) {
        this.baParentType = baParentType;
    }

    public List<BaSonType> getBaSonTypeList() {
        return baSonTypeList;
    }

    public void setBaSonTypeList(List<BaSonType> baSonTypeList) {
        this.baSonTypeList = baSonTypeList;
    }
}
