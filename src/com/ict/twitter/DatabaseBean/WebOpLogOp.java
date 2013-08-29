package com.ict.twitter.DatabaseBean;
import java.sql.*;

import com.ict.twitter.task.beans.Task;
public class WebOpLogOp {
	Connection con;
	PreparedStatement pst=null;
	public WebOpLogOp(Connection con){
		this.con=con;

	}
	public boolean Insert(String TaskName,String MainType,String TaskType,String URL,String Result,int count){
		if(pst==null){
			try {
				pst=con.prepareStatement("Insert into weboplog(TaskName,MainType,TaskType,URL,Result,RequestCount) VALUES(?,?,?,?,?,?)");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			pst.setString(1, TaskName);
			pst.setString(2, MainType);
			pst.setString(3, TaskType);
			pst.setString(4, URL);
			pst.setString(5, Result);
			pst.setInt(6, count);
			int influence=pst.executeUpdate();
			if(influence>0){
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("WebOPLog操作数据库出错");
			System.out.println("TaskType is:["+TaskType+"]");
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
}
