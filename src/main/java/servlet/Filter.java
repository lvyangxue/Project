package servlet;

import domain.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter(value = "/*",dispatcherTypes = {DispatcherType.ASYNC,DispatcherType.REQUEST})
public class Filter implements javax.servlet.Filter {
    public void destroy() {
    }

    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletRequest request=(HttpServletRequest) req;
        HttpServletResponse response=(HttpServletResponse) resp;
        //获取用户访问资源路径
        String uri = request.getRequestURI();
        //判断是否访问需要登录的路径
        if(uri.contains("myFavorite")||uri.contains("addFavorite")){
            //访问需要登录路径，获取session域中用户的登录信息
            HttpSession session = request.getSession();
            User user= (User)session.getAttribute("user");
            if(user==null){
                //没有登录，直接结束 在前端提示未登录
                System.out.println("没登陆");
            } else {
                //有登录，放行
                chain.doFilter(request, response);
            }
        }else {
            //不是访问需要登录的资源 放行
            chain.doFilter(request, response);
        }

    }

    public void init(FilterConfig config) throws ServletException {

    }

}
