package com.pp.xmind.xmind8;


import lombok.Data;

@Data
public class Topic {
    private String id;
    private Object title;
    private Long timestamp;
    private Children children;
}
