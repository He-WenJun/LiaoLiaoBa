package com.hwj.tieba.vo;

import com.hwj.tieba.entity.ModuleParentType;
import com.hwj.tieba.entity.ModuleSonType;

import java.util.List;

public class ModuleTypeVo {
    private ModuleParentType moduleParentType;
    private List<ModuleSonType> moduleSonTypeList;

    public ModuleTypeVo(ModuleParentType moduleParentType, List<ModuleSonType> moduleSonTypeList) {
        this.moduleParentType = moduleParentType;
        this.moduleSonTypeList = moduleSonTypeList;
    }

    public ModuleParentType getModuleParentType() {
        return moduleParentType;
    }

    public void setModuleParentType(ModuleParentType moduleParentType) {
        this.moduleParentType = moduleParentType;
    }

    public List<ModuleSonType> getModuleSonTypeList() {
        return moduleSonTypeList;
    }

    public void setModuleSonTypeList(List<ModuleSonType> moduleSonTypeList) {
        this.moduleSonTypeList = moduleSonTypeList;
    }
}
