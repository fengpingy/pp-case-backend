package com.pp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "pp_module")
public class ModuleEntity extends BaseEntity{
    private String name;
    private String code;
    private Long parentId;

}
