package dao;

import domain.User;

//此接口用于实现,用户注册的相关数据库操作
public interface UserDao {
    //用户注册,返回受影响行数
    public int addUser(User user);
    //根据用户名获取用户信息
    public User findByUsername(String username);
    //根据激活码寻找用户
   public User findByCode(String code);
    //更新用户的激活状态
    public int updateStatus(int uid);
    //将用户数据存入redis
   public void addRedis(User user);
    //将指定User的数据从redis中获取，并存入该User中
   public User getUserByRedis(User user);
//根据用户名密码查询User并返回user对象
   public User findByUsernameAndPassword(String username, String password);
}
