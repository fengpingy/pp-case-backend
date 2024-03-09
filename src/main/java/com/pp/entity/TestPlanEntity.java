package com.pp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.pp.common.enums.business.ProductModuleType;
import java.util.Date;

@Data
@TableName(value = "moka_test_plan")
public class TestPlanEntity extends BaseEntity{
    private String name;
    private Date startTime;
    private Date endTime;
    private Long creatorId;
    private Long executorId;
    private ProductModuleType productModule;
    private String jiraDemand;
    private String bugLabels;

}
