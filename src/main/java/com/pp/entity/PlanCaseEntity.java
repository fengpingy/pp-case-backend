package com.pp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName(value = "pp_plan_case")
@NoArgsConstructor
@AllArgsConstructor
public class PlanCaseEntity extends BaseEntity {
    private Long planId;
    private Long caseImageId;
    private Integer isExecute;
    private  String executeName;
    private Date executeTime;
    private Integer isPass;
    private Long ownerId;
    private String caseRemark;

    @Override
    public String toString() {
        return "PlanCaseDTO{" +
                "caseImageId=" + caseImageId +
                ", isExecute=" + isExecute +
                ", executeTime=" + executeTime +
                ", isPass=" + isPass + '\'' +
                '}';
    }
}
