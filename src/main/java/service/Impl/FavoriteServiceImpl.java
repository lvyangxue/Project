package service.Impl;

import dao.FavoriteDao;
import dao.Impl.FavoriteDaoImpl;
import dao.Impl.RouteDaoImpl;
import dao.RouteDao;
import domain.Favorite;
import domain.PageBean;
import domain.Route;
import domain.User;
import service.FavoriteService;

import java.util.ArrayList;
import java.util.List;

//收藏操作接口实现类
public class FavoriteServiceImpl implements FavoriteService {

    private FavoriteDao fd=new FavoriteDaoImpl();
    private RouteDao rd=new RouteDaoImpl();
    //判断当前uid用户是否已经收藏此rid对应的route
    @Override
    public boolean isFavorite(int uid, int rid) {
        //根据参数对数据库进行查询
        boolean flag=fd.isFavorite(uid,rid);
        return flag;
    }
    //将此rid对应的route添加的此uid用户的收藏夹
    @Override
    public boolean addFavorite(int uid, int rid,String formatDate) {

        boolean flag=fd.addFavorite(uid,rid,formatDate);
        //并且更新此rid的总共收藏次数count
        int count = rd.getCountByRid(rid);
        rd.updateCount(count,rid);
        return flag;
    }
    //根据登录用户查看该用户已经收藏项目
    @Override
    public PageBean<Route> myFavorite(int uid,int currentPage,int rows) {
        PageBean<Route> pageBean=new PageBean<>();
        pageBean.setCurrentPage(currentPage);
        pageBean.setRow(rows);
        //查询该uid收藏的rid总数量
        int totalCount=fd.ridCountByUid(uid);
        //存入PageBean
        pageBean.setTotalCount(totalCount);
        //收藏总页数
        int totalPage=totalCount%rows==0?totalCount/rows:totalCount/rows+1;
        //存入pagebean
        pageBean.setTotalPage(totalPage);
        //根据uid,currentPage,rows分页查询该uid所有收藏线路的rid
        List<Favorite> favoriteList=fd.findRidByUid(uid,currentPage,rows);
        //遍历rid集合，查询rid对应的route对象存入List<Route>集合
        List<Route> routeList = new ArrayList<>();
        for(Favorite i:favoriteList){
            //查询每个rid对应的route
            Route route = rd.getRouteByRid(i.getRid());
            //将route存入集合，即为该用户所有搜查的线路
            routeList.add(route);
        }
        //存入pagebean
        pageBean.setList(routeList);
        return pageBean;
    }
    //按照用户输入的关键信息对排行榜所有route进行按照收藏次数降序分页查询
    @Override
    public PageBean<Route> favoriteRank(String rname,int minMoney,int maxMoney,int currentPage,int rows) {
        PageBean<Route> pageBean=new PageBean<>();
        //设置pageBean的currentPage
        pageBean.setCurrentPage(currentPage);
        //设置pageBean的rows
        pageBean.setRow(rows);
        //根据条件查询排行榜的总数据量
        int totalCount = fd.favoriteRankCount(rname, minMoney, maxMoney);
        //设置pageBean的totalCount
        pageBean.setTotalCount(totalCount);
        //计算总页数
        int totalPage=totalCount%rows==0? totalCount/rows:totalCount/rows+1;
        //设置pageBean的totalPage
        pageBean.setTotalPage(totalPage);
        //设置pageBean的list
        List<Route> routes = fd.favoriteRank(rname,minMoney,maxMoney,currentPage,rows);
        pageBean.setList(routes);
        //返回pagebean
        return pageBean;
    }
}
