package com.pp.dto.request;


import lombok.Data;

@Data
public class ModuleAndSubmoduleDTO {
    private Long rootModuleId;
    private Boolean isIncludeSubmodule;
}
