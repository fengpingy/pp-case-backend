package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.entity.CaseImageEntity;
import org.apache.ibatis.annotations.Param;
import java.util.List;
public interface ImageCaseMapper extends BaseMapper<CaseImageEntity>{
    int batchInsert(@Param("caseList") List<CaseImageEntity> ImageCaseList);
    int batchDelete(@Param("caseList") List<CaseImageEntity> caseImageEntityList);

    List<CaseImageEntity> selectByOriginalCaseId(Long id);
    List<CaseImageEntity> selectBatchByIds(@Param("ids") List<Long> ids);
    int deleteByOriginalCaseId(Long id);
    int batchDeleteByOriginalCaseId(@Param("ids") List<Long> ids);

    int batchUpdateByOriginalCaseId(@Param("caseImageList") List<CaseImageEntity> caseImageList);

    int deleteImageCaseByModuleId(@Param("moduleId") long moduleId);

}
