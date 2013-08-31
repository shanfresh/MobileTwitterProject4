package com.ict.twitter.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.DBFactory;

public class CrawlerServerReporter{
	
	
	private String ClinetID;
	private Connection con;
	public int message_count,message_rel_count,user_count,user_rel_count;
	PreparedStatement ps;
	public CrawlerServerReporter(String _ClientID,Connection _con){
		this.ClinetID=_ClientID;this.con=_con;
		initiallize();
	}
	public CrawlerServerReporter(String _cleintID){
		this.ClinetID=_cleintID;
		con=(new DBFactory()).getConnection();
		initiallize();
	}
	
	
	public void close(){
		try {
			if(ps!=null&&!ps.isClosed()){
				ps.close();
			}
			if(con!=null&&!con.isClosed()){
				con.close();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public CrawlerServerReporter(String clinetID, Connection con, int messageCount,int messageRelCount, int userCount, int userRelCount) {
		super();
		ClinetID = clinetID;
		this.con = con;
		message_count = messageCount;
		message_rel_count = messageRelCount;
		user_count = userCount;
		user_rel_count = userRelCount;
		initiallize();
		
	}
	public boolean initiallize(){
		try {
			if(con==null||con.isClosed()){
				con=(new DBFactory()).getConnection();
				if(con==null){
					System.out.println("数据连接错误已经");
					return false;
				}
			}
			if(ps==null){
				String psStr="INSERT INTO `statistics` "+
						"(`state`,"+"`message_count`,`message_increment`,"+
						"`user_count`,`user_increment`,`followship_count`,`followship_increment`"+
						") VALUES(?,?,?,?,?,?,?)";
				System.out.println(psStr);
				ps=con.prepareStatement(psStr);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//Report增量信息，每分钟汇报一次，或者每条消息汇报一次
	public boolean doReportIncrementByDataBase(ReportData rpdata) throws SQLException{
		initiallize();
		ps.setBoolean(1, true);//State表明是增量信息1：增量，2：全量
		ps.setInt(2, -1);
		ps.setInt(3, rpdata.message_increment);
		ps.setInt(4, -1);
		ps.setInt(5, rpdata.user_increment);
		ps.setInt(6, -1);//消息
		ps.setInt(7, rpdata.user_rel_increment);//用户关系的增长信息
		try{
			ps.executeUpdate();
		}catch(SQLException ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
		return true;		
	}
	//Report全量信息，每分钟汇报一次，或者每条消息汇报一次
	public boolean doReportIncrementTotal(int message,int user,int userRel) throws SQLException{
		initiallize();
		ps.setBoolean(1, false);//State表明是增量信息1：增量，0：全量
		ps.setInt(2,message );
		ps.setInt(3, 0);
		ps.setInt(4, user);
		ps.setInt(5, 0);
		ps.setInt(6, userRel);//消息
		ps.setInt(7, 0);//用户关系的增长信息
		try{
			ps.executeUpdate();
		}catch(SQLException ex){
			System.out.println(ex.getMessage());
			ex.printStackTrace();
			LogSys.crawlerServLogger.error("汇报全量数据失败"+message+","+user+","+userRel+"");
			LogSys.crawlerServLogger.error(ex.fillInStackTrace());
		}
		LogSys.crawlerServLogger.info("汇报全量数据成功"+message+","+user+","+userRel+"");
		return true;
	}
	public static void main(String[] args){
		Connection con=(new DBFactory()).getConnection();
		String id="FacebookWEB";
		int message=0;
		int message_rel=1;
		int user=2;
		int user_rel=33;
		CrawlerServerReporter cr=new CrawlerServerReporter(id, con,message,message_rel,user,user_rel);
		for(int i=0;i<2;i++){
			ReportData rpdata=new ReportData(1,2,1,3,"NULL");
			try {
				cr.doReportIncrementByDataBase(rpdata);
				System.out.println("汇报成功");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("汇报失败");
				e.printStackTrace();
			}
		}
	}
	

	
	

}
