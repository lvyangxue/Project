package dao.Impl;

import dao.RouteDao;
import domain.Route;
import domain.RouteImg;
import domain.Seller;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
//route操作数据库实现类
public class RouteDaoImpl implements RouteDao {
    private QueryRunner qr=new QueryRunner();

    //指定rid，查询详细信息
    @Override
    public List<RouteImg> getDetails(int rid) {
        Connection connection= DruidUtils.getconnection();
        String sql="select * from tab_route_img where rid=?";
        List<RouteImg> query = null;
        try {
            query = qr.query(connection, sql, new BeanListHandler<RouteImg>(RouteImg.class), rid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return query;
    }
//根据rid查询对应route对象
    @Override
    public Route getRouteByRid(int rid) {
        Connection connection=DruidUtils.getconnection();
        String sql="select * from tab_route where rid=?";
        Route query = null;
        try {
            query = qr.query(connection, sql, new BeanHandler<>(Route.class), rid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return query;
    }
//根据sid查询seller对象
    @Override
    public Seller getSeller(int sid) {
        Connection connection=DruidUtils.getconnection();
        String sql="select * from tab_seller where sid=?";
        Seller query = null;
        try {
            query = qr.query(connection, sql, new BeanHandler<>(Seller.class), sid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return query;
    }
//根据rid查询该route被收藏了多少次
    @Override
    public int getCountByRid(int rid) {
        Connection connection=DruidUtils.getconnection();
        String sql="select count(*) from tab_favorite where rid=?";
        Object query=null;
        try {
            query = qr.query(connection, sql, new ScalarHandler(), rid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        int result=Integer.parseInt(query.toString());
        return result;
    }
    //更新指定rid的route收藏次数
    @Override
    public void updateCount(int count,int rid) {
        Connection connection=DruidUtils.getconnection();
        String sql="update tab_route set count=? where rid=?";
        try {
            qr.update(connection,sql,count,rid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
    }
}
