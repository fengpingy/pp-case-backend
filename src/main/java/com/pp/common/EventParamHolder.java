package com.pp.common;

import com.pp.common.enums.system.EventType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EventParamHolder {


    private final static ThreadLocal<Map<EventType, List<Long>>> EventParamHolderLocal = ThreadLocal.withInitial(HashMap::new);

    public static Map<EventType, List<Long>> get() {
        return EventParamHolderLocal.get();
    }


    public static void putEvent(EventType t, Long l) {
        Map<EventType, List<Long>> eventTypeListMap = get();
        if (!hasThisKey(eventTypeListMap,t)) {
            eventTypeListMap.put(t, new ArrayList<>());
        }
        eventTypeListMap.get(t).add(l);
    }

    public static void putEvent(EventType t, List<Long> list) {
        Map<EventType, List<Long>> eventTypeListMap = get();
        if (!hasThisKey(eventTypeListMap,t)) {
            eventTypeListMap.put(t, list);
        } else {
            eventTypeListMap.get(t).addAll(list);
        }
    }

    public static void remove() {
        EventParamHolderLocal.remove();
    }

    public static boolean hasThisKey(Map<EventType, List<Long>> map,EventType t){
        return map.containsKey(t);
    }

}
