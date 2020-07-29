package dao;

import domain.Favorite;
import domain.Route;

import java.util.List;

//收藏操作查询数据库接口
public interface FavoriteDao {
    //判断当前uid用户是否已经收藏此rid对应的route
    boolean isFavorite(int uid, int rid);
    //将此rid对应的route添加的此uid用户的收藏夹
    boolean addFavorite(int uid, int rid,String formatDate);
    //根据登录用户uid分页查看该用户已经收藏项目
    List<Favorite> findRidByUid(int uid, int CurrentPage, int rows);
    //查询该uid收藏的rid总数量
    int ridCountByUid(int uid);
    //按照用户输入的关键信息对排行榜所有route进行按照收藏次数降序分页查询
    List<Route> favoriteRank(String rname,int minMoney,int maxMoney,int currentPage,int rows);
    //查询按照输入信息查询排行榜符合要求的总数据
    int favoriteRankCount(String rname,int minMoney,int maxMoney);
}
