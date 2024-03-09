package com.pp.dto.request.query.page;

import com.pp.common.enums.business.ProductModuleType;
import lombok.Data;
import java.util.Date;

@Data
public class TestPlanQuery {
    private Long id;
    private String name;
    private Date startTime;
    private Date endTime;
    private Long creatorId;
    private Long executorId;
    private ProductModuleType productModule;
}
