package dao;

import domain.Route;
import domain.RouteImg;
import domain.Seller;

import java.util.List;
//对route进行数据库的相关操作接口
public interface RouteDao {
    //获取路线详细信息
    List<RouteImg> getDetails(int rid);
    //获取指定route对象
    Route getRouteByRid(int rid);
   //根据route的sid获取seller对象
    Seller getSeller(int sid);
    //根据rid获取该route被收藏总数
    int getCountByRid(int rid);
    //更新route的被收藏次数
    void updateCount(int count,int rid);
}
