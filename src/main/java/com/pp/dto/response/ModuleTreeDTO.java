package com.pp.dto.response;


import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ModuleTreeDTO {
    private Long id;
    private String name;
    private String code;
    private List<ModuleTreeDTO> children = new ArrayList<>();
}
