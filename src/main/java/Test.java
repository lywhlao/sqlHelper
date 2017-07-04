import com.netease.activity.core.SqlUtil;
import com.netease.activity.sample.UserEntity;

import java.io.File;

/**
 * Created by hzlaojiaqi on 2017/7/4.
 */
public class Test {

    public static void main(String[] args) {
        String path=System.getProperty("user.dir")+File.separator+"schema.sql";
        Class [] classes={UserEntity.class};
        SqlUtil.writeSqlToFile(classes,path);
    }
}
