package com.pp.dto;

import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import com.pp.entity.other.CaseStepExpect;
import lombok.Data;

import java.util.List;

@Data
public class CaseImageDTO {
    private Long id;
    private String code;
    private String title;
    private List<CaseStepExpect> stepExpect;
    private CaseType type;
    private CaseLevel level;
    private String precondition;
    private String remark;
    private Long testerId;
    private Long moduleId;
    private Long originalCaseId;
}
