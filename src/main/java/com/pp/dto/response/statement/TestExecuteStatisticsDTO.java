package com.pp.dto.response.statement;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TestExecuteStatisticsDTO {
    /**
     * 测试执行数据统计
     */
    private Integer testExecutePassAmount = 0;
    private Integer testExecuteFailAmount = 0;
    private List<Long> testFailCaseImageId;
}
