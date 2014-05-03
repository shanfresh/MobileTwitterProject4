package com.ict.twitter;

import java.util.Vector;
import java.util.concurrent.*;

import org.apache.http.impl.client.DefaultHttpClient;

import com.ict.twitter.DatabaseBean.WebOpLogOp;
import com.ict.twitter.Report.ReportData;
import com.ict.twitter.StatusTrack.MyTracker;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.hbase.TwitterHbase;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public abstract class AjaxCrawl {

	/**
	 * @param args
	 */

	public ExecutorService service = Executors.newCachedThreadPool();
	public WebOpLogOp weboplog;
	public DbOperation dboperation;

	/////////////////////////////HBASE///////////////////////////////////////////
	protected boolean Hbase_Enable=false;
	protected TwitterHbase hbase=null; 
	//////////////////////////////////////////////////////////////////////////////
	String ErrorMsg;
	/////////////////////////////////////////////////////////////////////////////
	public void SetHabae(TwitterHbase hbase,boolean IsEnable){
		Hbase_Enable=IsEnable;
		this.hbase=hbase;
	}
	
	boolean Total_Crawl_FLAG=false;
	
	public abstract boolean doCrawl(Task task,MulityInsertDataBase dbo,Vector<TwiUser> RelateUsers,ReportData reportData);
	
	public String openLink(final DefaultHttpClient httpclient,final String targetUrl,final Task task,final int count,WebOperationResult webres) {
		String WebPageContent = null;
		Future<String> future = service.submit(new Callable<String>() {
			public String call() throws Exception {
				try {
					return WebOperationAjax.openLink(httpclient,task,targetUrl,count);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return null;
			}
		});
		
		try{
			WebPageContent = (String) future.get(20000, TimeUnit.MILLISECONDS);			
		}catch(TimeoutException ex){
			LogSys.nodeLogger.error("OpenURL TimeOut(20s):" + targetUrl);
			webres=WebOperationResult.TimeOut;
		}catch (Exception e) {
			e.printStackTrace();
			LogSys.nodeLogger.error(e.getMessage());
			LogSys.nodeLogger.error("OpenURL Error URL:" + targetUrl);
			webres=WebOperationResult.Fail;
			WebPageContent = null;
		}
		if(WebPageContent == null){
			
		}else{
			
		}
		return WebPageContent;

			
	}
	//保存采集到的Web请求日志信息
	public boolean SaveWebOpStatus(Task task,String URL,int count,WebOperationResult webrs,MulityInsertDataBase dbo){
		if(weboplog==null){
			if(dboperation==null){
				dboperation=new DbOperation();
			}
			weboplog=new WebOpLogOp(dboperation.GetConnection());
		}
		try{
			String taskName=task.getTargetString();
			String MainType=task.getMainType().toString();
			String taskType=task.getOwnType().toString();
			String CurrentURL=URL;
			String resultStr= webrs.toString();
			return weboplog.Insert(task.getTargetString(), task.getMainType().toString(), task.ownType.toString(),URL,webrs.toString(), count);
			
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		
	}
	

}
