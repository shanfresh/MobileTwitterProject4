package com.ict.twitter;

import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ict.twitter.Report.ReportData;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.hbase.MessageTwitterHbase;
import com.ict.twitter.hbase.TwitterHbase;
import com.ict.twitter.hbase.UserRelTwitterHbase;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxFollowCrawl extends AjaxCrawl{
	public boolean isFollowing=true;
	/**
	 * @param args
	 */
	private String baseUrl="";
	//
	//private String cursor="cursor=1417310652538012226";
	private final String cursor="&cursor=";
	private DefaultHttpClient httpclient;
	private JSONParser parser = new JSONParser();
	
	/*
	 * isFollowing true:following
	 * isFollowing false:follower
	 */
	public AjaxFollowCrawl(DefaultHttpClient httpclient,boolean isFollowing,DbOperation dboperation){
		super.dboperation=dboperation;
		if(isFollowing){
			this.isFollowing=true;
			baseUrl="/%s/following/users?%sinclude_available_features=1&include_entities=1&is_forward=true";;
		}else{
			this.isFollowing=false;
			baseUrl="/%s/followers/users?%sinclude_available_features=1&include_entities=1&is_forward=true";;
		}
		this.httpclient=httpclient;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean doCrawl(Task task,MulityInsertDataBase batchdb,Vector<TwiUser> RelateUsers,ReportData reportData){
		String userID=task.getTargetString();
		String URL;
		boolean hasMoreItems=false;
		AjaxFollowAnalyser aa=new AjaxFollowAnalyser(batchdb,task);
		if(this.Hbase_Enable){
			aa.SetHabae(hbase, this.Hbase_Enable);
		}
		String nextCursor="";
		int count=0;
		do{
			count++;
			int retryCount=0;
			hasMoreItems=false;
			if(nextCursor.equals("")){
				URL=String.format(baseUrl, userID,"");
			}else{
				URL=String.format(baseUrl, userID,cursor+nextCursor);
			}
			String content=openLink(httpclient, URL,task,retryCount);
			if(content==null||(content.length())<=20){
				System.out.println("网页返回为空 采集结束");
				super.SaveWebOpStatus(task, URL, count, WebOperationResult.Fail, batchdb);
				return false;
			}
			super.SaveWebOpStatus(task, URL, count, WebOperationResult.Success, batchdb);
			Map<String, Object> map = null;
			int index=0;
			try {
				map = (Map<String,Object>) parser.parse(content);
			}catch (ParseException e) {
				// TODO Auto-generated catch block
				LogSys.nodeLogger.error("错误发生时当前采集的用户是--"+userID);
				e.printStackTrace();
				break;
			}
			try{
				Object hasmore = map.get("has_more_items");
				String items_html=(String)map.get("items_html");
				Vector<TwiUser> needTosave=new Vector<TwiUser>();				
				index=aa.doAnalyse(userID,isFollowing,items_html,needTosave);
				SaveToDataBase(task,batchdb,needTosave);
				
				if(hasmore!=null){
					hasMoreItems=(Boolean)hasmore;
					if(hasMoreItems)
						nextCursor=(String)map.get("cursor");
					else{
						nextCursor=null;
					}
				}
				reportData.user_increment+=index;
				reportData.user_rel_increment+=index;
			}catch(AllHasInsertedException ex){
				LogSys.nodeLogger.error("当前采集的用户下所有Following采集完毕--"+userID);
				break;
			}
			catch (Exception e) {
				LogSys.nodeLogger.error("错误发生时当前采集的用户是--"+userID);
				e.printStackTrace();
				return false;
			}
		}while(hasMoreItems&&nextCursor!=null);
		
		
		
		return true;
	}
	
	private void SaveToDataBase(Task task,MulityInsertDataBase dbo,Vector<TwiUser> RelateUsers){
		TwiUser[] userArray=new TwiUser[RelateUsers.size()];
		RelateUsers.toArray(userArray);
		try{
			if(RelateUsers.size()>0){
				dbo.insertIntoUser(userArray,"user_wenyunchao");
			}
		}catch(AllHasInsertedException ex){
			LogSys.nodeLogger.debug("所有用户均已经插入：Task:["+task.toString()+"]");
		}
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.67", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		AjaxFollowCrawl at=new AjaxFollowCrawl(httpclient,true,null);
		UserRelTwitterHbase userrelhbase=new UserRelTwitterHbase("user_relationship");
		at.SetHabae(userrelhbase, true);
		Vector<TwiUser> users=new Vector<TwiUser>(20);
		MulityInsertDataBase dbo=new MulityInsertDataBase();
		
		Task task=new Task(TaskType.Following,"wenyunchao");
		task.setTargetTableName("user_relationship");
		at.doCrawl(task,dbo,users,new ReportData());		
		System.out.println(users.size());
		


	}


}
