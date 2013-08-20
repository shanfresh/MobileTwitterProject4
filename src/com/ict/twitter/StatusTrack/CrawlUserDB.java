package com.ict.twitter.StatusTrack;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;

import org.junit.Before;
import org.junit.Test;

import com.ict.twitter.tools.AllHasInsertedException;
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
			int[] result=pst.executeBatch();
			try {
				checkBatch(result);
			} catch (AllHasInsertedException e) {//当前插入失败
				// TODO Auto-generated catch block
				e.printStackTrace();
				return -1;
			}
			return 1;
		}catch(SQLException ex){
			//ex.printStackTrace();
			
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

	private void checkBatch(int[] updateCounts) throws AllHasInsertedException{
		int OKRows=0,NoInfoRows=0,FailRows=0;
		for(int i=0;i<updateCounts.length;i++){
			if (updateCounts[i] >= 0) {
				OKRows++;
		      } else if (updateCounts[i] == Statement.SUCCESS_NO_INFO) {
		        NoInfoRows++;
		      } else if (updateCounts[i] == Statement.EXECUTE_FAILED) {
		    	//System.out.println("["+i+"]Failed to execute; updateCount=Statement.EXECUTE_FAILED");
		        FailRows++;
		      }
		}
		//System.out.println(String.format("Success:%d NoInfo:%d Failed:%d",OKRows,NoInfoRows,FailRows));
		if(FailRows==updateCounts.length){
			throw new AllHasInsertedException("所有的数据都插入过了");
		}
	}
	@Test
	public void test(){
		java.sql.Timestamp crawltime=new java.sql.Timestamp(System.currentTimeMillis());
		insertUserItem("shanjixi",-1, true, crawltime, 1);
	}
}
