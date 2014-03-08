package com.ict.twitter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;
import com.ict.twitter.tools.SaveTxtFile;

public class AjaxProfileCrawlMain {
	
	DbOperation dbo;
	Connection con;
	public AjaxProfileCrawlMain(){
		
	}
	
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
	
	public Vector<String> FindUserID(){
		Connection connection=this.GetDBCon();
		Vector<String> result=new Vector<String>();
		try {
			PreparedStatement pst=connection.prepareStatement("SELECT user_name FROM http_twitter140305.result WHERE follower_count IS  NULL");
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
	
	
	

	public static void main(String[] args){
		TwitterClientManager cm=new TwitterClientManager();
		MulityInsertDataBase dbo=new MulityInsertDataBase();
		
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.67", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename=sdf.format(date);
		SaveTxtFile sxf=new SaveTxtFile(filename, false);
		System.out.println("出错后ID 列表："+filename);	
		
		AjaxProfileCrawl apc=new AjaxProfileCrawl(httpclient, null);
		AjaxProfileCrawlMain father=new AjaxProfileCrawlMain();
		
		Vector<String> keyuserlist=father.FindUserID();
		
		for(int i=0;i<keyuserlist.size();i++){
			System.out.println("当前正在采集的用户："+keyuserlist.get(i));
			Task task=new Task(TaskType.About,keyuserlist.get(i));
			boolean flag=apc.doCrawl(task, dbo, null, null);			
			if(false==flag){
				sxf.Append(task.getTargetString());				
			}
		}
		
		
		
	}
}
