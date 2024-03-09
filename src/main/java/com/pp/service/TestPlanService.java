package com.pp.service;

import com.pp.dto.PageInfoStatus;
import com.pp.dto.TestPlanDTO;
import com.pp.dto.request.TestPlanStatisticsDTO;
import com.pp.dto.request.TimeDTO;
import com.pp.dto.request.query.PageDTO;
import com.pp.dto.request.query.page.TestPlanQuery;
import com.pp.dto.response.page.PageResponse;
import com.pp.dto.response.page.TestPlanPage;
import com.pp.entity.TestPlanEntity;

import java.util.List;

public interface TestPlanService {
    /**
     * 添加测试计划
     *
     * @param testPlanDTO
     * @return
     */
    Long addTestPlan(TestPlanDTO testPlanDTO);

    /**
     * 获取测试计划详情
     *
     * @param id
     * @return
     */
    TestPlanDTO getTestPlan(Long id);

    /**
     * 删除测试计划
     *
     * @param id
     * @return
     */
    int delTestPlan(Long id);

    /**
     * 修改测试计划
     *
     * @param testPlanDTO
     * @return
     */
    int editTestPlan(TestPlanDTO testPlanDTO);

    /**
     * 测试用例分页
     *
     * @param planQueryPageDTO
     * @return
     */
    PageResponse<TestPlanPage> testPlanList(PageDTO<TestPlanQuery> planQueryPageDTO);


    /**
     * 测试计划执行情况
     *
     * @param timeDTO
     * @return
     */
    PageInfoStatus<TestPlanPage> testPlanExecution(TimeDTO timeDTO);

    /**
     * 查询时间范围内的测试计划
     *
     * @param testPlanStatisticsDTO
     * @return
     */
    List<TestPlanEntity> findTestPlanByTimeRange(TestPlanStatisticsDTO testPlanStatisticsDTO);

    /**
     * 测试计划完成情况组装
     */
//    void testPlanFinishedCondition();
}
