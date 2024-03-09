package com.pp.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class DistinctUtils {
    /**
     * 指定字段去重
     * @param keyExtractor
     * @param <T>
     * @return
     */

    public static <T> Predicate<T> distinctByVariable(Function<? super T, ?> keyExtractor) {
        HashMap<Object, Boolean> map = new HashMap<>();
        return t -> map.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
    }

    /**
     * 获取重复数据
     */
    public static <T> Predicate<T> distinctNotByKey(Function<? super T, ?> keyExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) != null;
    }
}
