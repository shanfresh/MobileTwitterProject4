package com.iie.twitter.analyse;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ict.twitter.tools.SaveTxtFile;

public class ReplyBackAnalyse extends BaseAnalyse{
	Vector<String> allContent=null;
	HashMap<String,Integer> total=new HashMap<String,Integer>();
	public void doAnalyse(){
		long time=System.currentTimeMillis();
		allContent=new Vector<String>(200);
		SaveTxtFile sxf=new SaveTxtFile("REPLY_BACK_FROM_"+time+".txt",false);
		Connection con=this.GetDBCon();		
		try {
			PreparedStatement pst=con.prepareStatement("select title from message_result_0309 where user_id='wenyunchao'");
			ResultSet rs=pst.executeQuery();
			while(rs.next()){
				Vector<String> rest=AnalyseString(rs.getString(1));
				for(int j=0;j<rest.size();j++){
					String t=rest.get(j);
					Integer currentCount=total.get(t);
					if(currentCount==null){
						currentCount=new Integer(1);
						total.put(t, currentCount);
					}else{
						currentCount++;
						total.put(t, currentCount);
					}
				}
				
			}
			sxf.flush();
			
			
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Update(){
		Set<String> keys=total.keySet();
		Iterator<String> it=keys.iterator();
		Connection con=this.GetDBCon();
		while(it.hasNext()){
		    try{
					PreparedStatement pst=con.prepareStatement("UPDATE result set reply_back_count=? where user_name=?");
					String username=it.next();
					Integer count=total.get(username);
					pst.setInt(1, count.intValue());
					pst.setString(2, username);
					pst.executeUpdate();
			}catch(SQLException ex){
				ex.printStackTrace();
			}
		}
		
	}
	
	
	public Vector<String> AnalyseString(String title){
		Pattern pattern=Pattern.compile("@([a-zA-Z0-9]+)");
		Matcher matcher=pattern.matcher(title);
		int count=matcher.groupCount();
		Vector<String> result=new Vector<String>();
		while(matcher.find()){
			
			result.add(matcher.group(1));
			
		}
		for(int i=0;i<result.size();i++){
			System.out.println(result.get(i));
		}
		return result;
	}
	
	public static void main(String[] args){
		ReplyBackAnalyse rb=new ReplyBackAnalyse();
		rb.GetUserList();
		rb.doAnalyse();
		rb.Update();
		
		
	}

}
