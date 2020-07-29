package service.Impl;

import dao.CategroyDao;
import dao.Impl.CategroyDaoImpl;
import domain.Category;
import domain.PageBean;
import domain.Route;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;
import service.CategoryService;
import sun.jvm.hotspot.debugger.Page;
import utils.JedisPoolUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CategoryServiceImpl implements CategoryService {
    CategroyDao cd=new CategroyDaoImpl();
    @Override
    public List<Category> getAllCategory() {
        //创建一个集合存放查询数据
        List<Category> list=new ArrayList<>();
        //先从redis中查询
        Jedis jedis = JedisPoolUtils.getJedis();
        //查询score和value
        Set<Tuple> category = jedis.zrangeWithScores("category", 0, -1);
        if(category==null||category.size()==0){
            //redis中没有数据，第一次访问，从数据库中去查询
            list=cd.getAllCategory();
            //并将结果存入redis中，且按照id顺序
            for(Category c:list){
                jedis.zadd("category",c.getCid(),c.getCname());
            }
            //最后返回查询结果
            return list;
        }
        //redis中存在数据 score对应cid ,value对应cname
        for(Tuple tuple:category){
            //创建Category对象，存储redis中遍历的数据
            Category c=new Category();
            c.setCname(tuple.getElement());
            c.setCid((int) tuple.getScore());
            //存入集合
            list.add(c);
        }
        return list;
    }

    //根据cid,currentPage获取数据库中相关的route对象集合,进行分页展示
    @Override
    public PageBean<Route> findRouteByCidAndPage(int cid,int currentPage,int rows,String rname) {
        PageBean<Route> pageBean=new PageBean<>();
        //根据参数查询分页展示对象数据
        List<Route> list=cd.findRouteByCidAndPage(cid,currentPage,rows,rname);
        //根据拼接后的sql2查询数据总数量
        int totalCount=cd.getPageAndCount(cid,rname);
        //根据数据总量和每页数据量计算总页数
        int totalPage=totalCount%rows==0? totalCount/rows:totalCount/rows+1;
        //将数据封装进PageBean对象
        pageBean.setTotalCount(totalCount); //总记录数
        pageBean.setTotalPage(totalPage); //总页数
        pageBean.setCid(cid);//当前类别id
        pageBean.setRow(rows); //每页显示数据数
        pageBean.setList(list); //每页显示数据对象集合
        pageBean.setCurrentPage(currentPage);
        return pageBean;
    }
}
