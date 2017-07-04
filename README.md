# sqlHelper
A tool for generating creating table script in Mysql.
###Usage
#### 1.add annotation to Java Bean
- for example:
```java
@TableName("tb_yq_user")
@Desc("用户表")
public class UserEntity {

   @PrimaryKey
   long id;
   @UniqueKey("IDX_YQ_USER_UID")
   @Desc("账号")
   String uid;
   //omit set,get method
 }
 
```
#### 2.create sql script
- create main method
- invoke SqlUtil.writeSqlToFile method
- for example:
```java
public class Test {
    public static void main(String[] args) {
        String path=System.getProperty("user.dir")+File.separator+"schema.sql";
        Class [] classes={UserEntity.class};
        SqlUtil.writeSqlToFile(classes,path);
    }
}
```
#### 3.get sql script
- we can find a file defined above(named ``schema.sql``).
- for example:
```mysql
DROP TABLE IF EXISTS `tb_yq_user`;
CREATE TABLE `tb_yq_user` ( 
		`id` bigInt(20) NOT NULL AUTO_INCREMENT COMMENT '主键' ,
		`uid` varchar(64)  DEFAULT '' COMMENT '账号',
		PRIMARY KEY (`id`),
		UNIQUE KEY `IDX_YQ_USER_UID` (`uid`)
)ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
```
