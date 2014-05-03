package com.ict.twitter;

import java.util.Vector;

import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import com.ict.twitter.CrawlerNode.AjaxNode;
import com.ict.twitter.Report.ReportData;
import com.ict.twitter.Report.ReportDataType;
import com.ict.twitter.StatusTrack.MyTracker;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;
import com.ict.twitter.hbase.*;
public class AjaxMainSearch extends AjaxMainSearchFrameWork {
	boolean test=true;
	MyTracker tracker=new MyTracker();
	MessageTwitterHbase msghbase;
	UserRelTwitterHbase userrelhbase;
	UserTwitterHbase userhbase;
	
	public AjaxMainSearch(String Name,AjaxNode fatherNode){
		this.Name=Name;
		this.node=fatherNode;
	}
	
	private void InitHttpclientAndConnection(){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient=null;
		if(this.node.isProxy){
			httpclient=cm.getClientByIpAndPort(this.node.proxyAddress, this.node.proxyPort);
			System.err.println("启用了代理服务器");
		}else{
			httpclient= cm.getClientNoProxy();
		}
		httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		this.httpclient=httpclient;
		if(this.node.dbOper!=null){
			this.DBOp=this.node.dbOper;
		}else{
			this.DBOp=new DbOperation();
		}
		if(this.node.Hbase_Enable){
			InitHbase();
		}
		
	}
	
	private void InitHbase(){
		msghbase=new MessageTwitterHbase("message");
		userrelhbase=new UserRelTwitterHbase("user_relationship");
		userhbase=new UserTwitterHbase("user");
	}
	
	@Override
	public void run() {
		InitHttpclientAndConnection();
		System.out.println("start run");
		while(true){
			doWork();
		}
	}
	
	@Override
	public void doWork(){
		LogSys.nodeLogger.info("MainSearch["+Name+"] Start To doWork");		
		AjaxFollowCrawl followingCrawl=new AjaxFollowCrawl(this.httpclient,true,this.DBOp);
		AjaxFollowCrawl followerCrawl=new AjaxFollowCrawl(this.httpclient,false,this.DBOp);
		followerCrawl.isFollowing=false;
		
		AjaxSearchCrawl searchCrawl=new AjaxSearchCrawl(this.httpclient,this.DBOp);
		AjaxTimeLineCrawl timelineCrawl=new AjaxTimeLineCrawl(this.httpclient,this.DBOp);	
		AjaxProfileCrawl profileCrawl = new AjaxProfileCrawl(this.httpclient,this.DBOp);
		MulityInsertDataBase batchdb =  new MulityInsertDataBase();
		followingCrawl.SetHabae(userrelhbase,this.node.Hbase_Enable);
		searchCrawl.SetHabae(msghbase,this.node.Hbase_Enable);
		timelineCrawl.SetHabae(msghbase,this.node.Hbase_Enable);
		profileCrawl.SetHabae(userhbase,this.node.Hbase_Enable);
		try{
			while(true){
				Task task=this.getTask();
				if(task==null){
					SLEEP(1000);
					continue;
				}
				Vector<TwiUser> users=new Vector<TwiUser>(30);
				ReportData reportData=new ReportData();
				boolean flag=false;
				String ErrorMsg="";
				switch(task.ownType){
					case Search:{
						flag=searchCrawl.doCrawl(task,batchdb,users,reportData);
						sentKeyUsers(users);
						break;
					}
					case Following:{						
						flag=followingCrawl.doCrawl(task,batchdb,users,reportData);
						sentNorUsers(users);
						break;
					}
					case Followers:{
						flag=followerCrawl.doCrawl(task,batchdb,users,reportData);
						sentNorUsers(users);
						break;
					}
					case TimeLine:{
						flag=timelineCrawl.doCrawl(task,batchdb,users,reportData);
						sentKeyUsers(users);
						ErrorMsg=timelineCrawl.ErrorMsg;
						break;
					}
					case About:{
						flag=profileCrawl.doCrawl(task, batchdb, users,reportData);
						break;
					}
					default:{
						LogSys.nodeLogger.error("未知的TaskType数据类型 exit");
						break;
					}												
				}
				TwiUser[] userArray=new TwiUser[users.size()];
				users.toArray(userArray);
				try{
					if(users.size()>0){
						batchdb.insertIntoUser(userArray,"user");
					}
				}catch(AllHasInsertedException ex){
					LogSys.nodeLogger.debug("所有用户均已经插入：Task:["+task.toString()+"]");
				}

				if(flag){
					tracker.FinishTask(task,ErrorMsg);
				}else{
					tracker.FailTask(task,ErrorMsg);
				}
				//节点中的汇报数据进行累加，累加后MainSearch中数据清零。
				LogSys.nodeLogger.debug(String.format("Nodename:[%s] message:%d,user:%d,userrel%d",this.node.NodeName,reportData.message_increment,reportData.user_increment,reportData.message_rel_increment));
				node.rpdata.add(reportData);
				reportData=null;
			}
		}catch(Exception ex){
			LogSys.nodeLogger.error("采集发生错误");
			ex.printStackTrace();
		}
		
	}
	private void sentKeyUsers(Vector<TwiUser> users){
		StringBuffer sb=new StringBuffer();
		for(TwiUser t:users){
			sb.append("<name>");
			String name=t.getName();
			if(name.contains("@")){
				name=name.replaceAll("@", "");
			}
			sb.append(name);
			sb.append("</name>");
		}
		node.addKeyUserIDs(sb.toString());
		LogSys.nodeLogger.debug("Send To Server KeyUser"+sb.toString());		
		
	}
	private void sentNorUsers(Vector<TwiUser> users){		
		StringBuffer sb=new StringBuffer();
		sb.append("<count>"+users.size()+"</count>");
		for(TwiUser item:users){					
			String name=item.name;
			sb.append("<name>"+name+"</name>");
			int sum=1000;
			sb.append("<sum>"+sum+"</sum>");		
		}
		node.addNomalUserIDs(sb.toString());
		LogSys.nodeLogger.info("向服务器回发NormalUserJms"+sb.toString());				
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

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		AjaxMainSearch mainSearch=new AjaxMainSearch("FIRST_MAINSEARCH",null);
		Thread mainSearchThread=new Thread(mainSearch);
		mainSearchThread.setName(mainSearch.Name);
		mainSearchThread.start();

	}

}
