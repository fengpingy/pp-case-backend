package com.pp.dto;

import com.pp.utils.DateUtils;
import lombok.Data;

import java.util.Date;

@Data
public class PlanCaseDTO {
    private Long id;
    private Long planId;
    private Long caseImageId;
    private Integer isExecute;
    private Date executeTime;
    private Integer isPass;
    private String executeName;
    private Date createTime = DateUtils.dateTime();
    private Date updateTime = DateUtils.dateTime();
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
