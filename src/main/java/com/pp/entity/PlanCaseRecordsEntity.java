package com.pp.entity;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pp.common.enums.system.RoleType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "moka_plan_case_records", autoResultMap = true)
public class PlanCaseRecordsEntity extends PlanCaseEntity{
    @TableField("execute_role")
    private RoleType executeRole;






}
