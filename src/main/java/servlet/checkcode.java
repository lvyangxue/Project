package servlet;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

//此类用于动态验证码的生成

public class checkcode extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        //设置图片的尺寸
        int width=100;
        int height=50;
        //1.创建一个对象，在内存中代表图片
        BufferedImage bi=new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        //2.美化图片
        //2.1填充背景色
        Graphics graphics = bi.getGraphics();//获取画笔对象
        graphics.setColor(Color.PINK);//设置画笔颜色
        graphics.fillRect(0,0,width,height);//填充背景色 范围为图片大小
        //2.2画边框
        graphics.setColor(Color.BLUE);
        graphics.drawRect(0,0,width-1,height-1);
        //设置验证码总集
        String str="ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz01234567890";
        //产生一个随机数去随机获取str中的一个作为验证码元素
        StringBuilder sb=new StringBuilder(); //用于存储产生验证码的正确值
        Random r=new Random();
        for (int i = 1; i <= 4; i++) {
            int index = r.nextInt(str.length());//以该str的长度作为最大值保证产生的随机数能对应str中的某个字符
            //获取角标对应的字符
            char c = str.charAt(index);
            sb.append(c);
            //2.3写验证码,并设置该验证码在图片中的坐标
            graphics.drawString(c+"",width/5*i,height/2);//c+""将字符转为string形式  并且动态坐标将该字符显示在图片上
        }
        //将sb转换为String
        String s = sb.toString();
        //创建一个session存储验证码的正确值用于验证
        HttpSession session = request.getSession();
        session.setAttribute("checkcode",s);
        //2.4画干扰线
        graphics.setColor(Color.green);//选择要画干扰线的颜色
        //循环确定干扰线的数目
        for (int i = 0; i < 4; i++) {
            //随机生成坐标点确定画线的位置
            int x1 = r.nextInt(width); //x1,y1个点的坐标  x2，y2第二点坐标 两点确定一条直线
            int x2 = r.nextInt(width);
            int y1 = r.nextInt(height); //x坐标不超出图片尺寸width y坐标不超出图片尺寸height
            int y2 = r.nextInt(height);
            graphics.drawLine(x1,x2,y1,y2);
        }
        //3.将页面输出展示
        ImageIO.write(bi,"jpg",response.getOutputStream());

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }
}
