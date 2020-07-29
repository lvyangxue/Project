package service;

import domain.Category;
import domain.PageBean;
import domain.Route;
import sun.jvm.hotspot.debugger.Page;

import java.util.List;

//此接口规定旅游类别信息的获取
public interface CategoryService {
    List<Category> getAllCategory();

    //根据cid,currentPage,rname获取数据库中相关的route对象集合,进行分页展示
    PageBean<Route> findRouteByCidAndPage(int cid,int currentPage,int rows,String rname);
}
