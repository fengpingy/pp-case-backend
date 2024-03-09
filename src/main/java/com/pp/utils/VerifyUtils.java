package com.pp.utils;

import java.util.List;
import java.util.Map;

public class VerifyUtils {
    public static boolean notNull(String message) {
        if (message == null || message.equals("")) {
            return false;
        }
        return true;
    }

    public static <T> Boolean listIsNotNull(List<T> list) {
        return list != null && list.size() > 0;
    }

    public static <T, E> boolean mapIsNotNull(Map<T, E> map) {
        return map != null && map.size() > 0;
    }
}
