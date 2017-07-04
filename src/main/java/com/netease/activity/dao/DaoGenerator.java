package com.netease.activity.dao;

import com.netease.activity.core.SqlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.ibatis.annotations.Param;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 *
 * 自动生成sql语句
 * Created by laojiaqi on 2016/11/26.
 */
public class DaoGenerator {

    public static final String DAO_PATH_PREFIX=System.getProperty("user.dir") + "\\src\\main\\java\\com\\netease\\mail\\activity\\com.netease.activity.dao\\";

    public static final String IMPORT_PACKAGE="import org.apache.ibatis.annotations.*;";

    public static final String IMPORT_KEY="import";

    public static final String ANNOTATION_KEY="@";

    public static final Set<String> KEY_SET = new HashSet<String>();

    public static final Set<String> PARAM_SET = new HashSet<String>();

    static {
        KEY_SET.add("get");
        KEY_SET.add("update");
        KEY_SET.add("delete");
        KEY_SET.add("insert");
        KEY_SET.add("clean");
        PARAM_SET.add("id");
        PARAM_SET.add("uid");
    }


    public static  void generate(Class<?> TARGET_CLASS){
        String FILE_NAME=TARGET_CLASS.getSimpleName()+".java";
        DaoGenerator generator=new DaoGenerator();
        Method[]  methods=generator.inputMethods(TARGET_CLASS);
        String tableName= SqlUtil.getTableName(TARGET_CLASS);
        List<GenerateBean> generators=generator.generator(methods,tableName);
        List<String> originResult=readDaoFile(FILE_NAME);
        produceNewFile(originResult,generators,FILE_NAME);
    }

