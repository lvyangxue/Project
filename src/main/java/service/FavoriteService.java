package service;

import domain.PageBean;
import domain.Route;
import domain.User;

//收藏相关操作接口
public interface FavoriteService {
    //判断当前uid用户是否已经收藏此rid对应的route
    boolean isFavorite(int uid, int rid);
    //将此rid对应的route添加的此uid用户的收藏夹
    boolean addFavorite(int uid, int rid,String formatDate);
    //根据登录用户查看该用户已经收藏项目
    PageBean<Route> myFavorite(int uid,int currentPage,int rows);
    //按照用户输入的关键信息对排行榜所有route进行按照收藏次数降序分页查询
    PageBean<Route> favoriteRank(String rname,int minMoney,int maxMoney,int currentPage,int rows);
}
