package com.pp.dto.response.statement;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestPlanStatisticsResponseDTO extends PlanCaseStatisticsDTO{

    @ApiModelProperty(value = "研发执行cases总数")
    private long devExecuteAmount = 0;
    @ApiModelProperty(value = "测试执行cases总数")
    private long testExecuteAmount = 0;

    @ApiModelProperty(value = "研发测试执行相等的cases总数")
    private long equalityPassAmount = 0;
    @ApiModelProperty(value = "研发测试执行不同的cases总数")
    private long unequalPassAmount = 0;
    @ApiModelProperty(value = "测试计划列表")
    private Integer testExecutePassAmount = 0;

    private Integer testExecuteFailAmount = 0;
    private List<TestPlanResponseDTO> testPlanLists;

}
