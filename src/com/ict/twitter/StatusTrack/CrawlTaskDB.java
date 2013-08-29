package com.ict.twitter.StatusTrack;

import com.ict.twitter.tools.DbOperation;
import java.sql.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CrawlTaskDB {
	DbOperation dbOp;
	Connection con;
	PreparedStatement pst,pstUpdateStatus;
	public CrawlTaskDB(){
		dbOp=new DbOperation();
		con=dbOp.conDB();
		Init();
	}
	private void Init(){
		try {
			pst=con.prepareStatement("INSERT INTO `crawlstatus` (`taskStr`,`taskType`,`CreateTime`,`FinTime`,`Status`) VALUES(?,?,?,?,?)");
			pstUpdateStatus=con.prepareStatement("update `crawlstatus` SET `FinTime`=?,`Status`=? WHERE taskStr=? AND taskType=?");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public boolean AddTask(String taskStr,CrawlTaskType type){
		Date date=new Date(System.currentTimeMillis());
		try{
			pst.setString(1, taskStr);
			pst.setInt(2, type.ordinal()+1);
			pst.setDate(3,date);
			pst.setDate(4, null);
			pst.setString(5, "Created");
			pst.executeUpdate();
		}catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ex){
			System.err.println("÷ÿ∏¥≤Â»Î"+ex.getLocalizedMessage());
			return false;
		}
		catch(Exception ex){
			return false;
		}
		return true;
		
	}
	public boolean FinishTask(String task,CrawlTaskType type,boolean isOK){
		if(isOK)
			return this.SetTaskStatus(task, type, "Success");
		else
			return this.SetTaskStatus(task, type, "Fail");
		
	}
	
	private boolean SetTaskStatus(String task,CrawlTaskType type,String status){
		Timestamp date=new Timestamp(System.currentTimeMillis());
		try{
			pstUpdateStatus.setTimestamp(1, date);
			pstUpdateStatus.setString(2, status);
			pstUpdateStatus.setString(3,task);
			pstUpdateStatus.setInt(4, type.ordinal()+1);
			pstUpdateStatus.executeUpdate();
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
	}
	CrawlTaskDB db;
	@Before
	public void before(){
		db=new CrawlTaskDB();
	}
	
	@Test
	public void testUpdate(){
		db.SetTaskStatus("shanjixi", CrawlTaskType.Search,"Fail");
	}
	
	@Test
	@Ignore
	public void test(){
		db.AddTask("shanjixi", CrawlTaskType.Search);
	}

}
