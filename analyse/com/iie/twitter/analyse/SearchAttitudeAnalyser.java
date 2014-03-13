package com.iie.twitter.analyse;

import java.io.File;
import java.util.Vector;
import java.sql.*;

import com.ict.twitter.tools.ReadTxtFile;

public class SearchAttitudeAnalyser extends BaseAnalyse {
	Vector<String> trueList;
	Vector<String> falsList;
	public SearchAttitudeAnalyser(String filename){
		trueList=load("AnalyseSource/支持关键词.txt");
		System.out.println("支持的值大小"+trueList.size());
		falsList=load("AnalyseSource/反对关键词.txt");
		System.out.println("不支持的值大小"+falsList.size());
	}
	public static void main(String[] args){
		SearchAttitudeAnalyser sa=new SearchAttitudeAnalyser("");
		sa.doAna();
	}
	public void doAna(){
		Connection con=super.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("SELECT group_concat(title),user_id FROM http_twitter140305.message_search_wenyunchao group by user_id;");
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				String content=rs.getString(1);
				String user=rs.getString(2);
				if(IsExist(content)){
					doUpdate("反对",user,content);
				}else{
					doUpdate("支持",user,content);
				}
				
			}
			pst.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	private void doUpdate(String attitude,String userid,String content){
		Connection con=super.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("UPDATE result set attitude_to_wen=?,tweet_total=? where user_name=?");
			pst.setString(1, attitude);
			pst.setString(2, content);
			pst.setString(3, userid);
			pst.executeUpdate();
			pst.close();
			
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	
	private boolean IsExist(String content){
		for(int i=0;i<falsList.size();i++){
			if(content.contains(falsList.get(i))){
				return true;
			}			
		}
		return false;
		
	}
	
	
	
	private Vector<String> load(String fileName){
		
		ReadTxtFile rxf=new ReadTxtFile(fileName);
		Vector<String> result=rxf.read();
		
		return result;
	}
}
