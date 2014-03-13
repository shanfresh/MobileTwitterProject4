package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

import com.ict.twitter.tools.ReadTxtFile;

public class SensiteiveAnalyser extends BaseAnalyse implements Runnable {
	Vector<String> sensitive;
	Vector<String> youxing;
	int start;
	int end;
	public SensiteiveAnalyser(int start,int end){
		this.start=start;
		this.end=end;
		super.GetUserList();
		this.doLoad();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SensiteiveAnalyser sa1=new SensiteiveAnalyser(0,4883);
		
		sa1.doAnalyse();
		System.out.println("运行完毕");
		
	}
	private Vector<String> load(String fileName){
		
		ReadTxtFile rxf=new ReadTxtFile(fileName);
		Vector<String> result=rxf.read();
		
		return result;
	}
	public void doLoad(){
		sensitive=load("AnalyseSource/LingDaoRen.txt");
		youxing=load("AnalyseSource/youxing.txt");
	}
	public Vector<String> FindTweetByUserID(String userid){
		Vector<String> result=new Vector<String>(100);
		Connection con=super.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("Select title from message_result_0309 where user_id=? limit 1000");
			pst.setString(1, userid);
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				result.add(rs.getString(1));
			}
			pst.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return result;
	}
	
	private void UpdateAndSet(String user_id,int attitude){
		Connection con=super.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("UPDATE result set sensitive_level=? where user_name=?");
			pst.setInt(1, attitude);
			
			pst.setString(2, user_id);
			pst.executeUpdate();
			
			pst.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		
	}
	
	private  String LoadStringByID(String user){
		Connection con=super.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("select tweet_total from result where user_name=?");
			pst.setString(1,user);
			ResultSet rs=pst.executeQuery();
			String t="";
			if(rs.next()){
				t=rs.getString(1);
			}
			pst.close();
			return t;
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		return "";
	}
	
	public void doAnalyse(){
		Connection con=super.GetDBCon();
		int count=0;
		for(int i=0;i<userid.size();i++){
			String user=userid.get(i);
			
			String str=LoadStringByID(user);
			int result=0;
			
			
			for(int j=0;j<sensitive.size();j++){
				if(str.contains(sensitive.get(j))){
					result=3;
					break;
				}
			}
			if(result==0){
				for(int j=0;j<youxing.size();j++){
					if(str.contains(youxing.get(j))){
						result=2;
						break;
					}
				}
			}
			if(result==0){
				result=1;
			}
			UpdateAndSet(user,result);
			
			
			
			count++;
			System.out.println("当前节点运行到"+count);
		}
		
	}
	
	
	@Override
	public void run() {
		System.out.println("From"+start+"TO"+end);
		doAnalyse();
	}


}
