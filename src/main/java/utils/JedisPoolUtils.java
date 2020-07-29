package utils;


import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
/*
        Jedis连接池工具类
        加载配置文件，配置连接池的参数
        提供获取连接的方法
 */
public class JedisPoolUtils {
      private   static JedisPool jedisPool;


      //静态代码块，当类加载的时候 就读取配置文件
    static {
        //读取配置文件
        InputStream is = JedisPoolUtils.class.getClassLoader().getResourceAsStream("JedisPool.properties");
        //创建Properties对象，加载流中数据
        Properties p=new Properties();
        try {
            p.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //获取配置文件中的键值对数据作为JediesPool的参数
        String maxtotal_str =(String)p.get("MaxTotal");
        int maxtotal=Integer.parseInt(maxtotal_str);
        String maxIdle_str = (String)p.get("MaxIdle");
        int maxIdle=Integer.parseInt(maxIdle_str);
        String ip=(String)p.get("ip");
        String port_str=(String)p.get("port");
        int port=Integer.parseInt(port_str);
        //创建连接池配置对象
        JedisPoolConfig config=new JedisPoolConfig();
        config.setMaxTotal(maxtotal);
        config.setMaxIdle(maxIdle);
        //创建连接池对象
        jedisPool=new JedisPool(config,ip,port);
    }

    public static Jedis getJedis(){
        Jedis jedis = jedisPool.getResource();
        return jedis;
    }

}
