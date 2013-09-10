package com.ict.twitter.StatusTrack;

import com.ict.twitter.tools.DbOperation;
import java.sql.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CrawlTaskDB {
	DbOperation dbOp;
	Connection con;
	PreparedStatement pst,pstUpdateStatus,pstFind;
	public CrawlTaskDB(){
		dbOp=new DbOperation();
		con=dbOp.conDB();
		Init();
	}
	private void Init(){
		try {
			if(pst==null||pst.isClosed()){
				pst=con.prepareStatement("INSERT INTO `crawlstatus` (`taskStr`,`taskType`,`CreateTime`,`FinTime`,`Status`) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
			}
			if(pstFind==null||pstFind.isClosed()){
				pstFind=con.prepareStatement("Select id from `crawlstatus` where `taskStr`=? AND `taskType`=?");
			}
			if(pstUpdateStatus==null||pstUpdateStatus.isClosed()){
				pstUpdateStatus=con.prepareStatement("update `crawlstatus` SET `FinTime`=?,`Status`=? WHERE taskStr=? AND taskType=?");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public int AddTask(String taskStr,CrawlTaskType type){
		this.Init();
		Timestamp now=new Timestamp(System.currentTimeMillis());
		int result=-1;
		try{
			pst.setString(1, taskStr);
			pst.setInt(2, type.ordinal()+1);
			pst.setTimestamp(3,now);
			pst.setDate(4, null);
			pst.setString(5, "Created");
			pst.executeUpdate();
			ResultSet rsFind=pst.getGeneratedKeys();
			if(rsFind.next()){
				result=rsFind.getInt(1);
			}
			pst.close();
		}catch(com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException ex){
			System.err.println("重复插入"+ex.getLocalizedMessage());
			try {
				pstFind.setString(1, taskStr);
				pstFind.setInt(2, type.ordinal()+1);
				ResultSet rs=pstFind.executeQuery();
				if(rs.next()){
					result=rs.getInt(1);
				}
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return result;//出错后返回查找之后的的
			
		}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}catch(Exception ex){
			return -1;
		}
		return result;
		
	}
	public boolean FinishTask(String task,CrawlTaskType type,boolean isOK){
		if(isOK)
			return this.SetTaskStatus(task, type, "Success");
		else
			return this.SetTaskStatus(task, type, "Fail");
		
	}
	
	private boolean SetTaskStatus(String task,CrawlTaskType type,String status){
		this.Init();
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
	@Ignore
	public void testUpdate(){
		db.SetTaskStatus("shanjixi", CrawlTaskType.Search,"Fail");
	}
	
	@After
	public void after(){
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void test(){
		int t=db.AddTask("shanjixi", CrawlTaskType.Search);
		System.out.println(t);
	}

}
