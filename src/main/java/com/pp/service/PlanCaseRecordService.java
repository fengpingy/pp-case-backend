package com.pp.service;

import com.pp.dto.PlanCaseDTO;
import com.pp.dto.PlanCaseRecordsDTO;
import com.pp.dto.request.TestPlanStatisticsDTO;
import com.pp.dto.response.statement.ContrastStatisticsDTO;
import com.pp.dto.response.statement.PlanCaseStatisticsDTO;
import com.pp.dto.response.statement.TestPlanStatisticsResponseDTO;

import java.util.List;

public interface PlanCaseRecordService {
    /**
     * 添加cases执行记录
     */

    int batchAddPlanCaseRecord(List<PlanCaseRecordsDTO> planCaseRecordsDTOS);

    List<PlanCaseRecordsDTO> searchPlanCaseRecordsByPlanId(PlanCaseDTO planCaseDTO);

    ContrastStatisticsDTO devTestContrastStatistics(PlanCaseDTO planCaseDTO);

    PlanCaseStatisticsDTO planCaseStatistics(PlanCaseDTO planCaseDTO);

    TestPlanStatisticsResponseDTO itemPlanCaseStatistics(Long planId, TestPlanStatisticsResponseDTO testPlanStatisticsResponseDTO);

    TestPlanStatisticsResponseDTO planCaseStatisticsByTimeRange(TestPlanStatisticsDTO testPlanStatisticsDTO);





}
