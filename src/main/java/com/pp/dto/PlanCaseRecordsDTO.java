package com.pp.dto;

import com.pp.common.enums.system.RoleType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class PlanCaseRecordsDTO extends PlanCaseDTO{
    /**
     * 执行人角色
     */
    @NotNull(message = "角色不能为空")
    private RoleType executeRole;



}
