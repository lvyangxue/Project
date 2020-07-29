package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import domain.ResultInfo;
import domain.User;
import org.apache.commons.beanutils.BeanUtils;
import service.Impl.UserServiceImpl;
import service.UserService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

@WebServlet("/user/*")
public class UserServlet extends BaseServlet {
    private UserService service=new UserServiceImpl();
    //激活用户
    public void activeUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取激活码
        String code = request.getParameter("code");
        if (code != null) {
            //根据激活码查找用户
            boolean result = service.activeUser(code);
            String msg = null;
            if (result) {
                //激活成功
                msg = "激活成功，请<a href='" + request.getContextPath() + "/login.html'>登录</a>";
            } else {
                //激活失败
                msg = "请联系管理员";
            }
            //设置响应数据格式和编码,防止提示信息中文乱码
            response.setContentType("text/html;charset=utf-8");
            //对客户端发送响应数据
            response.getWriter().write(msg);
        }
    }
    //用户自动登录
    public void autoLogin(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = "";
        String password = "";
        User user = null;
        //获取请求头中Cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie c : cookies) {
                if ("username".equals(c.getName())) {
                    //有cookie key为username的代表用户设置了自动登录
                    //设置usename为登录用户的用户名
                    username = c.getValue();
                }
                if ("password".equals(c.getName())) {
                    //设置password为登录用户的密码
                    password = c.getValue();
                }
            }
        }else {
            //Cookie为null没有设置登录
            return;
        }
        //有设置登录，对用户名和密码进行查询
        if (username != "" && password != "") {
            //调用service，根据用户名和密码查询登录user
            user = service.autologin(username, password);
            //将user对象存入session中
            HttpSession session = request.getSession();
            session.setAttribute("user",user);
        }
        //将user转为json字符串作为响应数据发送
        wirteValue(user,response);
    }
    //判断用户是否进行了登录
    public void findUser(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //获取session域中的User对象
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        //将user转变为json字符串作为响应数据
       wirteValue(user,response);
    }
    //用户登录
    public void login(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //创建ResultInfo对象，用以提示注册相关信息
        ResultInfo info = new ResultInfo();
        //首先获取用户输入的验证码
        String codeinput = request.getParameter("checkcode");
        //获取session域中存放自动生成的正确验证码
        HttpSession session = request.getSession();
        String rightcode = (String) session.getAttribute("checkcode");
        //获取后移除session验证码共享数据，防止验证码的复用
        session.removeAttribute("checkcode");
        if (codeinput == "" || !codeinput.equalsIgnoreCase(rightcode)) {
            //未输入验证码，或验证码不正确
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
        } else {
            //验证码正确，获取账号密码进行校验
            Map<String, String[]> loginmap = request.getParameterMap();
            //创建一个User对象
            User user = new User();
            //将登录数据封装到user中
            try {
                BeanUtils.populate(user, loginmap);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
            //进行判断
            User user1 = service.login(user);
            //判断登录用户账号密码是否正确以及账号是否激活
            if (user1 != null && "Y".equals(user1.getStatus())) {
                //用户信息正确账号已经激活,可用正常登陆
                info.setFlag(true);
                //将用户对象存入Session中
                session.setAttribute("user",user1);
                //判断用户有无设置自动登录
                String autologin = request.getParameter("autologin");
                if("1".equals(autologin)){
                    //代表用户设置了自动登录，将用户登录信息存放在cookie中
                    Cookie cookie_username=new Cookie("username",user1.getUsername());
                    Cookie cookie_password=new Cookie("password",user1.getPassword());
                    //设置cookie的持久化存储7天
                    cookie_username.setMaxAge(60*60*24*7);
                    cookie_password.setMaxAge(60*60*24*7);
                    response.addCookie(cookie_password);
                    response.addCookie(cookie_username);
                }
            }
            if (user1 != null && "N".equals(user1.getStatus())) {
                //用户未激活提示到邮箱激活账号
                info.setFlag(false);
                info.setErrorMsg("账户未激活，请到邮箱激活后进行登录操作");
            }
            if (user1 == null) {
                //用户名不存在或者密码错误
                info.setFlag(false);
                info.setErrorMsg("用户名不存在或密码错误");
            }
        }
        //将info转为json字符串数据作为响应数据发送给login.html
        wirteValue(info,response);
    }


    //退出登录
    public void logOut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //退出登录就是删除session域中已经登录的user对象
        HttpSession session = request.getSession();
        session.removeAttribute("user");
        //删除自动登录中的相关Cookie信息
        //设置两个同名的Cookie，覆盖原先保存的Cookie
        Cookie cookie_username=new Cookie("username",null);
        Cookie cookie_password=new Cookie("password",null);
        //发送新Cookie，防止下次访问仍旧可以自动登录
        response.addCookie(cookie_username);
        response.addCookie(cookie_password);
    }

    //用户注册
    public void register(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置请求数据字符集
        request.setCharacterEncoding("utf-8");
        //获取session域中的验证码
        HttpSession session = request.getSession();
        String rightcode =(String) session.getAttribute("checkcode");
        //获取验证码后将session域中的验证码删除，防止复用
        session.removeAttribute("checkcode");
        //获取输入的验证码
        String condeinput = request.getParameter("checkcode");
        //判断验证码是否正确
        if(condeinput==null||!condeinput.equalsIgnoreCase(rightcode)){
            //创建ResultInfo对象
            ResultInfo info=new ResultInfo();
            //验证码未输入或者验证码输入错误
            info.setFlag(false);
            info.setErrorMsg("验证码错误");
            //将info转为json字符串数据作为响应数据发送给login.html
            wirteValue(info,response);
            return;
        }
        //验证码正确获取用户的输入信息,用于创建用户
        Map<String, String[]> parameterMap = request.getParameterMap();
        //将输入数据封装为user对象
        User user=new User();
        try {
            BeanUtils.populate(user,parameterMap);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        //创建ResultInfo对象
        ResultInfo info=new ResultInfo();
        //调用Service
        int i = service.userRegister(user);
        //对i进行判断
        if(i==1){
            //数据库受影响行数为1,注册成功 跳转至register_ok页面
            //response.sendRedirect("/travel/register_ok");
            info.setFlag(true);
        } else {
            //i为0 提示注册失败
            info.setFlag(false);
            info.setErrorMsg("注册失败,用户名已存在");
        }
        //将info转为json字符串数据作为响应数据发送给login.html
        wirteValue(info,response);
    }

    //用户名检测
    public void usernameCheck(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //设置响应数据的格式为text,编码方式为utf-8
        response.setContentType("text/html;charset=utf-8");
        //获取用户异步提交的用户名
        String username = request.getParameter("username");
        //没有输入用户名
        if ("".equals(username) || username == null) {
            response.getWriter().write("1");
            return;
        }
        //调用Service判断用户名是否存在
        int i = service.usernameCheck(username);
        //对返回结果进行判断
        if (i == 1) {
            //代表用户名存在
            response.getWriter().write("1");
        } else {
            //用户名不存在
            response.getWriter().write("0");
        }

    }
}
