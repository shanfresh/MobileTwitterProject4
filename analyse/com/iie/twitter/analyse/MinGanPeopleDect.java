package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;
import java.util.regex.Pattern;

import com.ict.twitter.tools.ReadTxtFile;

public class MinGanPeopleDect  extends BaseAnalyse implements Runnable{
	public int start;
	public int eachSize;
	public String StartTime;
	public Vector<String> minganci;
	public MinGanPeopleDect(){
		ReadTxtFile rxf=new ReadTxtFile("UsefulFile/mingan_0316.txt");
		minganci=rxf.read();		
	}
	
	public void doAnalyse() {
		Connection con=this.GetDBCon();
		PreparedStatement pst;
		try {
			pst = con.prepareStatement("Select user_name from user_profile where is_chinese=1 order by id asc limit "+start+","+eachSize);
			ResultSet rs=pst.executeQuery();
			int count=0;
			int mingancount=0;
			while(rs.next()){
				String UserName=rs.getString(1);
				String content=this.getString(UserName, StartTime);
				if(IsMinGan(content)){
					
					mingancount++;
					System.out.println("当前敏感用户："+UserName);
					System.out.println("---------------------------------");
				}
			}
			System.out.println("敏感用户数目"+mingancount);
			pst.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	private void setResultToSql(String UserName,boolean flag){
//		Connection con=this.GetDBCon();
//		try{
//			PreparedStatement pst=con.prepareStatement("UPDATE user_profile SET is_chinese_2=? where user_name=?");
//			pst.setBoolean(1, flag);
//			pst.setString(2, UserName);
//			int count=pst.executeUpdate();
//			
//			pst.close();
//		}catch(SQLException ex){
//			ex.printStackTrace();
//		}
	}
	public String getString(String user_name,String fromTime){
		Connection con=this.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("SELECT group_concat(title) FROM message_keyuser_0316 where create_time>=? AND  user_id=? ");
			pst.setString(2, user_name);
			pst.setString(1, fromTime);
			ResultSet rs=pst.executeQuery();
			String result=null;
			if(rs.next()){
				result = rs.getString(1);
			}else{
				result ="";
			}
			pst.close();
			return result;
		}catch(SQLException ex){
			ex.printStackTrace();
			return null;
		}
	}
	private boolean IsMinGan(String content){
		if(content==null){
			return false;
		}
		boolean flag=false;
		String mingan="";
		for(int i=0;i<minganci.size();i++){
			if(content.contains(minganci.get(i))){
				mingan+=minganci.get(i)+"  ";
				flag=true;
				
			}
		}
		if(flag){
			System.out.println("Conent"+content);
			System.out.println("KeyWords:"+mingan);
			
			return true;
		}else{
			return false;
		}
		
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args){
		MinGanPeopleDect mgt=new MinGanPeopleDect();
		mgt.start=0;
		mgt.eachSize=1000;
		mgt.StartTime="2013-12-16 00:00:00";
		mgt.doAnalyse();
	}

}
