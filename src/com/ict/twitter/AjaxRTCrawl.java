package com.ict.twitter;

import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ict.twitter.Report.ReportData;
import com.ict.twitter.analyser.beans.MessageDetail;
import com.ict.twitter.analyser.beans.MessageReteet;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxRTCrawl extends AjaxCrawl {
	String messageid;
	String baseUrl="";
	private DefaultHttpClient httpclient;
	public AjaxRTCrawl(DefaultHttpClient httpclient,DbOperation dboperation){
		super.dboperation=dboperation;
		baseUrl="/i/expanded/batch/$?facepile_max=50&include%5B%5D=social_proof&include%5B%5D=ancestors&include%5B%5D=descendants&page_context=profile&section_context=profile";
		this.httpclient=httpclient;
	}

	
	@Override
	public boolean doCrawl(Task task, MulityInsertDataBase dbo,Vector<TwiUser> RelateUsers, ReportData reportData) {
		// TODO Auto-generated method stub
		System.out.println("Current Message"+task.getTargetString());
		messageid=task.getTargetString();
		JSONParser parser=new JSONParser();
		String URL="";
		try{
			URL=baseUrl.replaceAll("\\$", messageid);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
		int count=0;
		String content=openLink(httpclient, URL,task,count);
		if(content==null||(content.length())<=20){
			System.out.println("网页返回为空 采集结束");
			super.SaveWebOpStatus(task, URL, count, WebOperationResult.Fail, dbo);
			return false;
		}
		super.SaveWebOpStatus(task, URL, count, WebOperationResult.Success, dbo);
		Map<String,Object> map=null;
		try {
			map = (Map<String,Object>) parser.parse(content);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String HTML=(String) map.get("descendants");
		Vector<MessageReteet> allReteet=new Vector<MessageReteet>();
		AjaxRTAnalyser arta=new AjaxRTAnalyser(dbo, task);
		try {
			arta.doAnalyser(HTML, allReteet);
			System.out.println("Analyse Result:"+allReteet.size());
			MessageReteet[] ss=allReteet.toArray(new MessageReteet[allReteet.size()]);			
			dbo.insertIntoMessageReTeet(ss);
		} catch (AllHasInsertedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return false;
	}
	

	
	
	
	public static void main(String[] args){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.67", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		MulityInsertDataBase dbo=new MulityInsertDataBase();
		AjaxRTCrawl reteetCrawl=new AjaxRTCrawl(httpclient,null);
		Vector<TwiUser> users=new Vector<TwiUser>(20);
				
		reteetCrawl.doCrawl(new Task(TaskType.MessageRel,"441031275470929920"),dbo,users,new ReportData());
		
	}

}
