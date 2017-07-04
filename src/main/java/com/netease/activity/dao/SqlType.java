package com.netease.activity.dao;

/**
 * Created by hzlaojiaqi on 2016/11/28.
 */
public enum  SqlType {

    SELECT(1,"get"),

    DELETE(2,"delete"),

    UPDATE(3,"update"),

    INSERT(4,"insert"),

    CLEAN(5,"clean"),

    DEFAULT(6,"default");

    int type;
    String desc;

     SqlType(int type, String desc) {
        this.type = type;
        this.desc = desc;
    }


    public static SqlType getSqlType(String methodName){
        for(SqlType temp:SqlType.values()){
            if(methodName.startsWith(temp.desc)){
                return temp;
            }
        }
        return DEFAULT;
    }
}
