package com.pp.common.enums.system;


import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Locale;

@AllArgsConstructor
@Getter
public enum RoleType {
    /**
     * 管理员
     */
    ADMIN(100, "admin"),
    /**
     * 测试
     */
    TEST(50, "test"),

    /**
     * 开发
     */
    DEVELOP(20, "develop"),
    /**
     * 设计
     */
    UX(30,"ux"),
    /**
     * 产品
     */
    PRODUCT(10,"product");

    @EnumValue
    private final Integer num;
    @EnumValue
    private final String name;

    public static RoleType of(Integer num) {
        if (num == null) {
            return null;
        }
        for (RoleType value : values()) {
            if (value.num.equals(num)) {
                return value;
            }
        }
        return null;
    }
    public static RoleType nameOf(String name) {
        if (name==null)return null;
        for (RoleType value : values()) {
            if (value.name.toUpperCase(Locale.ROOT).equals(name.toUpperCase(Locale.ROOT))){
                return value;
            }
        }
        return null;
    }
}
