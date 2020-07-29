package servlet;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


//此Servlet用户模块Servlet的方法分发不需要被访问到
public class BaseServlet extends HttpServlet {
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       //步骤1.获取客户端访问的路径
        String uri = req.getRequestURI();
        //步骤2.获取uri中最后位置的名称作为方法名
        //以最后出现的/位置索引+1作为截取的起始位置进行截取uri获取最后部分
        String methodName = uri.substring(uri.lastIndexOf("/") + 1);
        //步骤3.获取方法对象
        //3.1获取UserServlet的字节码文件对象
        //3.2然后调用getDeclaredMethod(忽略访问权限修饰符)方法传入获取的methodName以及该方法参数类型的字节码文件，从而获取UserServlet中同名方法的反射对象
        try {
            Method method = this.getClass().getMethod(methodName, HttpServletRequest.class, HttpServletResponse.class);
            //4.执行该方法对象
            try {
                method.invoke(this,req,resp);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    //将数据转换为json对象并作为响应数据发送的方法抽取
    public void wirteValue(Object obj,HttpServletResponse response) throws IOException {
       ObjectMapper mapper=new ObjectMapper();
        String json = mapper.writeValueAsString(obj);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(json);
    }
}
