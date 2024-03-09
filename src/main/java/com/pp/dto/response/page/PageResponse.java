package com.pp.dto.response.page;


import lombok.Data;

import java.util.List;

@Data
public class PageResponse<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Integer size;
    private Long total;
    private Integer pages;
    private List<T> list;
}
