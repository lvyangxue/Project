package dao;

import domain.Category;
import domain.Route;

import java.util.List;

public interface CategroyDao {
    //获取数据库中所有类别
    List<Category> getAllCategory();
    //根据cid,currentPage获取数据库中相关的route对象集合,进行分页展示
    List<Route> findRouteByCidAndPage(int cid,int currentPage,int rows,String rname);
    //根据指定sql获取数据总量
    int getPageAndCount(int cid,String rname);
}
