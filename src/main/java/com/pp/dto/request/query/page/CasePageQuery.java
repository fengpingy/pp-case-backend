package com.pp.dto.request.query.page;


import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import lombok.Data;


@Data
public class CasePageQuery {
    private Long creatorId;
    private String title;
    private CaseLevel level;
    private Long moduleId;
    private CaseType type;
    private String creator;
}
