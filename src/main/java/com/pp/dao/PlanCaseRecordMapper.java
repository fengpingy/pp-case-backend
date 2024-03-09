package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.entity.PlanCaseRecordsEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanCaseRecordMapper extends BaseMapper<PlanCaseRecordsEntity> {
    int batchInsertPlanCaseRecord(@Param("planCaseList") List<PlanCaseRecordsEntity> planCaseRecordsEntities);

    List<PlanCaseRecordsEntity> searchPlanCaseRecordsByPlanId(long planId);
}
