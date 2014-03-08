package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.Vector;

import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.ReadTxtFile;

public class ResultOperation {

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
	public void Update(String filename){
		ReadTxtFile rxf=new ReadTxtFile(filename);
		Vector<String> zhichifandui=rxf.read();
		int count=0;
		for(String t:zhichifandui){
			StringTokenizer st=new StringTokenizer(t, "\t");
			
			if(t.contains("支持")){
				String username=st.nextToken();
				String value="true";
				//UpdateZhichi(username,"attitude_to_wen",value);
				count++;
				if(!this.exist(username)){
					System.out.println(username);
				}
			}else if(t.contains("反对")){
				String username=st.nextToken();
				String value="false";
				//UpdateZhichi(username,"attitude_to_wen",value);
				count++;
				if(!this.exist(username)){
					System.out.println(username);
				}
			}
			
		}
		System.out.println("总标注数目"+count);
		
	}
	public boolean exist(String user_id){
		Connection con=this.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("SELECT * from result where user_name=?");
			pst.setString(1, user_id);
			ResultSet result=pst.executeQuery();
			boolean flag=false;
			if(result.next()){
				flag=true;
			}
			pst.close();
			return flag;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
		
	}
	
	
	
	public boolean UpdateZhichi(String user_id,String FieldName,String value){
		Connection con=this.GetDBCon();
		try {
			PreparedStatement pst=con.prepareStatement("UPDATE result SET "+FieldName+"=? where user_name=?");
			pst.setString(1, value);
			pst.setString(2, user_id);
			int result=pst.executeUpdate();
			if(result<1){
				return false;
			}else
				return true;
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
		
	}
	
	
	public static void main(String[] args) {
		ResultOperation rop=new ResultOperation();
		rop.Update("AnalyseSource/ZhiChi_Fandui.txt");

	}

}
