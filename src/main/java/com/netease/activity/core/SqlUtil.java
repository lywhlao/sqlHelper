package com.netease.activity.core;


import com.netease.activity.annoation.*;
import com.netease.activity.output.FileUtil;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

/**
 *
 * Bean->sql语句工具类,运行Start类中的main方法，即可生成对应的建表语句
 *
 * Created by hzlaojiaqi on 2016/10/10.
 */
public class SqlUtil {
    public static Map<String, String> map = new HashMap<String, String>();
    private static final String ID = "id";
    private static final String INT = "int";
    private static final String CHAR = "char";

    static {
        map.put("String", "varchar(64)");
        map.put("int", "int(10)");
        map.put("long", "bigInt(20)");
        map.put("boolean", "tinyInt(1)");
        map.put("Integer", "int(10)");
    }


    /**
     * 生成sql语句 并写入文件
     * @param classes
     * @throws IOException
     */
    public static void writeSqlToFile(Class<?>[] classes,String outputPath) {

        for(Class<?> temp:classes){
            String beanSql=sqlCreate(temp);
            try {
                FileUtil.writeSql(beanSql,outputPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 将javaBean 生成sql语句
     *
     * @param clazz
     * @return
     */
    private static String sqlCreate(Class<?> clazz) {
        StringBuilder sb = new StringBuilder();
        generateTableName(sb, clazz);
        generateColumn(sb, clazz);
        generateConstraint(sb, clazz);
        return sb.toString();
    }

    /**
     * 生成表名
     *
     * @param sb
     * @param clazz
     */
    private static void generateTableName(StringBuilder sb, Class<?> clazz) {
        String tableName = getTableName(clazz);
        sb.append("\n\n");
        sb.append("DROP TABLE IF EXISTS `" + tableName + "`;\n");
        sb.append("CREATE TABLE `" + tableName + "` ( \n");
    }

    /**
     * 生成列
     *
     * @param sb
     * @param clazz
     */
    private static void generateColumn(StringBuilder sb, Class<?> clazz) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field temp : fields) {
            if(isIgnoreField(temp)){
                continue;
            }
            if(ID.equals(temp.getName().toLowerCase())){
                sb.append("\t\t");
                sb.append("`" + temp.getName() + "`" +
                        " " + map.get(temp.getType().getSimpleName()) +
                        " " + "NOT NULL AUTO_INCREMENT COMMENT \'主键\' ,\n");
            }else{
                sb.append("\t\t");
                sb.append("`" + temp.getName() + "`" +
                        " " + map.get(temp.getType().getSimpleName()) +
                        " " + " DEFAULT "+getMemberDefaultValue(temp)+" COMMENT "+getDescValue(temp)+",\n");

            }
        }
    }

    /**
     * 获取列描述值
     * @param temp
     * @return
     */
    private static String getDescValue(Field temp){
        Annotation [] annotations= temp.getAnnotations();
        for(Annotation tempAnnotation:annotations){
            if(tempAnnotation instanceof Desc){
                return "\'"+((Desc) tempAnnotation).value()+"\'";
            }
        }
        return "\'备注\'";
    }

    /**
     * 获取成员默认值
     * @param temp
     * @return
     */
    private static String getMemberDefaultValue(Field temp){
         String value= map.get(temp.getType().getSimpleName());
         if(value.toLowerCase().contains(INT)){
             return "\'0\'";
         }
        return "\'\'";
    }

    /**
     * 该成员变量是否需要忽略
     * @param temp
     * @return
     */
    private static boolean isIgnoreField(Field temp){
        Annotation [] annotations= temp.getAnnotations();
        for(Annotation tempAnnotation:annotations){
            if(tempAnnotation instanceof Ignore){
                return true;
            }
        }
        return false;
    }


    /**
     * 生成constraint SQL语句
     * @param sb
     * @param clazz
     */
    private static void generateConstraint(StringBuilder sb, Class<?> clazz) {
        Map<String, List<String>> keyMap = new HashMap<String, List<String>>();
        Map<String, List<String>> uniqueKeyMap = new HashMap<String, List<String>>();
        Map<String, List<String>> primaryKeyMap = new HashMap<String, List<String>>();
        Field[] fields = clazz.getDeclaredFields();
        for (Field tempField : fields) {
            Annotation[] annotations = tempField.getAnnotations();
            for (Annotation tempAnnotation : annotations) {
                if (tempAnnotation instanceof PrimaryKey) {
                    setKeyMap(primaryKeyMap,tempAnnotation,tempField);
                }
                if (tempAnnotation instanceof UniqueKey) {
                     setKeyMap(uniqueKeyMap,tempAnnotation,tempField);
                }
                if (tempAnnotation instanceof Key) {
                    setKeyMap(keyMap, tempAnnotation,tempField);
                }
            }
        }
        generateKeyConstraint(primaryKeyMap,sb,IndexType.PRIMARY_KEY);
        generateKeyConstraint(keyMap,sb,IndexType.KEY);
        generateKeyConstraint(uniqueKeyMap,sb,IndexType.UNIQUE_KEY);
        String tableDesc=getTableDesc(clazz);
        sb.append("\n)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='"+tableDesc+"';");
    }


    /**
     * 获取表名
     * @param clazz
     * @return
     */
    private static String getTableDesc(Class<?> clazz){
        Annotation[] annotations = clazz.getAnnotations();
        for(Annotation temp:annotations){
            if(temp instanceof Desc){
                return ((Desc)temp).value();
            }
        }
        return "表名";
    }

    /**
     * 生成索引sql语句
     * @param keyMap
     * @param sb
     */
    private static void generateKeyConstraint(Map<String, List<String>> keyMap,StringBuilder sb,boolean isUniqueKey){
        Set<String> keySet = keyMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String keyName = iterator.next();
            List<String> list = keyMap.get(keyName);
            sb.append("\t\t");
            if(!isUniqueKey) {
                sb.append("KEY `" + keyName + "`");
            }else{
                sb.append("UNIQUE KEY `" + keyName + "`");
            }
            sb.append(" (");
            for(int i=0;i<list.size();i++){
                if(i<list.size()-1){
                    sb.append("`"+list.get(i)+"`,");
                }else{
                    sb.append("`"+list.get(i)+"`");
                }
            }
            sb.append(")");
            if(iterator.hasNext()){
                sb.append(",\n");
            }
        }
        if(!isUniqueKey){
            sb.append(",\n");
        }
    }

    /**
     * 生成索引sql语句
     * @param keyMap
     * @param sb
     */
    private static void generateKeyConstraint(Map<String, List<String>> keyMap,StringBuilder sb,IndexType indexType){
        Set<String> keySet = keyMap.keySet();
        Iterator<String> iterator = keySet.iterator();
        while (iterator.hasNext()) {
            String keyName = iterator.next();
            List<String> list = keyMap.get(keyName);
            sb.append("\t\t");

            if(indexType==IndexType.PRIMARY_KEY){
                sb.append(indexType.indexPreName);
            }else{
                sb.append(indexType.indexPreName+" `"+keyName+"`");
            }
            sb.append(" (");
            for(int i=0;i<list.size();i++){
                if(i<list.size()-1){
                    sb.append("`"+list.get(i)+"`,");
                }else{
                    sb.append("`"+list.get(i)+"`");
                }
            }
            sb.append(")");
            if(iterator.hasNext()){
                sb.append(",\n");
            }
        }
        if(indexType!=IndexType.UNIQUE_KEY){
            sb.append(",\n");
        }
    }


    /**
     * 设置索引值
     *
     * @param keyMap
     * @param tempAnnotation
     */
    private static void setKeyMap(Map<String, List<String>> keyMap, Annotation tempAnnotation,Field tempField) {
        String name="";
        if(tempAnnotation instanceof  Key) {
            name = ((Key) tempAnnotation).value();
        }
        if(tempAnnotation instanceof  UniqueKey) {
            name = ((UniqueKey) tempAnnotation).value();
        }
        if(tempAnnotation instanceof PrimaryKey){
            name="PRIMARY KEY";
        }
        List<String> list = keyMap.get(name);
        if (list == null) {
            List<String> tempList = new ArrayList<String>();
            tempList.add(tempField.getName());
            keyMap.put(name, tempList);
        } else {
            list.add(tempField.getName());
        }
    }


    /**
     * 获取tableName
     *
     * @param clazz
     * @return
     */
    public static String getTableName(Class<?> clazz) {
        Annotation[] annotations = clazz.getAnnotations();
        String tableName = "table_name";
        for (Annotation temp : annotations) {
            if (temp instanceof TableName) {
                tableName = ((TableName) temp).value();
            }
        }
        return tableName;
    }
}
