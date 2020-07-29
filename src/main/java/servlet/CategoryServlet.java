package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.Category;
import domain.PageBean;
import domain.Route;
import service.CategoryService;
import service.Impl.CategoryServiceImpl;
import sun.jvm.hotspot.debugger.Page;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/category/*")
public class CategoryServlet extends BaseServlet {
    private  CategoryService service=new CategoryServiceImpl();
//获得所有的旅游项目
    public void getAllCategory(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException{
        //调用Service获取所有旅游类别
        List<Category> categoryList=service.getAllCategory();
        //将该集合对象转为json数据
        wirteValue(categoryList,response);
    }
//根据cid和currentPage,rows,rname展示当前页面从数据查询的数据
    public void routeByCidAndPage(HttpServletRequest request,HttpServletResponse response)throws ServletException, IOException{
        //设置请求数据编码方式
        request.setCharacterEncoding("UTF-8");
        //获取cid数据 类别id
        String cid = request.getParameter("cid");
        //对cid进行一个判断
        if(cid==null){
            //若cid为null 默认赋值为0
            cid="0";
        }
        //获取currentPage
        String currentPage = request.getParameter("currentPage");
        if(currentPage==null){
            //没有传入current默认为第一页
            currentPage="1";
        }
        //获取rows每页数据展示量
        String rows=request.getParameter("rows");
        //获取搜索信息rname
        String rname=request.getParameter("rname");
        //获取用户查询的rname
        if("null".equals(rname)){
            //用户没有输入ranme，则传递的值为 “null” 将其转变为null
            rname=null;
        }
        //调用service查询该cid以及搜索框中搜索内容进行对应的数据展示
        PageBean<Route> pageBean=service.findRouteByCidAndPage(Integer.parseInt(cid),Integer.parseInt(currentPage),Integer.parseInt(rows),rname);
        //将数据转为json作为响应数据发送
        wirteValue(pageBean,response);
    }

}
