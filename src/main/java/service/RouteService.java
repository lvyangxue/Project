package service;

import domain.Route;
import domain.RouteImg;

import java.util.List;
//此接口规范route的各种操作
public interface RouteService {

    //根据rid获取单个route对象
    Route getRouteByRid(int rid);
}
