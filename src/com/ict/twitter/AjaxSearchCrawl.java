package com.ict.twitter;

import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
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


public class AjaxSearchCrawl extends AjaxCrawl{

	/**
	 * @param args
	 *        304600005539422208
	 * max_id=304600005539422207
	 */
	String baseURL="/i/search/timeline?src=typd&type=recent&include_available_features=1&include_entities=1%s&q='%s'";
	String max_id_str="&max_id=";
	DefaultHttpClient httpclient;
	private JSONParser parser = new JSONParser();
	
	public AjaxSearchCrawl(DefaultHttpClient _httpclient,DbOperation dboperation){
		super.dboperation=dboperation;
		this.httpclient=_httpclient;
	}
	public static void main(String[] args) {
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientNoProxy();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		AjaxSearchCrawl test=new AjaxSearchCrawl(httpclient,null);
		MulityInsertDataBase dbo = new MulityInsertDataBase();
		Vector<TwiUser> users=new Vector<TwiUser>(20);
		Task task=new Task(TaskType.Search,"����+��");
		System.out.println("current Search String Size:"+users.size());
	}
	
	
	public boolean doCrawl(Task task,MulityInsertDataBase dbo,Vector<TwiUser> RelateUsers,ReportData reportData){
		String keyWords=task.getTargetString();
		boolean has_next=false;
		String next_max_id=null;
		AjaxSearchAnalyser ana=new AjaxSearchAnalyser(dbo);
		String URL;
		int count=1;
		do{
			if(next_max_id==null||next_max_id.equals("")){
				URL=String.format(baseURL,"",keyWords);
			}else{
				URL=String.format(baseURL,max_id_str+next_max_id,keyWords);
			}
			String content=super.openLink(httpclient, URL,task,count++);
			Map map=null;
			if(content==null){
				System.out.println("HttpClint����Ajax����Ϊ�ջ򳤶Ȳ���");
				super.SaveWebOpStatus(task, URL, count, WebOperationResult.Fail, dbo);
				break;
			}
			super.SaveWebOpStatus(task, URL, count, WebOperationResult.Success, dbo);
			try {
				map = (Map)parser.parse(content);				
			}catch (ParseException e) {
				// TODO Auto-generated catch block
				LogSys.nodeLogger.debug(content);
				LogSys.nodeLogger.error("������ʱ��ǰ�ɼ��Ĺؼ�����--"+keyWords);
				e.printStackTrace();				
				return false;				
			}
			has_next=(Boolean)map.get("has_more_items");
			String html=(String)map.get("items_html");
			AnalyserCursor res=null;
			try {
				res = ana.doAnalyse(html,RelateUsers,reportData);
			} catch (AllHasInsertedException e) {
				//ϵͳ�����ظ��ɼ���ֹͣ��ǰ�ɼ�����
				has_next=false;
				LogSys.nodeLogger.debug("��ǰSearch�ɼ����["+keyWords+"]");
				break;
			}catch (Exception ex){
				has_next=false;
				LogSys.nodeLogger.debug("��ǰSearchAnalyse������������["+keyWords+"]");
				return true;
			}		
			if(map.get("max_id")!=null){
				next_max_id=(String)map.get("max_id");
			}else{
				try{
					next_max_id=Long.toString(Long.parseLong(res.lastID)-1);
				}catch(NumberFormatException ex){
					LogSys.nodeLogger.error(res.lastID);
					has_next=false;
					next_max_id="0";
				}
			} 
				
			
		}while(has_next==true);
		return true;

	}
}
