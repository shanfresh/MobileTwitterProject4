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
				String id=rs.getString("UserID");
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
				con.close();
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
		return keyusers;
	}
	
	@Test
	public void test(){
		DBKeyUserDAO duser=new DBKeyUserDAO();
		Vector rs=duser.GetKeyUser();
		System.out.println(rs.size());
	}
}
