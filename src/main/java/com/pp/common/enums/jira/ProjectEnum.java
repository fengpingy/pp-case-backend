package com.pp.common.enums.jira;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ProjectEnum {
    /**
     * pp 测试环境jira项目
     */
    HCM_ALL(10402,"HCM-ALL");

    @EnumValue
    private Integer value;
    @EnumValue
    private String name;

    public static ProjectEnum of (Integer value){
        if (value == null){
            return null;
        }

        for (ProjectEnum myValue: values()){
            if (myValue.value.equals(value)){
                return myValue;
            }
        }
        return null;

    }

    public static ProjectEnum nameOf(String name){
        if (name==null){
            return null;
        }

        for (ProjectEnum myName: values()){
            if (myName.name.equals(name.toUpperCase())){
                return myName;
            }
        }

        return null;

    }

}
