package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.github.pagehelper.Page;
import com.pp.dto.request.query.page.PlanCaseQuery;
import com.pp.entity.PlanCaseEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanCaseMapper extends BaseMapper<PlanCaseEntity> {
    int batchInsert(@Param("planCaseList") List<PlanCaseEntity> PlanCaseList);

    int updatePlanCaseByCaseImageId(PlanCaseEntity planCaseEntity);
    int batchUpdatePlanCaseByCaseImageIds(@Param("planCaseEntityList") List<PlanCaseEntity> planCaseEntityList);
    Page<PlanCaseEntity> selectByTestPlanId(Long id);
    Page<PlanCaseEntity> planCasePageLists(@Param("planCaseQuery") PlanCaseQuery planCaseQuery);
    int deleteByPlanId(Long id);
    int deleteByCaseImageId(Long id);
    int getCountByPlan(long planId);


    int updateOwnerId(@Param("originalCaseId") long originalCaseId,@Param("id") long id);
}
