package com.pp.entity;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("moka_label")
public class LabelEntity {
    /**
     * 标签ID
     */
    private int id;
    /**
     * 标签名字
     */
    private String name;

}
