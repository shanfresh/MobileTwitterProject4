package com.ict.twitter;

import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;
import com.ict.twitter.tools.SaveTxtFile;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

public class AjaxRTCrawlMain {
	DbOperation dbo;
	Connection con;
	public AjaxRTCrawlMain(){
		
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
	public Vector<String> ListMessageID(){
		Connection connection=this.GetDBCon();
		Vector<String> result=new Vector<String>();
		try {
			PreparedStatement pst=connection.prepareStatement("select message_id from message");
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
	
	public void close(){
		try {
			if(con!=null&&false==con.isClosed())
				con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		TwitterClientManager cm=new TwitterClientManager();
		MulityInsertDataBase dbo=new MulityInsertDataBase();
		
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.67", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		AjaxRTCrawlMain arcm=new AjaxRTCrawlMain();
		AjaxRTCrawl ac=new AjaxRTCrawl(httpclient,null);
		
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename=sdf.format(date);
		SaveTxtFile sxf=new SaveTxtFile(""+filename, false);
		
		System.out.println("出错后ID 列表："+filename);
		
		Vector<String> messageidlist=arcm.ListMessageID();
		System.out.println(messageidlist.size());
		arcm.close();
		for(int i=0;i<messageidlist.size();i++){
			System.out.println("要采集的目标推文ID"+messageidlist.get(i));
			Task task=new Task(TaskType.MessageRel,messageidlist.get(i));
			boolean flag=ac.doCrawl(task, dbo, null,null);
			if(false==flag){
				sxf.Append(messageidlist.get(i)+"\r\n");
			}
		}
		sxf.close();
		
		
	}
	
	private static void SaveToTextFile(String Content){
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename=sdf.format(date);
		SaveTxtFile sxf=new SaveTxtFile(""+filename, false);
		System.out.println("保存的文件名是："+filename);
		sxf.Append(Content);
		sxf.close();
	}
	
}
