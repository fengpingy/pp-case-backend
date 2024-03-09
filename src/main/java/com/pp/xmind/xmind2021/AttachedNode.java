package com.pp.xmind.xmind2021;

import lombok.Data;

@Data
public class AttachedNode {
    private String id;
    private String title;
    private Attached children;
}
