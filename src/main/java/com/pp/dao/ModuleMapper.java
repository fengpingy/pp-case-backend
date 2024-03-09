package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.entity.ModuleEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleMapper extends BaseMapper<ModuleEntity> {

    List<ModuleEntity> selectAllModule();

    int updateBatchById(@Param("moduleList") List<ModuleEntity> moduleList);

    List<ModuleEntity> selectOneParentId(@Param("id") Long id);

    List<ModuleEntity> selectModulesByIds(List<Long> ModulesIds);

}
