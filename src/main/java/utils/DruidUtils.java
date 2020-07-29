package utils;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

//此类为获取数据库连接池对象的工具类
public class DruidUtils {
    static DataSource ds;
    static{
        //读取druid相关配置文件,并以输入流读取配置文件中数据
        InputStream is = DruidUtils.class.getClassLoader().getResourceAsStream("druid.properties");
        Properties p=new Properties();
        //载入is流中的数据
        try {
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取数据库连接池
        try {
            ds= DruidDataSourceFactory.createDataSource(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取数据库连接池对象方法
    public static Connection getconnection(){
        Connection connection = null;
        try {
            connection = ds.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return connection;

    }
    //关闭有关数据库的资源
    public static void shutdown(Connection connection, Statement statement, ResultSet set){
        try {
            if(set!=null){
                set.close();
            }
            if(statement!=null){
                statement.close();
            }
            if(connection!=null){
                connection.close();
            }
        } catch (SQLException throwables) {
            throw new RuntimeException();
        }
    }

}

