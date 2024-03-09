package com.pp.dto.response.page;


import com.pp.common.enums.business.ProductModuleType;
import lombok.Data;
import java.util.Date;

@Data
public class TestPlanPage {

    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Date createTime;
    private Date updateTime;
    private Long creatorId;
    private  String  creatorName;
    private Long executorId;
    private  String executorName;

    private String status;// 未开始 进行中  按时完成  未按时完成 计划未关联用例
    private Date completeTime;
    private ProductModuleType productModule;

}
