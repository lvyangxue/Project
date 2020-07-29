package service;

import domain.User;

public interface UserService {
    //此方法用于查询注册的用户名是否已经存在
    public int usernameCheck(String username);
    //此方法用于创建用户,返回数据库中受影响行数
    public int userRegister(User user);
    //激活用户账号
    boolean activeUser(String code);
    //判断用户名是否存在以及登录密码是否正确
    User login(User user);
    //判断该账号是否已经激活
    boolean checkActive(User user);
    //根据用户名和密码实现自动登录
    User autologin(String username,String password);
}
