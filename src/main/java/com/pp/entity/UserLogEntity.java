package com.pp.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import com.pp.common.enums.system.EventType;
import lombok.Data;

import java.util.Date;

@Data
@TableName("moka_user_log")
public class UserLogEntity extends BaseEntity{
    /**
     * 操作时间
     */
    private Date operationTime;
    /**
     * 操作者
     */
    private Long operationStaffId;
    /**
     * 请求id
     */
    private String requestId;
    /**
     * 请求描述
     */
    private String operationDescription;
    /**
     * 是否成功
     */
    private Boolean isSuccess;


    /**
     * 被操作者id
     */
    private Long byTheOperatorId;

    /**
     * 操作类型
     */

    private EventType operationClass;
}
