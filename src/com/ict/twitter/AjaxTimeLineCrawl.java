package com.ict.twitter;

import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.log4j.net.SyslogAppender;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;



import com.ict.twitter.AjaxAnalyser.AnalyserCursor;
import com.ict.twitter.Report.ReportData;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.hbase.MessageTwitterHbase;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

/*
 * 采用ajax 的手法来分析具体的Timeline
 *  1:包括一个weboperation
 *  2:一个结果的迭代器
 */
public class AjaxTimeLineCrawl extends AjaxCrawl{
	public enum PageType{
		Old_Page,New_Page;
	}
	
	/*
	 *https://twitter.com/i/profiles/show/BigBang_CBS/timeline?include_available_features=1&include_entities=1&last_note_ts=0&max_id=431598637420789760
	 */
	private String baseUrl="/i/profiles/show/%s/timeline?include_available_features=1&include_entities=1%s";
	private String max_id="&max_id=";
	
	private DefaultHttpClient httpclient;
	private JSONParser parser = new JSONParser();
	

	/**
	 * 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.67", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		AdvanceLoginManager lgtest=new AdvanceLoginManager(httpclient);
		lgtest.trylogin();
		AjaxTimeLineCrawl at=new AjaxTimeLineCrawl(httpclient,null);
		//MessageTwitterHbase msghbase=new MessageTwitterHbase("message");
		//at.SetHabae(msghbase, false);
		
		Vector<TwiUser> users=new Vector<TwiUser>();
		MulityInsertDataBase dbo=new MulityInsertDataBase();
		//Task task=new Task(TaskType.TimeLine,"chobogeinou");
		
		Task task=new Task(TaskType.TimeLine,"wenyunchao");
		task.setTargetTableName("message_AnalyseTest");
		task.setPageCount(-1);
		at.doCrawl(task,dbo,users,new ReportData());
		//at.doCrawl(new Task(TaskType.TimeLine,"mynamexu"),dbo,users,new ReportData());
		at.service.shutdown();

	}
	public AjaxTimeLineCrawl(DefaultHttpClient httpclient,DbOperation dboperation){		
		this.httpclient=httpclient;
		super.dboperation=dboperation;
	}
	
	
	
	public boolean doCrawl(Task task,MulityInsertDataBase dbo,Vector<TwiUser> RelatUsers,ReportData reportData){
		String userID=task.getTargetString();
		boolean has_more_items=false;
		String nextmaxID="459711924100280319";
		String URL="";
		AjaxTimeLineAnalyser TWAna=new AjaxTimeLineAnalyser(dbo,task);
		AjaxTimeLineAnalyserNew TWAnaNew=new AjaxTimeLineAnalyserNew(dbo, task);
		TWAna.SetHabae(this.hbase, this.Hbase_Enable);
		
		boolean flag=true;
		AnalyserCursor result;
		int count=0;
		int targetPageCount=task.getPageCount();
		do{
			int retryCount=0;
			WebOperationResult webres=WebOperationResult.Success;
			if(nextmaxID==null||nextmaxID.equals("")){
				URL=String.format(baseUrl, userID,"");
			}else{
				URL=String.format(baseUrl, userID,max_id+nextmaxID);
			}
			
			String content=openLink(httpclient, URL,task,retryCount,webres);
			if(!(this.CheckValidation(content))){
				flag=false;
				ErrorMsg="页面被冻结";
				LogSys.nodeLogger.error("错误发生时当前采集的用户是--"+userID+"Reason:"+ErrorMsg);
				super.SaveWebOpStatus(task, URL, count, WebOperationResult.Success, dbo);
				break;
			}
			if(content==null||(content.length())<=20){
				ErrorMsg="网络错误，返回长度不足";
				super.SaveWebOpStatus(task, URL, count, WebOperationResult.TimeOut, dbo);
				has_more_items=false;
				flag=false;
				break;
			}
			super.SaveWebOpStatus(task, URL, count, WebOperationResult.Success, dbo);
			try{
				Map<?, ?> json=(Map<?, ?>) parser.parse(content);
				String html=(String) json.get("items_html");
				has_more_items=(Boolean)json.get("has_more_items");
				PageType page_type=this.ChekPageType(content);
				if(page_type==PageType.New_Page){
					result=TWAnaNew.doAnalyser(html, RelatUsers);
				}else{
				//旧版本分析页面
					result=TWAna.doAnalyser(html,RelatUsers);
				}try{
					Long resultMax=Long.parseLong(result.lastID);
					resultMax=resultMax-1l;
					nextmaxID=resultMax.toString();
				}catch(NumberFormatException ex){
					nextmaxID=result.lastID;
				}
				
				
			}catch(ParseException ex){
				has_more_items=false;
				flag=false;
				break;
				
			}catch(AllHasInsertedException ex){
				LogSys.nodeLogger.error("当前用户所有推文已经采集完毕--"+userID);
				break;
			}
			catch(Exception ex){
				LogSys.nodeLogger.error("错误发生时当前采集的用户是--"+userID);
				ex.printStackTrace();
				flag=false;
				break;
			}
			count++;//正确执行完毕了Count++;
			reportData.message_increment+=result.size;
			if(targetPageCount!=-1&&count>targetPageCount){
				System.out.println("受限深度的采集，跳出"+targetPageCount+"/"+count);
				break;
			}
			
			
		}while(has_more_items);
		System.out.println("共分析了"+count+"次 (20twi)");
		return flag;
		
		
	}
	private boolean CheckValidation(String content){
		if(content.contains("flex-module error-page clearfix")||content.contains("你试图查看的个人资料已被冻结")){
			return false;
		}
		return true;
	}
	
	//检查页面的类型
	//缺少鲁棒性检查
	private PageType ChekPageType(String content){
		if(content.contains("Grid-cell")){
			return PageType.New_Page;
		}else{
			return PageType.Old_Page;
		}		
	}

}
