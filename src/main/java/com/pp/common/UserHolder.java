package com.pp.common;

import com.pp.entity.UserEntity;


public class UserHolder {

    private static final ThreadLocal<UserEntity> USER_HOLDER = new ThreadLocal<>();

    public static void set(UserEntity userEntity){
        USER_HOLDER.set(userEntity);
    }

    public static void remove(){
        USER_HOLDER.remove();
    }

    public static UserEntity get(){
        return USER_HOLDER.get();
    }

}

