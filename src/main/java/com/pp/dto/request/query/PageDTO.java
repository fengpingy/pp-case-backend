package com.pp.dto.request.query;


import com.pp.common.enums.business.ProductModuleType;
import lombok.Data;

@Data
public class PageDTO<T> {
    private Integer pageSize;
    private Integer pageNum;

    private T params;
}