    /**
     * 构建新文件
     * @param originResult
     * @param generators
     * @param fileName
     */
    public static void produceNewFile( List<String> originResult, List<GenerateBean> generators,String fileName){
        List<String> newFileStrings=new ArrayList<String>();
        boolean isImported=false;
        for(String temp:originResult){
            if(temp.contains(IMPORT_KEY)){
                if(!isImported){
                    newFileStrings.add(IMPORT_PACKAGE);
                    isImported=true;
                }
            }
            if(isMethodLine(temp,generators)){
                if(!haveGotAnnotation(newFileStrings)) {
                    newFileStrings.add(getGenerateSql(temp, generators));
                    newFileStrings.add(temp);
                }else{
                    newFileStrings.add(temp);
                }
            }else{
                newFileStrings.add(temp);
            }
        }
        String path=DAO_PATH_PREFIX+fileName;
        File file=new File(path);
        try {
            FileUtils.writeLines(file,"utf-8",newFileStrings);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static boolean haveGotAnnotation( List<String> newFileStrings){
        int size=newFileStrings.size();
        if("".equals(newFileStrings.get(size-1))){
            return false;
        }
        return true;
    }

    /**
     * 是否是方法行
     * @param origin
     * @param generators
     * @return
     */
    private static boolean isMethodLine(String origin,List<GenerateBean> generators){
        boolean flag=false;
        for(GenerateBean generateBean:generators){
            if(origin.contains(generateBean.getMethodName())){
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是方法行
     * @param origin
     * @param generators
     * @return
     */
    private static String getGenerateSql(String origin,List<GenerateBean> generators){
        for(GenerateBean generateBean:generators){
            if(origin.contains(generateBean.getMethodName())){
                return generateBean.getGenerateSql();
            }
        }
        return "--";
    }

    /**
     * 读取Java文件到内存
     * @param fileName
     * @return
     */
    private static List<String> readDaoFile(String fileName){
        String path=DAO_PATH_PREFIX+fileName;
        File file=new File(path);
        try {
            List<String> result=FileUtils.readLines(file,"utf-8");
            return  result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>();
    }

    /**
     * 获取输入需要处理的methods
     *
     * @param clazz
     * @return
     */
    public Method[] inputMethods(Class<?> clazz) {
        Method[] methods = clazz.getDeclaredMethods();
        Method[] inputMethods = qualify(methods);
        return inputMethods;
    }

    /**
     * @param methods
     * @param tableName
     * @return
     */
    private List<GenerateBean> generator(Method[] methods, String tableName) {
          List<GenerateBean> results=new ArrayList<GenerateBean>();
          for(Method method:methods){
              GenerateBean generateBean=new GenerateBean();
              generateBean.setMethodName(method.getName());
              generateBean.setGenerateSql(dealPerMethod(method,tableName));
              results.add(generateBean);
          }
        return results;
    }


    private String dealPerMethod(Method method, String tableName) {
        Annotation[][] annotations = method.getParameterAnnotations();
        List<String> whereParam = new ArrayList<String>();
        List<String> noWhereParm = new ArrayList<String>();
        for (int i = 0; i < annotations.length; i++) {
            for (int j = 0; j < annotations[i].length; j++) {
                if (annotations[i][j] instanceof Param) {
                    Annotation annotation = (Param) annotations[i][j];
                    String paramValue=getParamValue(annotation.toString());
                    if (PARAM_SET.contains(paramValue)) {
                        whereParam.add(paramValue);
                    } else {
                        noWhereParm.add(paramValue);
                    }

                }
            }
        }
        String result="";
        SqlType sqlType = SqlType.getSqlType(method.getName());
        switch (sqlType) {
            case SELECT:
                result=  dealSelect(whereParam,noWhereParm,method,tableName);
                break;

            case UPDATE:
                result= dealUpdate(whereParam,noWhereParm,tableName);
                break;

            case DELETE:
                result= dealDelete(method,whereParam,noWhereParm,tableName);
                break;

            case INSERT:
                result=dealInsert(method,tableName);
                break;

            case CLEAN:
                result=dealClean(tableName);
                break;

        }
            return result;
    }

    private String getParamValue(String source){
        String[] values=source.split("=");
        String result=values[1].replace(")","");
        return result;
    }

    private String dealClean(String tableName){
        StringBuilder sb=new StringBuilder();
        sb.append("\t@Delete(\"delete from "+tableName+" where id>0\")");
        return sb.toString();
    }

    private String dealDelete(Method method,List<String> whereParam, List<String> noWhereParam,String tableName){
        StringBuilder sb=new StringBuilder();
        sb.append("\t@Delete(\"delete from "+tableName+" where");
        if(whereParam.size()>0) {
            //1.where列表中有参数的情况
            for (String temp : whereParam) {
                sb.append("\t\"+\n" + temp + "=" + "#{" + temp + "}");
            }
        }else{
            //2.where列表中没有参数，可能只有一个参数
            Class<?> [] classes=method.getParameterTypes();
            if(classes.length==0){
                return "";
            }
            if(classes[0].getSimpleName().equals("String")){
               sb.append(" uid=#{uid}");
            }
            if(classes[0].getSimpleName().equals("Long")||classes[0].getSimpleName().equals("long")){
                sb.append(" id=#{id}");
            }
        }
        sb.append("\")");
        return sb.toString();
    }


    /**
     * @param whereParam
     * @param noWhereParam
     * @return
     */
    private String dealSelect( List<String> whereParam, List<String> noWhereParam,Method method,String tableName){
        StringBuilder sb=new StringBuilder();
        sb.append("\t@Select(\"select * from "+tableName+" where");
        if(whereParam.size()>0) {
            //1.where列表中有参数的情况
            for(String temp:whereParam){
                sb.append("\"+\n\t\""+temp+"="+"#{"+temp+"}");
            }
            sb.append("\")");
        }else{
            //2.where列表中没有参数，可能只有一个参数
            Class<?> [] classes=method.getParameterTypes();
            if(classes[0].getSimpleName().equals("String")){
                sb.append(" uid=#{uid}");
            }
            if(classes[0].getSimpleName().equals("Long")||classes[0].getSimpleName().equals("long")){
                sb.append(" id=#{id}");
            }
            sb.append("\")");
        }

        return sb.toString();
    }


    /**
     * @param method
     * @param tableName
     * @return
     */
    private String dealInsert(Method method,String tableName) {
        StringBuilder sb=new StringBuilder();
        Class<?>[] classes=method.getParameterTypes();
        if(classes.length>1){
           return "classes.length >1";
        }
        sb.append("\t@Insert(\"insert ignore into "+tableName+"(");
        Class<?> targetClass=classes[0];
        Field[] fields=targetClass.getDeclaredFields();
        for(int i=0;i<fields.length;i++){
            if(fields[i].getName().equals("id")){
                continue;
            }
            sb.append(fields[i].getName());
            if(i<(fields.length-1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        sb.append("\"+\n\t\t\t\"values(");
        Field[] insertFields=targetClass.getDeclaredFields();
        for(int i=0;i<insertFields.length;i++){
            if(fields[i].getName().equals("id")){
                continue;
            }
            sb.append("#{"+fields[i].getName()+"}");
            if(i<(insertFields.length-1)) {
                sb.append(",");
            }
        }
        sb.append(")");
        sb.append("\")");
        return sb.toString();
    }


    private String dealUpdate(List<String> whereParam, List<String> noWhereParam,String tableName){
        StringBuilder sb=new StringBuilder();
        sb.append("\t@Update(\"update "+tableName+" set");
        for(int i=0;i<noWhereParam.size();i++){
            String temp=noWhereParam.get(i);
            sb.append("\"+\n\t\t\t\"" + temp + "=" + "#{" + temp + "}");
            if(i<noWhereParam.size()-1) {
                sb.append(",");
            }
        }
        sb.append(" where");
        for(int i=0;i<whereParam.size();i++){
            String temp=whereParam.get(i);
            sb.append(" " + temp + "=" + "#{" + temp + "}");
            if(i<noWhereParam.size()-1) {
                sb.append(" and ");
            }
        }
        sb.append("\")");
        return sb.toString();

    }


    /**
     * 过滤需要添加的annotations
     *
     * @param methods
     * @return
     */
    private Method[] qualify(Method[] methods) {
        ArrayList<Method> list = new ArrayList<Method>();
        for (Method temp : methods) {
            if(temp.getAnnotations().length==0){
                list.add(temp);
            }
            for (Annotation annotation : temp.getAnnotations()) {
                if (hasKeyWord(annotation)) {
                    continue;
                }
                list.add(temp);
            }
        }
        Method[] results=new Method[list.size()];
        int i=0;
        for(Method temp:list){
            results[i]=temp;
            i++;
        }
        return results;
    }


    /**
     * 判断annotation是否已经还有注解
     *
     * @param annotation
     * @return
     */
    private boolean hasKeyWord(Annotation annotation) {
        Iterator<String> iterator = KEY_SET.iterator();
        String targetString = annotation.toString();
        while (iterator.hasNext()) {
            if (targetString.startsWith(iterator.next())) {
                System.out.println("target="+targetString);
                return true;
            }
        }
        return false;
    }
}
