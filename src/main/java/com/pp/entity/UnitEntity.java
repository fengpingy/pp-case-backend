package com.pp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@TableName("moka_unit")
public class UnitEntity extends BaseEntity{

    @NotBlank(message = "组织单位名称不能为空")
    private String unitName;
    @NotBlank(message = "组织单位名称不能为空")
    private String code;
}
