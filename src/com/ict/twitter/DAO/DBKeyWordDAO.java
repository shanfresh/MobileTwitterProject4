package com.ict.twitter.DAO;

import com.ict.twitter.tools.DbOperation;

import java.sql.*;
import java.util.*;

import com.ict.twitter.DAO.bean.*;
public class DBKeyWordDAO {
	DbOperation dbo;
	public DBKeyWordDAO(){
		dbo=new DbOperation();
	}
	public Vector<KeyWord> getKeyWords(){
		Vector<KeyWord> res=new Vector<KeyWord>();
		Connection con=dbo.GetConnection();
		Statement sta=null;
		try {
			sta=con.createStatement();
			ResultSet rs=sta.executeQuery("select * from keyword");
			while(rs.next()){
				KeyWord keyword=new KeyWord(rs.getString("WordStr"));
				res.add(keyword);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			try{
				sta.close();
				con.close();
			}catch(Exception ex){
				ex.printStackTrace();
			}
		}
		
		return res;
	}

}
