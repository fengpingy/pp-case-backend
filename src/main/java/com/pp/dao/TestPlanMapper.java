package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.dto.request.TestPlanStatisticsDTO;
import com.pp.dto.request.TimeDTO;
import com.pp.dto.request.query.page.TestPlanQuery;
import com.pp.entity.TestPlanEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestPlanMapper extends BaseMapper<TestPlanEntity> {

    List<TestPlanEntity> selectTestPlanList(TestPlanQuery testPlanQuery);

    List<TestPlanEntity> selectTestPlanExecution(TimeDTO dto);

    List<TestPlanEntity> findTestPlanByTimeRange(TestPlanStatisticsDTO testPlanStatisticsDTO);

    //根据用户类型查询测试计划（如ats）
    List<TestPlanEntity> selectTestPlanExecutionForType(TimeDTO dto);

}
