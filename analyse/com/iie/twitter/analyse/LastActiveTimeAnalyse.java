package com.iie.twitter.analyse;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Vector;
public class LastActiveTimeAnalyse extends BaseAnalyse {
	public void doAnalyse(){
		Connection con=this.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("select create_time from message_result_0309 where user_id=?");
			
			for(int i=0;i<super.userid.size();i++){
				String date=null;
				String user_name=userid.get(i);
				pst.setString(1 , user_name);
				ResultSet rs=pst.executeQuery();
				while(rs.next()){
					if(date==null){
						date=rs.getString(1);
					}else{
						if(date.compareTo(rs.getString(1))<0){
							date=rs.getString(1);
						}
					}
				}
				System.out.println(i+"UserName:"+user_name+"Last_Login_Time="+date);
				update(user_name,date);
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void update(String userid,String result){
		Connection con=this.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("update result set last_active_time=? where user_name=?");
			pst.setString(1, result);
			pst.setString(2, userid);
			pst.executeUpdate();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
		
		
	}
	
	public static void main(String[] args){
		LastActiveTimeAnalyse lat=new LastActiveTimeAnalyse();
		lat.GetUserList();
		lat.doAnalyse();

		
	}
	
	
}
