package com.ict.facebook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import org.apache.http.params.CoreConnectionPNames;
import com.ict.facebook.FriendList.*;
import com.ict.facebook.TimeLine.*;


import com.ict.twitter.AjaxMainSearchFrameWork;
import com.ict.twitter.TwitterClientManager;
import com.ict.twitter.CrawlerNode.AjaxNode;
import com.ict.twitter.Report.ReportDataType;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.DbOperation;

public class FBAjaxMainSearch extends AjaxMainSearchFrameWork {
	public static void main(String[] args){
		FBAjaxMainSearch fbsearch1 = new FBAjaxMainSearch("test1",null);
		FBAjaxMainSearch fbsearch2 = new FBAjaxMainSearch("test2",null);
		String t1=FBWebOperation.openLink("");
		String t2=FBWebOperation.openLink("");
		System.out.println("t1:"+t1);
		System.out.println("t2:"+t2);
		
	}	
	
	//Construct function;
	public FBAjaxMainSearch(String Name,AjaxNode fatherNode){
		this.Name=Name;
		this.node=fatherNode;
		
		//InitHttpclientAndConnection();
	}
	private void InitHttpclientAndConnection(){
		TwitterClientManager cm=new TwitterClientManager();
		Properties pro =  new Properties();
		try {
			pro.load(new FileInputStream(new File("config/clientproperties.ini")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(pro.getProperty("http.ipv4").contains("True")){
			httpclient= cm.getClientByIpAndPort(pro.getProperty("http.proxyHost"), Integer.parseInt(pro.getProperty("http.proxyPort").trim()));
		}else if(pro.getProperty("http.ipv6").contains("True")){
			httpclient= cm.getClientNoProxy();
		}else{
			LogSys.nodeLogger.error(("没有可以访问的Internet"));
			System.exit(-1);
		}		
		httpclient.setHttpRequestRetryHandler(new myRetryHandler());
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000000); 
		if(this.node!=null&&this.node.dbOper!=null){
			this.DBOp=this.node.dbOper;
		}else{
			this.DBOp=new DbOperation();
		}
		LoginManager lgtest=new LoginManager(this.httpclient);
		lgtest.doLogin();
		System.out.println(this.Name+"完成登录验证");
		
	}
	
	
	@Override
	public void run() {
		
		System.out.println("start run");
		while(true){
			doWork();
		}
	}


	@Override
	public void doWork(){
		System.out.println("MainSearch["+Name+"] Start To doWork");		
		FriendListCrawl followingCrawl=new FriendListCrawl();
		TimeLineCrawl timelineCrawl=new TimeLineCrawl();
		DataBaseOP databaseOP = new DataBaseOP(this.DBOp);
		Task task=null;
		try{
			while(true){
				task=this.getTask();
				if(task==null){
					SLEEP(1000);
					continue;
				}
				System.out.println(this.node.NodeName+"开始任务:"+task.toString());
				Vector<com.ict.facebook.FriendList.User> users=new Vector<User>();
				Vector<com.ict.facebook.TimeLine.FBTimeLine> timelines=new Vector<com.ict.facebook.TimeLine.FBTimeLine>(30);
				switch(task.ownType){
					case Search:{
						break;
					}
					case Following:{						
						followingCrawl.doCrawl(task.getTargetString(),DBOp,users);
						databaseOP.saveUser(task.getTargetString(), users);
						databaseOP.saveUserRel(task.getTargetString(), users);
						sentNorUsers(users);						
						break;
					}
					case Followers:{
						System.err.println("Followers We exit");
						break;
					}
					case TimeLine:{
						timelineCrawl.doCrawl(task.getTargetString(),DBOp,timelines);
						databaseOP.saveTimeLine(task.targetString, timelines);
						break;
					}
					default:{
						System.err.println("未知的TaskType数据类型 exit");
						break;
					}												
				}//endSwitch
				
				System.out.println(this.node.NodeName+"结束任务:"+task.toString());
				
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.nodeLogger.error("采集发生错误("+task.targetString+")"+"("+task.ownType+")");			
		}
		
	}
	private void sentKeyUsers(Vector<TwiUser> users){
		StringBuffer sb=new StringBuffer();
		for(TwiUser t:users){
			sb.append("<name>");
			sb.append(t.name);
			sb.append("</name>");
		}
		node.addKeyUserIDs(sb.toString());
		node.ModifyReportMessageByType(ReportDataType.User, users.size());
		LogSys.nodeLogger.debug("向服务器回发KeyUser"+sb.toString());		
		
	}
	private void sentNorUsers(Vector<User> users){		
		StringBuffer sb=new StringBuffer();
		sb.append("<count>"+users.size()+"</count>");
		for(User item:users){					
			String name=item.ProfileID;
			sb.append("<name>"+name+"</name>");
			int sum=1000;
			sb.append("<sum>"+sum+"</sum>");		
		}
		node.addNomalUserIDs(sb.toString());
		node.ModifyReportMessageByType(ReportDataType.User, users.size());
		LogSys.nodeLogger.debug("向服务器回发NormalUserJms"+sb.toString());		
	}

	

	
	
	private Task getTask(){
		Task task=this.node.getTask();
		return task;
	}
	private void SLEEP(int count){
		try {
			Thread.sleep(count);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
