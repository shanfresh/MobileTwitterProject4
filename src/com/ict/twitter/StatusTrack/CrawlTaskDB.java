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
	}
	public int AddTask(String taskStr,CrawlTaskType type){
		Timestamp now=new Timestamp(System.currentTimeMillis());
		int result=-1;
		try{
			pst=con.prepareStatement("INSERT INTO `crawlstatus` (`taskStr`,`taskType`,`CreateTime`,`FinTime`,`Status`) VALUES(?,?,?,?,?)",Statement.RETURN_GENERATED_KEYS);
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
			System.err.println("采集状态已经插入，更新crawlstatus:"+ex.getLocalizedMessage());
			try {
				pstFind=con.prepareStatement("Select id from `crawlstatus` where `taskStr`=? AND `taskType`=?");
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
			}finally{
				try{
					pstFind.close();
				}catch(SQLException ex2){
					ex2.printStackTrace();
				}
			}
			return result;//出错后返回查找之后的的
			
		}catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}catch(Exception ex){
			return -1;
		}finally{
			try{
				pst.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		return result;
		
	}
	public boolean FinishTask(String task,CrawlTaskType type,boolean isOK,String ErrorMsg){
		if(isOK)
			return this.SetTaskStatus(task, type, "Success",ErrorMsg);
		else
			return this.SetTaskStatus(task, type, "Fail",ErrorMsg);
		
	}
	
	private boolean SetTaskStatus(String task,CrawlTaskType type,String status,String ErrorMsg){
		Timestamp date=new Timestamp(System.currentTimeMillis());
		try{
			pstUpdateStatus=con.prepareStatement("update `crawlstatus` SET `FinTime`=?,`Status`=?,`ErrorMsg`=? WHERE taskStr=? AND taskType=?");
			pstUpdateStatus.setTimestamp(1, date);
			pstUpdateStatus.setString(2, status);
			pstUpdateStatus.setString(3,ErrorMsg);
			pstUpdateStatus.setString(4, task);
			pstUpdateStatus.setInt(5, type.ordinal()+1);
			pstUpdateStatus.executeUpdate();
			
			return true;
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}finally{
			try{
				pstUpdateStatus.close();
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
		
	}
	CrawlTaskDB db;
	@Before
	public void before(){
		db=new CrawlTaskDB();
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
