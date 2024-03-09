package com.pp.dto.response.statement;

import com.pp.entity.CaseImageEntity;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DevTestContrastDTO {
    /**
     * 研发和测试cases执行对比
     */
    private Long caseImageId;
    private CaseImageEntity caseImageEntity;
    private Integer devIsPass;
    private Integer testIsPass;

    private boolean equalityPass;


}
