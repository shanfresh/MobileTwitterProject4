package com.ict.twitter.plantform;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.ict.twitter.tools.DbOperation;

public class DatabaseCounter {

	/**
	 * @param args
	 */
	Connection con;
	public DatabaseCounter(){
		con=(new DbOperation()).conDB();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DatabaseCounter dc=new DatabaseCounter();
		long first=System.currentTimeMillis();
		dc.countTwitterWEB();
		long last=System.currentTimeMillis();
		System.out.println((last-first)/1000);

	}

	public int[] countTwitterWEB(){
		int[] result=new int[4];
		
		try {
			result[0]=selectCountFromTable("message");
			result[1]=selectCountFromTable("message_relationship");
			result[2]=selectCountFromTable("user");
			result[3]=selectCountFromTable("user_relationship");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
		
	}
	public int selectCountFromTable(String tableName) throws SQLException{
		Statement sta=con.createStatement();
		ResultSet rs=sta.executeQuery("select count(id) from "+tableName);
		rs.next();
		int count=rs.getInt(1);
		System.out.println("查询大小是???"+count);
		return count;
		
	
	};
}
