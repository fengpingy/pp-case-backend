package com.pp.dto.response.page;

import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import lombok.Data;

import java.util.Date;


@Data
public class CasePage {
    private Long id;
    private Long testerId;
    private String title;
    private CaseLevel level;
    private Long moduleId;
    private String moduleName;
    private CaseType type;
    private String creator;
    private Date createTime;
    private Date updateTime;
}
