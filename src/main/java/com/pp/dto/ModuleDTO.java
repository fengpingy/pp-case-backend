package com.pp.dto;

import lombok.Data;

@Data
public class ModuleDTO {
    private Long id;
    private String name;
    private String code;
    private Long parentId;
}
