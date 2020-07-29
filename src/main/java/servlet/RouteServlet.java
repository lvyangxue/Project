package servlet;

import domain.Route;
import domain.RouteImg;
import service.Impl.RouteServiceImpl;
import service.RouteService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/route/*")
public class RouteServlet extends BaseServlet {
    private  RouteService service=new RouteServiceImpl();
    //根据rid获取单个route对象
    public void getRouteByRid(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取rid
        String rid=request.getParameter("rid");
        //调用service查询
        Route route=service.getRouteByRid(Integer.parseInt(rid));
        //将route作为响应数据发送
        wirteValue(route,response);
    }
}
