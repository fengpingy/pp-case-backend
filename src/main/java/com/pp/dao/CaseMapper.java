package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.dto.request.query.page.CasePageQuery;
import com.pp.entity.CaseEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseMapper extends BaseMapper<CaseEntity> {
    int batchInsert(@Param("caseList") List<CaseEntity> caseList);

    List<CaseEntity> selectPageList(@Param("moduleIdList") List<Long> ids, @Param("CaseQuery") CasePageQuery casePageQuery);

    List<CaseEntity> selectByModuleId(Long id);

    List<CaseEntity> selectByModuleIds(@Param("moduleIdList") List<Long> ids);

    int batchUpdate(@Param("caseList") List<CaseEntity> caseList);

    int deleteCaseByModuleId(@Param("moduleId") long moduleId);


    List<CaseEntity> selectByCaseId(@Param("id") long id);
}
