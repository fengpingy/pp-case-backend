package com.pp.dto.response.page;

import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import lombok.Data;

import java.util.Date;

@Data
public class PlanCasePage {
    private Long id;
    private Long caseImageId;
    private Long originalCaseId;
    private Long testerId;
    private String testerName;
    private String  executeName;
    private String title;
    private CaseLevel level;
    private Long moduleId;
    private String moduleName;
    private CaseType type;
    private String creator;
    private Date createTime;
    private Date updateTime;
    private Integer isExecute;
    private Integer isPass;
    private Long ownerId;
    private String ownerName;//经办人

}
