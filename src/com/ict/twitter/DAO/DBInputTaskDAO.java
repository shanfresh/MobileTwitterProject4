package com.ict.twitter.DAO;

import java.sql.SQLException;
import java.sql.Statement;

import com.ict.twitter.tools.DbOperation;
import com.mysql.jdbc.Connection;

public class DBInputTaskDAO {
	DbOperation dbo;
	public static String TableName=" inputtask ";
	public DBInputTaskDAO(){
		dbo=new DbOperation();	
	}
	public void InitTable() throws SQLException{
		java.sql.Connection con=dbo.GetConnection();
		Statement sta=con.createStatement();
		sta.executeUpdate("UPDATE "+TableName+"SET status='Created' where id <20");
		con.close();
	}
}
