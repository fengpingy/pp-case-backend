package com.pp.service;

import com.pp.entity.LabelEntity;

import java.util.List;

public interface LabelService {
    /**
     * 获取标签列表
     *
     * @return
     */
    List<LabelEntity> getList();


    /**
     * 更新标签
     * @param id caseid
     *        labelIds 标签IDs
     * @return
     */
    int updateLabel(Long id,String labelIds);

}
