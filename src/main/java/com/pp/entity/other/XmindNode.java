package com.pp.entity.other;

import com.pp.common.enums.business.XmindNodeType;
import lombok.Data;

import java.util.List;


@Data
public class XmindNode {
    private Long id;
    private String title;
    private XmindNodeType type;
    private List<XmindNode> children;
}
