package com.pp.dto.request.query.page;

import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import lombok.Data;

@Data
public class PlanCaseQuery {
    private Long planId;
    private String title;
    private CaseLevel level;
    private Long moduleId;
    private CaseType type;
    private Integer isExecute;
    private Integer isPass;
}
