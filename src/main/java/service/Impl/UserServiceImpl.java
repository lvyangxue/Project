package service.Impl;

import dao.Impl.UserDaoImpl;
import dao.UserDao;
import domain.User;
import redis.clients.jedis.Jedis;
import service.UserService;
import utils.JedisPoolUtils;
import utils.MailUtils;
import utils.UuidUtil;

import java.util.Map;

public class UserServiceImpl implements UserService {
    private Jedis jedis= JedisPoolUtils.getJedis();
    private UserDao ud=new UserDaoImpl();
    //检验用户名是否存在以及密码是否正确
    @Override
    public User login(User user) {
        //先从redis中查找该登陆用户及该用户名对应的密码
        String password = jedis.hget(user.getUsername(),"password");
        if(password==null){
            //jedis中不存在去数据库中寻找
            User user1 = ud.findByUsername(user.getUsername());
            //判断二者密码是否相等
            if(user.getPassword().equalsIgnoreCase(user1.getPassword())){
                //密码相等 返回true,并将user1信息写入redis中
                ud.addRedis(user1);
                return user1;
            }
            //密码不相等直接返回null
            return null;
        }
        //该用户名在redis中存在,比较密码是否相同
        if(user.getPassword().equals(password)){
            //密码相同将redis中该用户数据封装给user对象,返回该user
            User user2 = ud.getUserByRedis(user);
            return user2;
        }
        //密码不相同直接返回null
        return null;
    }
    //判断账号是否已经激活
    @Override
    public boolean checkActive(User user) {
        //从redis中查找该用户的激活状态
        String status = jedis.hget(user.getUsername(), "status");
        if("Y".equals(status)){
            //代表用户已经激活
            return true;
        }
        //用户未激活
        return false;
    }
    //根据用户名实现用户的自动登录
    @Override
    public User autologin(String username,String password) {
        //创建一个User
        User user=new User();
        user.setUsername(username);
        //先从redis中寻找有无该username对应的用户
        Map<String, String> map = jedis.hgetAll(username);
        if(map!=null){
            //redis中存在，将redis中该用户数据封装到user中
            User user1 = ud.getUserByRedis(user);
            return user1;
        }
        //redis中没有再从数据库中查找
        user=ud.findByUsernameAndPassword(username,password);
        //并将user存入redis
        ud.addRedis(user);
        return user;
    }
    @Override
    public int usernameCheck(String username) {
        //先查找redis中是否存在用户名
        String username1 = jedis.hget(username, username);
        if(username1==null){
            //redis中不存在继续在数据库中寻找
            User user = ud.findByUsername(username);
            if(user==null){
                //用户名不存在返回0
                return 0;
            }
        }
        //用户名存在返回1
        return 1;
    }

    //此方法用于用户的创建
    @Override
    public int userRegister(User user) {
        //在添加用户前先进行用户名判断
        int i = usernameCheck(user.getUsername());
        if(i==0){
            //用户名不存在
            //设置该用户的激活状态为N
            user.setStatus("N");
            //设置该用户唯一绑定的激活码
            String code= UuidUtil.getUuid();
            user.setCode(code);
            //将user信息进行添加数据库
            int result = ud.addUser(user);
            //注册成功向用户注册的邮箱发送一封激活邮件,code为用户生成的唯一激活码
            String email="<a href='http://localhost/travel/user/activeUser?code="+code+"'>点击链接激活账号</a>";
            MailUtils.sendMail(user.getEmail(),email,"激活账号");
            //将用户名信息存入redis中,用户id作为键 Value为hash类型
            ud.addRedis(user);
            return result;
        }
        //用户名存在
        return 0;
    }

    @Override
    public boolean activeUser(String code) {
        //根据激活码去寻找数据库中用户是否存在,返回一个user对象
        User user=ud.findByCode(code);
        if(user!=null){
            //用户存在代表用户进行激活,设置该用户的激活状态为Y
            int i=ud.updateStatus(user.getUid());
            //更新该用户在redis中的激活状态
            jedis.hset(user.getUsername(),"status","Y");
            //返回true激活成功
            return true;
        }
        //用户不存在，代表激活码时错误的
        return false;
    }
}
