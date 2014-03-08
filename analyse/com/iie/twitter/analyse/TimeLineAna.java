package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.SaveTxtFile;

public class TimeLineAna {
	Vector<String> allUser;
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
	public void listAlluser(){
		
		
	}
	
	public Vector<String> FindUserID(){
		Connection connection=this.GetDBCon();
		Vector<String> result=new Vector<String>();
		try {
			PreparedStatement pst=connection.prepareStatement("SELECT UserName FROM keyuser where Weight>3");
			ResultSet rst=pst.executeQuery();
			while(rst.next()){
				result.add(rst.getString(1));
			}
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	
	public boolean saveToProfile(String userid,int count){
		Connection connection=this.GetDBCon();
		Vector<String> result=new Vector<String>();
		try {
			PreparedStatement pst=connection.prepareStatement("update user_profile set invalid_time_count=? where user_name=?");
			pst.setInt(1, count);
			pst.setString(2, userid);
			int effect=pst.executeUpdate();
			pst.close();
			if(effect<=0){
				return false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean saveToProfile_fq_count(String userid,int count){
		Connection connection=this.GetDBCon();
		try {
			PreparedStatement pst=connection.prepareStatement("update user_profile set fq_count=? where user_name=?");
			pst.setInt(1, count);
			pst.setString(2, userid);
			int effect=pst.executeUpdate();
			pst.close();
			if(effect<=0){
				return false;
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public Vector<String> FindDateByUserName(String username){
		System.out.println("Curren UserName"+username);
		Vector<String> result=new Vector<String>();
		Connection con = this.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("Select create_time from message where user_id=?");
			pst.setString(1, username);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				result.add(rs.getString(1));
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;		
		
	}
	public Vector<String> FindMessageByUserName(String username){
		System.out.println("Curren UserName"+username);
		Vector<String> result=new Vector<String>();
		Connection con = this.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("Select title from message where user_id=?");
			pst.setString(1, username);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				result.add(rs.getString(1));
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
		return result;		
		
	}
	
	
	public static void main_FQ(String[] args){
		TimeLineAna timeana=new TimeLineAna();
		Vector<String> alluser=timeana.FindUserID();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat saveFileDate=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		
		Date date=new Date();
		String filename=saveFileDate.format(date);
		SaveTxtFile sxf=new SaveTxtFile(filename, false);
		
		System.out.println("出错后ID 列表："+filename);
		
		for(String t:alluser){
			Vector<String> allmessage=timeana.FindMessageByUserName(t);
			int invalidCount=0;
			for(int i=0;i<allmessage.size();i++){
				try {
					String currentMsg=allmessage.get(i);
					if (	 currentMsg.contains("翻墙")
							|| currentMsg.contains("lantern")
							|| currentMsg.contains("灯笼")
							|| currentMsg.contains("vpngate")
							|| currentMsg.contains("goagent")
							|| currentMsg.contains("自由门")
							|| currentMsg.contains("翻墙")
							|| currentMsg.contains("无界")
							|| currentMsg.contains("ssl")
							|| currentMsg.contains("ssh")
							|| currentMsg.contains("vpn")
							|| currentMsg.contains("代理")
							|| currentMsg.contains("shadowsocks")
							|| currentMsg.contains("gfw")
							|| currentMsg.contains("防火墙")
							|| currentMsg.contains("审查")
							|| currentMsg.contains("网络审查")
							|| currentMsg.contains("网络攻击")
							|| currentMsg.contains("隧道")
						) 
					{
						invalidCount++;	

					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			System.out.println("当前用户"+t+"含有关键词次数"+invalidCount);
			timeana.saveToProfile_fq_count(t,invalidCount);
		}
		
	}
	
	public static void main(String[] args){
		TimeLineAna timeana=new TimeLineAna();
		Vector<String> alluser=timeana.FindUserID();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		SimpleDateFormat saveFileDate=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		
		Date date=new Date();
		String filename=saveFileDate.format(date);
		SaveTxtFile sxf=new SaveTxtFile(filename, false);
		
		System.out.println("出错后ID 列表："+filename);
		
		for(String t:alluser){
			Vector<String> allmessage=timeana.FindMessageByUserName(t);
			int invalidCount=0;
			for(int i=0;i<allmessage.size();i++){
				try {
					String currentMsg=allmessage.get(i);
					if(true) 
					{
						invalidCount++;	

					}
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
			System.out.println("当前用户"+t+"含有关键词次数"+invalidCount);
			timeana.saveToProfile_fq_count(t,invalidCount);
		}
		
	}
	
	
}
