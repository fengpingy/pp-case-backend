package com.pp.service.impl;

import com.pp.dao.LabelMapper;
import com.pp.entity.LabelEntity;
import com.pp.service.LabelService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;


@Service
public class LabelServiceImpl implements LabelService {
    @Resource
    private LabelMapper labelMapper;

    @Override
    public List<LabelEntity> getList() {
        return labelMapper.getList();
    }

    @Override
    public int updateLabel(Long id,String ids) {
        return labelMapper.updateCaseLabel(id,ids);
    }


}
