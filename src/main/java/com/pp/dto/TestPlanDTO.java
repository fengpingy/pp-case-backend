package com.pp.dto;

import com.pp.common.enums.business.ProductModuleType;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.List;

@Data
@Accessors(chain = true)
public class TestPlanDTO {
    private Long id;
    @NotBlank(message = "测试计划名称不能为空")
    private String name;
    @NotNull(message = "开始时间不能为空")
    private Date startTime;
    @NotNull(message = "结束时间不能为空")
    private Date endTime;
    private Long executorId;
    private List<Long> caseIds;
    private List<Long> moduleIds;
    private Boolean isIncludeSubset;
    private ProductModuleType productModule;
    @Size(max = 32,message = "需求关键字不能超过32字符")
    private String jiraDemand;
    @Size(max = 32,message = "标签字符不能超过32字符")
    private String bugLabels;
}
