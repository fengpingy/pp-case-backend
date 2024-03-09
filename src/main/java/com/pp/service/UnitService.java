package com.pp.service;

import com.pp.dto.UnitDTO;
import com.pp.dto.response.ModuleTreeDTO;

public interface UnitService {
    /**
     * 新增组织
     * @param unitDTO
     * @return
     */
    Long addUnit(UnitDTO unitDTO);

    /**
     * 根据组织id获取模块树
     * @param unitId
     * @return
     */
    ModuleTreeDTO selectModuleTree(Long unitId);
}
