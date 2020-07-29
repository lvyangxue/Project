package service.Impl;

import dao.Impl.RouteDaoImpl;
import dao.RouteDao;
import domain.Route;
import domain.RouteImg;
import domain.Seller;
import service.RouteService;

import java.util.List;

public class RouteServiceImpl implements RouteService {
    private RouteDao rd=new RouteDaoImpl();
    //根据rid获取单个route对象
    @Override
    public Route getRouteByRid(int rid) {
        //根据rid查询该route的详情图片集合
        List<RouteImg> imgList = rd.getDetails(rid);
        //根据rid查询对应的route对象
        Route route = rd.getRouteByRid(rid);
        //根据此route的sid查找对应的seller
        Seller seller=rd.getSeller(route.getSid());
        //根据rid查询该route的收藏次数
        int count=rd.getCountByRid(rid);
        route.setCount(count);
        route.setSeller(seller);
        route.setRouteImgList(imgList);
        return route;
    }


}
