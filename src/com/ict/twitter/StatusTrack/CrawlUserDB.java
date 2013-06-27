package com.ict.twitter.StatusTrack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;

import com.ict.twitter.tools.DbOperation;
public class CrawlUserDB {
	DbOperation dbOp;
	Connection con;
	PreparedStatement pst;
	public CrawlUserDB(){
		dbOp=new DbOperation();
		con=dbOp.conDB();
		Init();
	}
	private void Init(){
		try {
			pst=con.prepareStatement("INSERT INTO `crawluser`(`username`,`userid`,`isKeyUser`,`createTime`,`deepth`,`status`)values(?,?,?,?,?,?)");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public int insertUser(CrawlUser user[]){
		try{
			pst.clearBatch();
			for(int i=0;i<user.length;i++){
				CrawlUser it=user[i];
				pst.setString(1, it.username);
				pst.setLong(2, it.userid);
				pst.setBoolean(3, it.isKeyUser);
				
				pst.setTimestamp(4, it.createTime);
				pst.setInt(5, it.deepth);
				pst.setString(6, "created");
				pst.addBatch();
			}
			return pst.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
			return -1;
		}			
	}
	public int insertUserItem(String username,int userid,boolean iskeyuser,Timestamp createTime,int deepth){
		CrawlUser user=new CrawlUser();
		user.username=username;
		user.userid=userid;
		user.isKeyUser=iskeyuser;
		user.createTime=createTime;
		user.deepth=deepth;
		CrawlUser[] users=new CrawlUser[1];
		users[0]=user;
		return insertUser(users);
	}


	@Test
	public void test(){
		java.sql.Timestamp crawltime=new java.sql.Timestamp(System.currentTimeMillis());
		insertUserItem("shanjixi",-1, true, crawltime, 1);
	}
}
