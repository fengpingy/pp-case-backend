package com.pp.xmind;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
@SuppressWarnings("all")
public enum XmindVersion {
    XMIND8_UPDATE9("xmind8update9", XmindConst.XMIND8_CONTENT_FILE),
    XMIND2021("xmin2021", XmindConst.XMIND2021_CONTENT_FILE);
    private final String name;
    private final String contentMainFile;

    public static XmindVersion of(String name) {
        if (name == null) return null;
        for (XmindVersion value : values()) {
            if (value.name.equals(name)) {
                return value;
            }
        }
        return null;
    }
}
