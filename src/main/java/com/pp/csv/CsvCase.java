package com.pp.csv;

import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class CsvCase {
    @CsvBindByName(column = "用例编号")
    private String code;

    @CsvBindByName(column = "所属产品")
    private String product;

    @CsvBindByName(column = "所属模块")
    private String module;

    @CsvBindByName(column = "相关需求")
    private String demand;

    @CsvBindByName(column = "用例标题")
    private String title;

    @CsvBindByName(column = "前置条件")
    private String precondition;

    @CsvBindByName(column = "步骤")
    private String step;

    @CsvBindByName(column = "预期")
    private String expect;

    @CsvBindByName(column = "实际情况")
    private String actual;

    @CsvBindByName(column = "关键词")
    private String keyword;

    @CsvBindByName(column = "优先级")
    private String level;

    @CsvBindByName(column = "用例类型")
    private String type;

    @CsvBindByName(column = "适用阶段")
    private String stage;

    @CsvBindByName(column = "用例状态")
    private String state;

    @CsvBindByName(column = "B")
    private String B;

    @CsvBindByName(column = "R")
    private String R;

    @CsvBindByName(column = "S")
    private String S;

    @CsvBindByName(column = "结果")
    private String result;

    @CsvBindByName(column = "由谁创建")
    private String creator;

    @CsvBindByName(column = "创建日期")
    private String createTime;

    @CsvBindByName(column = "最后修改者")
    private String modifier;

    @CsvBindByName(column = "修改日期")
    private String updateTime;

    @CsvBindByName(column = "用例版本")
    private String caseVersion;

    @CsvBindByName(column = "相关用例")
    private String relatedCase;
}
