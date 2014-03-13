package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.ict.twitter.tools.DbOperation;

public class BaseAnalyse {

	private DbOperation dbo;
	Connection con;
	Vector<String> userid;
	public Connection GetDBCon(){
		try {
			if(con==null||con.isClosed()){
				dbo=new DbOperation();
				con=dbo.GetConnection();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			dbo=new DbOperation();
			con=dbo.GetConnection();
		}
		return con;

	}
	
	void GetUserList(){
		userid=new Vector<String>(200);
		try{
			Connection con=this.GetDBCon();
			PreparedStatement pst=con.prepareStatement("SELECT user_name from result");
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				userid.add(rs.getString(1));
			}
			System.out.println("总的用户数"+userid.size());
			pst.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
}
