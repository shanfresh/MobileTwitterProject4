package com.ict.twitter.DAO;

import com.ict.twitter.DAO.bean.KeyUser;
import com.ict.twitter.tools.DbOperation;

import java.util.*;
import java.sql.*;

import org.junit.Test;
public class DBKeyUserDAO {
	DbOperation dbo;
	public DBKeyUserDAO(){
		dbo=new DbOperation();
	}
	public Vector<KeyUser> GetKeyUser(){
		Vector<KeyUser> keyusers=new Vector<KeyUser>();
		Connection con=dbo.GetConnection();
		PreparedStatement pst=null;
		ResultSet rs=null;
		try {
			pst=con.prepareStatement("select * from keyuser");
			rs=pst.executeQuery();
			while(rs.next()){
				String id=rs.getString("User_Name");
				int CrawlCount=rs.getInt("CrawlCount");
				int Weight=rs.getInt("Weight");
				KeyUser keyuser=new KeyUser(id,CrawlCount,Weight);
				keyusers.add(keyuser);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{
				pst.close();
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
		try {
			PreparedStatement updateCrawlCount=con.prepareStatement("Update keyuser set CrawlCount=CrawlCount+1,CrawlTime=? Where User_Name=?");
			updateCrawlCount.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			for(int i=0;i<keyusers.size();i++){
				updateCrawlCount.setString(2, keyusers.get(i).UserID);
				updateCrawlCount.addBatch();
			}
			updateCrawlCount.executeBatch();
			updateCrawlCount.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		

		
		
		return keyusers;
	}
	//每次调用后执行本语句，刷新整个KeyUser表中Weight的大小
	public void  UpdateCount(){
		
	}
	
	public boolean CheckAndInsert(String username) throws SQLException{
		Connection con=dbo.GetConnection();
		PreparedStatement pst_select=con.prepareStatement("Select * from keyuser where User_Name=?");
		pst_select.setString(1, username);
		ResultSet rs=pst_select.executeQuery();
		boolean Flag_Exist=false;
		if(rs.next()){
			Flag_Exist=true;
		}else{
			Flag_Exist=false;
		}
		rs.close();
		pst_select.close();
		
		if(Flag_Exist){
			//Update
			PreparedStatement pst=con.prepareStatement("Update keyuser SET Weight=Weight+1 WHERE User_Name=?");
			pst.setString(1, username);
			int count=pst.executeUpdate();
			pst.close();
			return (count>0?true:false);
		}else{
			PreparedStatement pst=con.prepareStatement("Insert into keyuser(`User_Name`,`CrawlCount`,`Weight`) values(?,?,?)");
			pst.setString(1, username);
			pst.setInt(2, 0);
			pst.setInt(3, 0);
			int count=pst.executeUpdate();
			pst.close();
			return (count>0?true:false);
		}
		
		
	}
	
	
	@Test
	public void test(){
		DBKeyUserDAO duser=new DBKeyUserDAO();
		Vector rs=duser.GetKeyUser();
		System.out.println(rs.size());
		try {
			duser.CheckAndInsert("shanjixi");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
