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
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

/*
 * ����ajax ���ַ������������Timeline
 *  1:����һ��weboperation
 *  2:һ������ĵ�����
 */
public class AjaxTimeLineCrawl extends AjaxCrawl{

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
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		AjaxTimeLineCrawl at=new AjaxTimeLineCrawl(httpclient,null);
		Vector<TwiUser> users=new Vector<TwiUser>();
		MulityInsertDataBase dbo=new MulityInsertDataBase();
		at.doCrawl(new Task(TaskType.TimeLine,"wenyunchao"),dbo,users,new ReportData());
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
		String nextmaxID="416792326216773631";
		String URL="";
		AjaxTimeLineAnalyser TWAna=new AjaxTimeLineAnalyser(dbo,task);
		boolean flag=true;
		AnalyserCursor result;
		int count=1;
		int targetPageCount=task.getPageCount();
		do{
			if(nextmaxID==null||nextmaxID.equals("")){
				URL=String.format(baseUrl, userID,"");
			}else{
				URL=String.format(baseUrl, userID,max_id+nextmaxID);
			}
			
			String content=openLink(httpclient, URL,task,count);
			if(content==null||(content.length())<=20){
				System.err.println("web opreation error content is null");
				super.SaveWebOpStatus(task, URL, count, WebOperationResult.Fail, dbo);
				has_more_items=false;
				flag=false;
				break;
			}
			super.SaveWebOpStatus(task, URL, count, WebOperationResult.Success, dbo);
			try{
				Map<?, ?> json=(Map<?, ?>) parser.parse(content);
				String html=(String) json.get("items_html");
				has_more_items=(Boolean)json.get("has_more_items");
				//�ɼ����
				result=TWAna.doAnalyser(html,RelatUsers);
				try{
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
				LogSys.nodeLogger.error("��ǰ�û����������Ѿ��ɼ����--"+userID);
				break;
			}
			catch(Exception ex){
				LogSys.nodeLogger.error("������ʱ��ǰ�ɼ����û���--"+userID);
				ex.printStackTrace();
				flag=false;
				break;
			}
			count++;
			reportData.message_increment+=result.size;
			if(targetPageCount!=-1&&count){
				
			}
			
		}while(has_more_items);
		System.out.println("��������"+count+"�� (20twi)");
		return flag;
		
		
	}

}
