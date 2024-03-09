package com.pp.utils;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * 暂时不使用
 */
public class QueryUtils {

    /**
     * 查询对象是否存在
     * @param mapper
     * @param id
     * @param <T>
     * @return
     */
    public static <T extends BaseMapper> boolean ObjIsExists(T mapper, Long id) {
        Object entity = mapper.selectById(id);
        return entity != null;
    }
}
