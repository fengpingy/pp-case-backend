package com.pp.dto.response.statement;

import com.pp.common.enums.business.ProductModuleType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestPlanResponseDTO {
    @ApiModelProperty(value = "测试计划id")
    private Long id;

    @ApiModelProperty(value = "测试计划名称")
    private String name;

    @ApiModelProperty(value = "创建人名称")
    private String creatorName;

    @ApiModelProperty(value = "执行人名称")
    private String executorName;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "开始执行时间")
    private Date startTime;

    @ApiModelProperty(value = "结束执行时间")
    private Date endTime;

    @ApiModelProperty(value = "所属产品")
    private ProductModuleType productModule;
}
