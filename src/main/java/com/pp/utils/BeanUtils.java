package com.pp.utils;

import com.pp.expection.PpExpection;


import java.util.ArrayList;
import java.util.List;

import static com.pp.utils.VerifyUtils.listIsNotNull;

public class BeanUtils {

    /**
     * 根据目标对象返回一个新对象
     * @param t
     * @param targetClass
     * @return
     * @param <T>
     * @param <E>
     */
    public static <T,E> E copyProperty(T t,Class<E> targetClass){
        if (t == null || targetClass == null) return null;
        try {
            E e = targetClass.newInstance();
            return copyProperty(t,e);
        } catch (Exception ex) {
            throw new PpExpection();
        }
    }

    /**
     * 复制参数
     * @param t
     * @param e
     * @return
     * @param <T>
     * @param <E>
     */
    public static <T,E> E copyProperty(T t,E e){
        if (t == null || e == null ) {
            return null;
        }
        org.springframework.beans.BeanUtils.copyProperties(t,e);
        return e;
    }


    /**
     * 复制一个列表
     * @param TList
     * @param cls
     * @return
     * @param <T>
     * @param <E>
     */
    public static <T, E> List<E>listObjectCopyProperty(List<T> TList, Class<E> cls) {
        if (!listIsNotNull(TList)) {
            return null;
        }
        List<E> EList = new ArrayList<>();
        TList.forEach(obj -> {
            try {
                E e = cls.newInstance();
                copyProperty(obj, e);
                EList.add(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        return EList;
    }
}
