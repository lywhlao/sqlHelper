package com.netease.activity.core;

/**
 * Created by hzlaojiaqi on 2017/7/4.
 */
public enum  IndexType {

    KEY("KEY"),
    UNIQUE_KEY("UNIQUE KEY"),
    PRIMARY_KEY("PRIMARY KEY");

    public String indexPreName;


    IndexType(String indexPreName) {
        this.indexPreName = indexPreName;
    }
}
