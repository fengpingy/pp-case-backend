package com.pp.utils;

import com.pp.expection.PpExpection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnumUtils {
    /**
     * 获取枚举类的对象数组
     * @param enumsClass
     * @param <T>
     * @return
     */
    public static <T extends Enum<T>> List<Map<String,T>>  enumToObj(Class<T> enumsClass){
        if (!enumsClass.isEnum()) throw new PpExpection();
        List<Map<String,T>> list = new ArrayList<>();
        try {
            T[] enumConstants = enumsClass.getEnumConstants();
            for (T enumConstant : enumConstants) {
                Map<String,T> map  = new HashMap<>();
                String name = enumConstant.name();
                map.put(name,enumConstant);
                list.add(map);
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
