package com.pp.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class ExportDTO {
    private Long moduleId;//模块ID
    private List<Long> caseIds;//case id
    private int type;//1 csv    0 xmind    不传xmind
}
