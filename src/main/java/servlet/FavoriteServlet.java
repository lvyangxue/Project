package servlet;

import domain.PageBean;
import domain.ResultInfo;
import domain.Route;
import domain.User;
import service.FavoriteService;
import service.Impl.FavoriteServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

@WebServlet("/favorite/*")
public class FavoriteServlet extends BaseServlet {
    private  FavoriteService service=new FavoriteServiceImpl();
    //此类对象用于提示信息
    private ResultInfo info=new ResultInfo();
    //展示当前详情页面route是否可被用户收藏
    public void isFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取当前用户登录对象
        HttpSession session = request.getSession();
        User user =(User) session.getAttribute("user");
        //没有登录默认uid为0
        int uid=0;
        //对用户是否登录做一个判断
        if(user!=null){
            //用户已经登录，uid为登路用户的uid
            uid=user.getUid();
        }
        //获取当前route详情页面的rid
        String rid = request.getParameter("rid");
        //调用service 查询当前用户是否已经收藏该route
        boolean flag=service.isFavorite(uid,Integer.parseInt(rid));
        if(flag){
            //为true代表用户可以收藏此route
            info.setFlag(flag);
        }else {
            //用户已经收藏过 flag为false
            info.setFlag(flag);
            info.setErrorMsg("您已经收藏过该路线");
            //将info转为json作为响应数据
        }

        wirteValue(info,response);
    }
//将当前详情页route添加到用户收藏
public void addFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    //获取当前用户登录对象
    HttpSession session = request.getSession();
    User user =(User) session.getAttribute("user");
        //创建一个Date对象 作为收藏日期
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String formatDate = sdf.format(date);
        //获取当前route详情页面的rid
        String rid = request.getParameter("rid");
        //调用service进行收藏
        boolean flag = service.addFavorite(user.getUid(), Integer.parseInt(rid), formatDate);
        if (flag) {
            //flag为true代表收藏成功
            info.setFlag(true);
            info.setErrorMsg("收藏成功");
        } else {
            //false收藏失败
            info.setFlag(false);
            info.setErrorMsg("收藏失败");
        }
    //}
    //将info转为json作为响应数据
    wirteValue(info,response);
}

//查看当前登录用户已经收藏的旅游项目
    public void myFavorite(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取当前用户登录信息
        HttpSession session = request.getSession();
        User user = (User)session.getAttribute("user");
        //获取当前页currentPage
        String currentPage_str = request.getParameter("currentPage");
        int currentPage=1;
        //对currentPage做判断
        if(currentPage_str!=null){
            //有传入，currentPage进行数据类型转换
           currentPage=Integer.parseInt(currentPage_str);
        }
        //获取每页展示数据量信息
        String rows_str=request.getParameter("rows");
        //没有传入数据默认每页展示10
        int rows=10;
        if(rows_str!=null){
            rows=Integer.parseInt(rows_str);
        }
        //调用service查询当前用户收藏，返回一个PageBean对象，对收藏的route数据进行展示
        PageBean<Route> pageBean=service.myFavorite(user.getUid(),currentPage,rows);
        //将pageBean转为json作为响应数据发送
        wirteValue(pageBean,response);

    }
    //按照用户输入的关键信息对排行榜所有route进行按照收藏次数降序分页查询
    public void favoriteRank(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        //获取用户查询的rname
        String rname = request.getParameter("rankname");
        if("null".equals(rname)){
            //用户没有输入ranme，则传递的值为 “null” 将其转变为null
            rname=null;
        }
        //获取用户输入的minMoney
        String minMoney_str = request.getParameter("minMoney");
        int minMoney=0;//用户没有输入minMoney默认为0
        if(minMoney_str!=null&&!("".equals(minMoney_str))){
            //用户输入了minMoney进行类型转换
            minMoney=Integer.parseInt(minMoney_str);
        }
        //获取用户输入的maxMoney
        String maxMoney_str = request.getParameter("maxMoney");
        int maxMoney=0;//用户没有输入minMoney默认为0
        if(maxMoney_str!=null&&!("".equals(maxMoney_str))){
            //用户输入了minMoney进行类型转换
            maxMoney=Integer.parseInt(maxMoney_str);
        }
        //获取当前页currentPage
        String currentPage_str = request.getParameter("currentPage");
        int currentPage=1;//默认为第一页
        //对currentPage做判断
        if(currentPage_str!=null){
            //有传入，currentPage进行数据类型转换
            currentPage=Integer.parseInt(currentPage_str);
        }
        //获取每页展示数据量信息
        String rows_str=request.getParameter("rows");
        //没有传入数据默认每页展示8个数据
        int rows=8;
        if(rows_str!=null){
            rows=Integer.parseInt(rows_str);
        }
        PageBean<Route> pageBean=service.favoriteRank(rname,minMoney,maxMoney,currentPage,rows);
        //将数据转换为json数据作为响应数据发送
        wirteValue(pageBean,response);
    }

}
