package dao.Impl;

import dao.FavoriteDao;
import domain.Favorite;
import domain.Route;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import utils.DruidUtils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

//收藏操作查询数据库接口
public class FavoriteDaoImpl implements FavoriteDao {
    private QueryRunner qr=new QueryRunner();
    //判断当前uid用户是否已经收藏此rid对应的route
    @Override
    public boolean isFavorite(int uid, int rid) {
        Connection connection=DruidUtils.getconnection();
        String sql="select count(*) from tab_favorite where uid=? and rid=? ";
        Object query = null;
        try {
            query = qr.query(connection, sql, new ScalarHandler(), uid, rid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);

        }
        int result=Integer.parseInt(query.toString());
        if(result==1){
            //查询到结果代表用户已经收藏过，不能再收藏 返回false
            return false;
        }
        //没有收藏过返回true
        return true;
    }
    //将此rid对应的route添加的此uid用户的收藏夹
    @Override
    public boolean addFavorite(int uid, int rid,String formatDate) {
        Connection connection=DruidUtils.getconnection();
        String sql="insert into tab_favorite(rid,date,uid) value(?,?,?) ";
        int result=0;
        try {
             result= qr.update(connection, sql, rid, formatDate ,uid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        if(result==0){
            //收藏失败返回false
            return  false;
        }
        //收藏成功 返回true
        return true;
    }
    //根据登录用户uid分页查看该用户已经收藏项目
    @Override
    public List<Favorite> findRidByUid(int uid, int currentPage, int rows) {
        Connection connection=DruidUtils.getconnection();
        String sql="select rid from tab_favorite where uid=? limit ?,?";
        List<Favorite> query = null;
        try {
            query = qr.query(connection, sql, new BeanListHandler<>(Favorite.class), uid,(currentPage-1)*rows,rows);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return query;
    }
    //查询该uid收藏的rid总数量
    @Override
    public int ridCountByUid(int uid) {
        Connection connection=DruidUtils.getconnection();
        String sql="select count(*) from tab_favorite where uid=?";
        Object query = null;
        try {
            query = qr.query(connection, sql, new ScalarHandler(), uid);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        int result=Integer.parseInt(query.toString());
        return result;
    }
    //按照用户输入的关键信息对排行榜所有route进行按照收藏次数降序分页查询
    @Override
    public List<Route> favoriteRank(String rname,int minMoney,int maxMoney,int currentPage,int rows) {
        Connection connection=DruidUtils.getconnection();
        //定义sql模板
        StringBuilder sb=new StringBuilder("select * from tab_route where 1=1");
        if(rname!=null&&rname.length()>0){
            rname="'%"+rname+"%'";
            //用户有输入线路名称信息
            sb.append(" and rname like "+rname);
        }
        if(minMoney!=0){
            //用输入了最低价格信息
            sb.append(" and price >"+minMoney);
        }
        if(maxMoney!=0){
            //用输入了最高价格信息
            sb.append(" and price <"+maxMoney);
        }
        //进行分页与排序
        sb.append(" order by count desc limit "+(currentPage-1)*rows+","+rows);
        //将sql转为String类型
        String sql=sb.toString();
        List<Route> query = null;
        try {
            query = qr.query(connection, sql, new BeanListHandler<Route>(Route.class));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        return query;
    }

    @Override
    public int favoriteRankCount(String rname, int minMoney, int maxMoney) {
        Connection connection=DruidUtils.getconnection();
        //定义sql模板
        StringBuilder sb=new StringBuilder("select count(*) from tab_route where 1=1");
        //rn
        if(rname!=null&&rname.length()>0){
            rname="'%"+rname+"%'";
            //用户有输入线路名称信息
            sb.append(" and rname like "+rname);
        }
        if(minMoney!=0){
            //用输入了最低价格信息
            sb.append(" and price >"+minMoney);
        }
        if(maxMoney!=0){
            //用输入了最高价格信息
            sb.append(" and price <"+maxMoney);
        }
        //将sql转为String类型
        String sql=sb.toString();
        Object query=null;
        try {
            query = qr.query(connection, sql, new ScalarHandler());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            //关闭资源
            DruidUtils.shutdown(connection,null,null);
        }
        int result=Integer.parseInt(query.toString());
        return result;
    }
}
