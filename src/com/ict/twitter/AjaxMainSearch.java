package com.ict.twitter;

import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import com.ict.twitter.CrawlerNode.AjaxNode;
import com.ict.twitter.Report.ReportDataType;
import com.ict.twitter.StatusTrack.MyTracker;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxMainSearch extends AjaxMainSearchFrameWork {
	boolean test=true;
	MyTracker tracker=new MyTracker();
	public AjaxMainSearch(String Name,AjaxNode fatherNode){
		this.Name=Name;
		this.node=fatherNode;
	}
	
	private void InitHttpclientAndConnection(){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientNoProxy();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		this.httpclient=httpclient;
		if(this.node.dbOper!=null){
			this.DBOp=this.node.dbOper;
		}else{
			this.DBOp=new DbOperation();
		}
		
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
		AjaxFollowCrawl followingCrawl=new AjaxFollowCrawl(this.httpclient,true);
		AjaxFollowCrawl followerCrawl=new AjaxFollowCrawl(this.httpclient,false);
		followerCrawl.isFollowing=false;
		
		AjaxSearchCrawl searchCrawl=new AjaxSearchCrawl(this.httpclient);
		AjaxTimeLineCrawl timelineCrawl=new AjaxTimeLineCrawl(this.httpclient);	
		AjaxProfileCrawl profileCrawl = new AjaxProfileCrawl(this.httpclient);
		MulityInsertDataBase batchdb =  new MulityInsertDataBase();
		try{
			while(true){
				Task task=this.getTask();
				if(task==null){
					SLEEP(1000);
					continue;
				}
				Vector<TwiUser> users=new Vector<TwiUser>(30);
				boolean flag=false;
				switch(task.ownType){
					case Search:{
						flag=searchCrawl.doCrawl(task.getTargetString(),batchdb,users);
						sentKeyUsers(users);
						break;
					}
					case Following:{						
						flag=followingCrawl.doCrawl(task.getTargetString(),batchdb,users);
						sentNorUsers(users);
						break;
					}
					case Followers:{
						flag=followerCrawl.doCrawl(task.getTargetString(),batchdb,users);
						sentNorUsers(users);
						break;
					}
					case TimeLine:{
						flag=timelineCrawl.doCrawl(task.getTargetString(),batchdb,users);
						break;
					}
					case About:{
						flag=profileCrawl.doCrawl(task.getTargetString(), batchdb, users);
					}
					default:{
						LogSys.nodeLogger.error("未知的TaskType数据类型 exit");
						break;
					}												
				}
				
				TwiUser[] userArray=new TwiUser[users.size()];
				users.toArray(userArray);
				if(users.size()>0){
					batchdb.insertIntoUser(userArray);
				}
				if(flag){
					tracker.FinishTask(task);
				}else{
					tracker.FailTask(task);
				}			
				
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
			sb.append(t.name);
			sb.append("</name>");
		}
		node.addKeyUserIDs(sb.toString());
		node.ModifyReportMessageByType(ReportDataType.User, users.size());
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
		node.ModifyReportMessageByType(ReportDataType.User, users.size());
				
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
