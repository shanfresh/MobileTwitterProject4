package com.ict.twitter.DatabaseBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.ict.twitter.CrawlerServer.InputType;
import com.ict.twitter.tools.DbOperation;

public class DBBeanInputTask {
	DbOperation dbOp;
	Connection con;
	java.sql.PreparedStatement pst;
	java.sql.PreparedStatement newInput;
	java.sql.PreparedStatement statusModify;//用于修改inputTask的Status状态
	public DBBeanInputTask(){
		dbOp=new DbOperation();
		con=dbOp.conDB();
		Init();
	}
	public DBBeanInputTask(DbOperation inputDbOp){
		dbOp=inputDbOp;
		con=dbOp.conDB();
		Init();
	}
	
	public boolean Init(){
		String GetNewTaskByType="SELECT ID,TaskName,TaskParameter,TaskWeight from inputtask where Status='Created' AND InputType=?";
		try {
			newInput=con.prepareStatement("Select count(*) from inputtask where Status='Created'");
			pst=con.prepareStatement(GetNewTaskByType);
			statusModify=con.prepareStatement("UPDATE inputtask SET Status=? where Id=?");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
	}
	public boolean CheckHasNewInput(){
		ResultSet rs;
		try {
			rs = newInput.executeQuery();
			if(rs.next()){
				int result=rs.getInt(1);
				return result>0;
			}else{
				return false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
	public Vector<InputTaskBean> GetTask(InputType inputType){
		Vector<InputTaskBean> vector;
		try {
			pst.setString(1, inputType.toString());
			ResultSet rs=pst.executeQuery();
			vector=new Vector<InputTaskBean>();
			while(rs.next()){
				InputTaskBean bean=new InputTaskBean();
				bean.ID=rs.getInt(1);
				bean.TaskName=rs.getString(2);
				bean.TaskParameter=rs.getString(3);
				bean.TaskWeight=rs.getInt(4);
				vector.add(bean);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			vector=null;
		}
		return vector;
	}
	public Vector<InputTaskBean> GetAllTask(){
		Vector<InputTaskBean> vector;
		try {
			PreparedStatement PstAll=con.prepareStatement("Select ID,TaskName,TaskParameter,TaskParameter2,InputType,TaskWeight from inputtask where Status='Created'");
			ResultSet rs=PstAll.executeQuery();
			vector=new Vector<InputTaskBean>();
			while(rs.next()){
				InputTaskBean bean=new InputTaskBean();
				bean.ID=rs.getInt(1);
				bean.TaskName=rs.getString(2);
				bean.TaskParameter=rs.getString(3);
				bean.TaskParameter2=rs.getString(4);
				bean.InputType=InputType.valueOf(rs.getString(5));
				bean.TaskWeight=rs.getInt(6);
				vector.add(bean);				
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			vector=null;
		}
		return vector;
	}
	public boolean ModifyStatus(String status,int ID){
		try{
			statusModify.setString(1, status);
			statusModify.setInt(2, ID);
			int ret=statusModify.executeUpdate();
			if(ret==1){
				return true;
			}
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return false;
		
	}
	
	public static void main(String[] args){
		DBBeanInputTask dbinput=new DBBeanInputTask();
		Vector<InputTaskBean> vector=dbinput.GetTask(InputType.Topic);
		System.out.println(vector.get(0).ID);
	}
}
