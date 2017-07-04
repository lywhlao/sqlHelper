package com.netease.activity.output;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 写文件工具
 * Created by hzlaojiaqi on 2016/10/11.
 */
public class FileUtil {


    public static void writeSql(String sqlContent,String path) throws IOException {
        File file = new File(path);
        if(!file.exists()){
            boolean result=file.createNewFile();
            if(!result){
                throw  new RuntimeException("can not create file");
            }
        }
        try  {
            // 打开一个写文件器，构造函数中的第二个参数true表示以追加形式写文件
            FileWriter writer = new  FileWriter(path,  true );
            writer.write(sqlContent);
            writer.close();
        } catch  (IOException e) {
            e.printStackTrace();
        }
    }

}
