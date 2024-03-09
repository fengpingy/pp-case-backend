package com.pp.dto;


import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import com.pp.entity.other.CaseStepExpect;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;


@Data
public class CaseDTO {
    private Long id;
    private String code;
    @NotBlank(message = "用例标题不能为空")
    private String title;
    private List<CaseStepExpect> stepExpect;
    private CaseType type;
    private CaseLevel level;
    private String precondition;
    private String remark;
    private Long testerId;
    private Long moduleId;
    private String moduleTitle;
}
