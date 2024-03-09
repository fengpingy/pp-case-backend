package com.pp.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pp.entity.LabelEntity;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LabelMapper extends BaseMapper<LabelEntity> {

    List<LabelEntity> getList();

    int updateCaseLabel(@Param("id") long id,@Param("labelIds") String labelIds);

}
