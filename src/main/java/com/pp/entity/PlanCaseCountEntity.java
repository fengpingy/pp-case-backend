package com.pp.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@TableName(value = "moka_plan_case")
@NoArgsConstructor
@AllArgsConstructor
public class PlanCaseCountEntity extends BaseEntity {
    private int count;

    private Date execute_time;
    @Override
    public String toString() {
        return "PlanCaseDTO{" +
                "count=" + count +
                '}';
    }
}
