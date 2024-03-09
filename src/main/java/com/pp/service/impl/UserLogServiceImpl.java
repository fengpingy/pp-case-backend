package com.pp.service.impl;

import com.pp.common.EventParamHolder;
import com.pp.common.UserHolder;
import com.pp.common.annotation.Event;
import com.pp.common.aspect.LogAspect;
import com.pp.common.constants.SystemConst;
import com.pp.common.enums.system.EventType;
import com.pp.dao.UserLogMapper;
import com.pp.entity.UserLogEntity;
import com.pp.service.UserLogService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;



@Service
public class UserLogServiceImpl implements UserLogService {

    @Resource
    private UserLogMapper userLogMapper;

    @Override
    public void record(Event event, boolean success) {
        long operationStaffId;
        //如果没有开启认证服务，使用默认常量策略
        if (UserHolder.get() == null) {
            operationStaffId = SystemConst.SYSTEM_USER;
        }else {
            operationStaffId = UserHolder.get().getId();
        }
        String requestId = LogAspect.SEQ_HOLDER.get();
        Date date = LogAspect.START_HOLDER.get();
        Map<EventType, List<Long>> eventTypeListMap = EventParamHolder.get();
        eventTypeListMap.forEach((k,v)->{
            v.forEach(id->{
                UserLogEntity userLogEntity = new UserLogEntity();
                userLogEntity.setOperationTime(date);
                userLogEntity.setRequestId(requestId);
                userLogEntity.setOperationStaffId(operationStaffId);
                userLogEntity.setIsSuccess(success);
                userLogEntity.setOperationClass(k);
                userLogEntity.setByTheOperatorId(id);
                userLogMapper.insert(userLogEntity);
            });
        });
    }
}
