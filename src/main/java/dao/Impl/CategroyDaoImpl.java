package dao.Impl;

import dao.CategroyDao;
import domain.Category;
import domain.Route;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class CategroyDaoImpl implements CategroyDao {
    private QueryRunner qr=new QueryRunner();
    //查询数据库中所有category数据
    @Override
    public List<Category> getAllCategory() {
        Connection connection= DruidUtils.getconnection();
        String sql="select * from tab_category order by cid  asc";
        List<Category> query = null;
        try {
            query = qr.query(connection, sql, new BeanListHandler<Category>(Category.class));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            DruidUtils.shutdown(connection,null,null);
        }
        return query;
    }
//根据cid,currentPage,rows,rname获取数据库中相关的route对象集合,进行分页展示
    @Override
    public List<Route> findRouteByCidAndPage(int cid,int currentPage,int rows,String rname) {
        Connection connection=DruidUtils.getconnection();
        //定义模板sql,查询指定条件的所有数据信息
        String sql= "select * from tab_route where 1=1";
        //判断cid
        if(cid!=0){
            //有传入cid数据
            sql+=" and cid="+cid;
        }
        //判断用户是否进行关键字搜索
        if(rname!=null&&!"".equals(rname)){
            //用户进行了关键字搜索,对sql语句进行拼接搜索并分页展示
            rname="'%"+rname+"%'";
            sql+=" and rname like "+rname+" limit "+(currentPage-1)*rows+","+rows;
        }else {
            //用户没有关键字搜随 只对cid进行查询数据库并分页展示
            sql+=" limit "+(currentPage-1)*rows+","+rows;
            //数据总量用初始化的sql2即可
        }
        List<Route> query = null;
            try {
                query = qr.query(connection, sql, new BeanListHandler<Route>(Route.class));
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }finally {
                //关闭资源
                DruidUtils.shutdown(connection,null,null);
            }
        return query;
        }


    //根据指定cid获取该cid数据总量
    @Override
    public int getPageAndCount(int cid,String rname) {
        Connection connection=DruidUtils.getconnection();
        //初始化sql语句,查询指定条件的总数据量
        String sql= "select count(*) from tab_route where 1=1";
        //判断cid
        if(cid!=0){
            //有传入cid数据
            sql+=" and cid="+cid;
        }
        //判断用户是否进行关键字搜索
        if(rname!=null&&!"".equals(rname)){
            //用户进行了关键字搜索,对sql语句进行拼接搜索并分页展示
            rname="'%"+rname+"%'";
            sql+=" and rname like "+rname;
        }
        Object query = null;
        try {
            query = qr.query(connection, sql, new ScalarHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
           //关闭资源
           DruidUtils.shutdown(connection,null,null);
        }
        //将Object类型的结果转为String类型 在转换为Integer类型
        String result=String.valueOf(query);
        return Integer.parseInt(result);

    }
}
