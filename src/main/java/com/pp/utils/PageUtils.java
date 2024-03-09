package com.pp.utils;

import com.github.pagehelper.PageInfo;
import com.pp.dto.response.page.PageResponse;

public class PageUtils {
    /**
     * 分页替换
     * @param pagePageInfo
     * @param <T>
     * @return
     */
    public static <T> PageResponse<T> pageInfoToPageResponse(PageInfo<T> pagePageInfo){
        PageResponse<T> pageResponse = new PageResponse<>();
        BeanUtils.copyProperty(pagePageInfo,pageResponse);
        return pageResponse;
    }
}
