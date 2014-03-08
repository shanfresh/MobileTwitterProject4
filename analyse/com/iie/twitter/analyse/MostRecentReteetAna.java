package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.SaveTxtFile;

public class MostRecentReteetAna {
	
	/*
	 * select * from 
	(select user_id as ret_id,count(*) as count_ret from message_reteet group by message_reteet.user_id order by count_ret desc) as tm2,
	(SELECT message_reteet.user_id as m1,message_reteet.content,message_reteet.user_name,create_time FROM http_twitter140305.message,http_twitter140305.message_reteet where message.message_id = message_reteet.message_id ) as tm
	where tm.m1=tm2.ret_id order by count_ret desc,ret_id desc 
	 */
	DbOperation dbo;
	Connection con;
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

	public void listAll() throws SQLException{
		String SQL="select * from (select user_id as ret_id,count(*) as count_ret from message_reteet group by message_reteet.user_id order by count_ret desc) as tm2,(SELECT message_reteet.user_id as m1,message_reteet.user_aliasname,message_reteet.content,message_reteet.user_name,create_time FROM  http_twitter140305.message,http_twitter140305.message_reteet where message.message_id = message_reteet.message_id ) as tm where tm.m1=tm2.ret_id order by count_ret desc,ret_id desc";
		SaveTxtFile sxf=new SaveTxtFile("wenyunchao_recent_following",false);
		Connection con=this.GetDBCon();
		PreparedStatement pst=con.prepareStatement(SQL);
		ResultSet rs=pst.executeQuery();
		String lastmessageid="-1";
		while(rs.next()){
			if(false==rs.getString("ret_id").equalsIgnoreCase(lastmessageid)){
				lastmessageid=rs.getString("ret_id");
				System.out.println();
				sxf.Append("\r\n");
				System.out.print(rs.getString("ret_id")+"\t");
				System.out.print(rs.getString("count_ret")+"\t");
				System.out.print(rs.getString("user_aliasname")+"\t");
				System.out.print(rs.getString("user_name")+"\t");
				System.out.print(rs.getString("content")+"\t");
				System.out.print(rs.getString("create_time")+"\t");	
				sxf.Append(rs.getString("ret_id")+"\t");
				sxf.Append(rs.getString("count_ret")+"\t");
				sxf.Append(rs.getString("user_aliasname")+"\t");
				sxf.Append(rs.getString("user_name")+"\t");
				
			}
			System.out.print(rs.getString("content")+"||");
			sxf.Append(rs.getString("content")+"||");
			
			
		}
		sxf.close();
		rs.close();
		pst.close();
		
	}
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MostRecentReteetAna mra=new MostRecentReteetAna();
		try {
			mra.listAll();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
