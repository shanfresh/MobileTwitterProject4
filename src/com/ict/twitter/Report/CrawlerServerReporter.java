package com.ict.twitter.Report;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
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
				ps=con.prepareStatement(
						"insert into node_count(node_name,message_increment,message_rel_increment,user_increment,user_rel_increment," +
						"message_count,message_rel_count,user_count,user_rel_count,db_time,total_time) values(?,?,?,?,?,?,?,?,?,?,?)");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public boolean doReportByDataBase(ReportData rpdata) throws SQLException{
		initiallize();
		ps.setString(1, ClinetID);
		ps.setInt(2, rpdata.message_increment);
		ps.setInt(3, rpdata.message_rel_increment);
		ps.setInt(4, rpdata.user_increment);
		ps.setInt(5, rpdata.user_rel_increment);
		message_count+=rpdata.message_increment;
		message_rel_count+=rpdata.message_rel_increment;
		user_count+=rpdata.user_increment;
		user_rel_count+=rpdata.user_rel_increment;
		
		ps.setInt(6, message_count);
		ps.setInt(7, message_rel_count);
		ps.setInt(8, user_count);
		ps.setInt(9, user_rel_count);
		Timestamp current=new Timestamp(System.currentTimeMillis());
		ps.setTimestamp(10, current);
		ps.setInt(11, 0);
		try{
			ps.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		System.out.println("当前总量信息M,ML,U,UL: "+message_count+","+message_rel_count+","+user_count+","+user_rel_count+",");
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
		for(int i=0;i<100;i++){
			ReportData rpdata=new ReportData(1,2,1,3,"NULL");
			try {
				cr.doReportByDataBase(rpdata);
				System.out.println("汇报成功");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println("汇报失败");
				e.printStackTrace();
			}
		}
	}
	

	
	

}
