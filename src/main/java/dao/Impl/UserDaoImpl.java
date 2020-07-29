package dao.Impl;

import dao.UserDao;
import domain.User;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import redis.clients.jedis.Jedis;
import utils.DruidUtils;
import utils.JedisPoolUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

//此类实现UserRegister接口
public class UserDaoImpl implements dao.UserDao {
    private  QueryRunner qr=new QueryRunner();
    private Jedis jedis= JedisPoolUtils.getJedis();
    //用户新增
    @Override
    public int addUser(User user) {
        Connection connection=DruidUtils.getconnection();
        //新增用户sql语句
        String sql="insert into tab_user(username,password,name,birthday,sex,telephone,email,status,code)" +
                "value(?,?,?,?,?,?,?,?,?)";
        //执行sql语句
        int result=0;
        try {
            result = qr.update(connection, sql, user.getUsername(), user.getPassword(), user.getName(), user.getBirthday(), user.getSex(), user.getTelephone(), user.getEmail(),user.getStatus(),user.getCode());

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return result;
    }
//获取用户在数据库中的信息
    @Override
    public User findByUsername(String username) {
        Connection connection=DruidUtils.getconnection();
        String sql="select * from tab_user where username=?";
        User user=null;
        try {
            user = qr.query(connection, sql, new BeanHandler<>(User.class), username);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return user;
    }
//根据激活码寻找唯一user
    @Override
    public User findByCode(String code) {
        Connection connection=DruidUtils.getconnection();
        String sql="select * from tab_user where code=?";
        User query = null;
        try {
            query = qr.query(connection, sql, new BeanHandler<>(User.class), code);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return query;
    }
//用户点击链接激活，更新激活状态
    @Override
    public int updateStatus(int uid) {
        Connection connection=DruidUtils.getconnection();
        String sql="update tab_user set status='Y' where uid=?";
        int result = 0;
        try {
            result = qr.update(connection, sql, uid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            DruidUtils.shutdown(connection,null,null);
        }
        return result;
    }
//将用户数据存入redis
    @Override
    public void addRedis(User user) {
        //将用户名信息存入redis中,用户id作为键 Value为hash类型
        jedis.hset(user.getUsername(),"username",user.getUsername());
        jedis.hset(user.getUsername(),"uid",String.valueOf(user.getUid()));
        jedis.hset(user.getUsername(),"password", user.getPassword());
        jedis.hset(user.getUsername(),"name", user.getName());
        jedis.hset(user.getUsername(),"birthday", user.getBirthday());
        jedis.hset(user.getUsername(),"sex", user.getSex());
        jedis.hset(user.getUsername(),"telephone", user.getTelephone());
        jedis.hset(user.getUsername(),"email", user.getEmail());
        jedis.hset(user.getUsername(),"status", user.getStatus());
        jedis.hset(user.getUsername(),"code", user.getCode());
        //关闭资源
        jedis.close();
    }
//将redis中的用户数据封装为User对象
    @Override
    public User getUserByRedis(User user) {
        Map<String, String> map= jedis.hgetAll(user.getUsername());
        user.setUsername(map.get("username"));
       user.setUid(Integer.parseInt(map.get("uid")));
        user.setPassword(map.get("password"));
        user.setName(map.get("name"));
        user.setBirthday(map.get("birthday"));
        user.setSex(map.get("sex"));
        user.setTelephone(map.get("telephone"));
        user.setEmail(map.get("email"));
        user.setStatus(map.get("status"));
        user.setCode(map.get("code"));
        return user;
    }
    //根据用户名密码查询User并返回user对象
    @Override
    public User findByUsernameAndPassword(String username, String password) {
        Connection connection=DruidUtils.getconnection();
        String sql="select * from tab_user where username=? and password=?";
        User user=null;
        try {
            user = qr.query(connection, sql, new BeanHandler<>(User.class), username, password);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            DruidUtils.shutdown(connection,null,null);
        }
        return user;
    }
}
