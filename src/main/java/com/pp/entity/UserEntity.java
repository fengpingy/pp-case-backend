package com.pp.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.pp.common.enums.system.RoleType;
import lombok.Data;


@Data
@TableName("moka_user")
public class UserEntity extends BaseEntity {

    /**
     * 用户名
     */
    private String username;

    /**
     * 角色
     */
    @TableField("role")
    private RoleType roleType;

    /**
     * 名称
     *
     */
    private String nickname;

    /**
     * 不添加到表中
     */
    @TableField(exist = false)
    private String password;

    /**
     * 区分QA所属业务线：1代表ats
     *
     */
    private int type;
}
