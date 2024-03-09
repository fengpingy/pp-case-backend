package com.pp.service;

import com.pp.common.annotation.Event;

public interface UserLogService {
    void record(Event event,boolean success);
}
