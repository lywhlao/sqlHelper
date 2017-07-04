package com.netease.activity.dao;

/**
 * Created by hzlaojiaqi on 2016/12/2.
 */
public class GenerateBean {

    String methodName; //方法名
    String generateSql; //对应的sql语句

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getGenerateSql() {
        return generateSql;
    }

    public void setGenerateSql(String generateSql) {
        this.generateSql = generateSql;
    }
}
