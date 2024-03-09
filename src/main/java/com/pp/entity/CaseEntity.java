package com.pp.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.pp.common.enums.business.CaseLevel;
import com.pp.common.enums.business.CaseType;
import com.pp.entity.other.CaseStepExpect;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@TableName(value = "moka_case", autoResultMap = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CaseEntity extends BaseEntity {
    private String code;
    private String title;
    @TableField(typeHandler = JacksonTypeHandler.class)
    private List<CaseStepExpect> stepExpect;
    private CaseType type;
    private CaseLevel level;
    private String precondition;
    private String remark;
    private Long testerId;
    private Long moduleId;
}
