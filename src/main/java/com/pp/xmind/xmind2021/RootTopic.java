package com.pp.xmind.xmind2021;

import lombok.Data;

@Data
public class RootTopic {

    private String id;
    private String structureClass;
    private String title;
    private Attached children;
}
