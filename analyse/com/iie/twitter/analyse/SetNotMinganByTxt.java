package com.iie.twitter.analyse;
import java.sql.*;
import java.util.*;

import com.ict.twitter.tools.ReadTxtFile;
public class SetNotMinganByTxt extends BaseAnalyse {

	public void doAnalyse() throws Exception{
		Connection con=this.GetDBCon();
		ReadTxtFile rxf=new ReadTxtFile("AnalyseSource\\Name.txt");
		Vector<String> allname=rxf.read();
		PreparedStatement pst=con.prepareStatement("update user_profile SET is_chinese=false where user_name=?");
		int flu=0;
		for(String t:allname){
			System.out.println(t);
			pst.setString(1, t);
			flu+=pst.executeUpdate();
		}
		con.close();
		System.out.println(flu);
		
	}
	
	public static void main(String[] args) {
		SetNotMinganByTxt sn=new SetNotMinganByTxt();
		try {
			sn.doAnalyse();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
