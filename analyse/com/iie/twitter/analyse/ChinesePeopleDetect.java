package com.iie.twitter.analyse;
import java.sql.*;
public class ChinesePeopleDetect extends BaseAnalyse implements Runnable{
	ChineseRecognizer  chineseRec=new ChineseRecognizer();
	public int start;
	public static final int  total=113732;
	public ChinesePeopleDetect(int start){
		this.start=start;
	}

	
	
	public void doAnalyse(){
		Connection con=this.GetDBCon();
		PreparedStatement pst;
		try {
			pst = con.prepareStatement("Select user_name from user_profile where id<113732 order by id asc limit "+start+","+ChinesePeopleDetect.total/100);
			ResultSet rs=pst.executeQuery();
			int count=0;
			while(rs.next()){
				String UserName=rs.getString(1);
				System.out.println("Current:"+count++ +" "+UserName+":");
				String content=getString(UserName);
				if(content==null){
					this.setResultToSql(UserName, false);
					continue;
				}
				if(chineseRec.isContainChinese(content)){
					if(chineseRec.isContainJapanese(content)){
						//System.err.println("ÈÕÎÄ×Ö·û´®£º"+content);
						this.setResultToSql(UserName, false);
					}else{
						System.err.println("ÖÐÎÄ×Ö·û´®£º"+content);
						this.setResultToSql(UserName, true);
					}
					
				}else{
					//System.out.println("Ó¢ÎÄ×Ö·û´®£º"+content);
					this.setResultToSql(UserName, false);
				}
				
			}
			pst.close();
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public String getString(String user_name){
		Connection con=this.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("SELECT group_concat(title) FROM profile_message where user_id=?");
			pst.setString(1, user_name);
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

	private void setResultToSql(String UserName,boolean flag){
		Connection con=this.GetDBCon();
		try{
			PreparedStatement pst=con.prepareStatement("UPDATE user_profile SET is_chinese_2=? where user_name=?");
			pst.setBoolean(1, flag);
			pst.setString(2, UserName);
			int count=pst.executeUpdate();
			
			pst.close();
		}catch(SQLException ex){
			ex.printStackTrace();
		}
	}
	public static void main(String[] args){
		Thread[] allThread=new Thread[100];
		for(int i=0;i<100;i++){
			int start=i*(ChinesePeopleDetect.total/100);
			ChinesePeopleDetect dect=new ChinesePeopleDetect(start);
			allThread[i]=new Thread(dect);			
		}
		for(int i=0;i<100;i++){
			allThread[i].start();
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		doAnalyse();
	}
	
	

}
