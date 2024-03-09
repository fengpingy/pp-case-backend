package com.pp.dto.request;

import com.pp.common.enums.business.ProductModuleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class TestPlanStatisticsDTO {
    /**
     * 按测试计划创建时间或者测试开始执行时间统计数据
     */
    @ApiModelProperty(value = "测试计划创建时间开始范围")
    private Date createTimeFirst;
    @ApiModelProperty(value = "测试计划创建时间结束范围")
    private Date createTimeEnd;

    @ApiModelProperty(value = "测试计划开始执行时间开始范围")
    private Date startTimeFirst;
    @ApiModelProperty(value = "测试计划开始执行时间结束范围")
    private Date startTimeEnd;

    @ApiModelProperty(value = "测试计划执行结束时间开始范围")
    private Date endTimeFirst;
    @ApiModelProperty(value = "测试计划执行结束时间结束范围")
    private Date endTimeEnd;

    @ApiModelProperty(value = "所属产品")
    private ProductModuleType productModule;


}
