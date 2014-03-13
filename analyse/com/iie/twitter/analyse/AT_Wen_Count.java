package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.ict.twitter.tools.SaveTxtFile;

public class AT_Wen_Count extends BaseAnalyse{

	public static void main(String[] args) {
		AT_Wen_Count atana=new AT_Wen_Count();
		atana.GetUserList();
		atana.doAnalyse();

	}

	public void doAnalyse(){
		long time=System.currentTimeMillis();
		SaveTxtFile sxf=new SaveTxtFile("At_to_wen_"+time+".txt",false);
		Connection con=this.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("select title from message_result_0309 where user_id=?");
			
			for(int i=0;i<super.userid.size();i++){
				String date=null;
				String user_name=userid.get(i);
				pst.setString(1, user_name);
				ResultSet rs=pst.executeQuery();
				int count=0;
				while(rs.next()){
					String content=rs.getString(1);
					if(content.contains("@wenyunchao")){
						count++;
					}
				}
				System.out.println(i+"UserName:"+user_name+"\t"+"At to Wen"+count);
				sxf.Append(user_name+"\t"+count);
				sxf.flush();
				update(user_name,count);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void update(String userid,int result){
		Connection con=this.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("update result set at_to_count=? where user_name=?");
			pst.setInt(1, result);
			pst.setString(2, userid);
			pst.executeUpdate();
			pst.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		
	}
}
