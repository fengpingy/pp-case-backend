package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.entity.UserLogEntity;

import java.util.List;

public interface UserLogMapper extends BaseMapper<UserLogEntity> {
    int saveBatch(List<UserLogEntity> logEntities);
}
