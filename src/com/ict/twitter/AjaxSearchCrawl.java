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
	 * 
	 * https://twitter.com
	 */
	String baseURL="/i/search/timeline?q='%s'&src=typd&f=realtime&include_available_features=1&include_entities=1&last_note_ts=0";
	String max_id_str="&scroll_cursor=%s";
	DefaultHttpClient httpclient;
	private JSONParser parser = new JSONParser();
	
	public AjaxSearchCrawl(DefaultHttpClient _httpclient,DbOperation dboperation){
		super.dboperation=dboperation;
		this.httpclient=_httpclient;
	}
	public static void main(String[] args) {
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.67",8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		AjaxSearchCrawl test=new AjaxSearchCrawl(httpclient,null);
		MulityInsertDataBase dbo = new MulityInsertDataBase();
		Vector<TwiUser> users=new Vector<TwiUser>(20);
		Task task=new Task(TaskType.Search,"薄熙来");
		ReportData reportData=new ReportData();
		test.doCrawl(task, dbo, users, reportData);
		System.out.println("current Search String Size:"+users.size());
	}
	
	
	public boolean doCrawl(Task task,MulityInsertDataBase dbo,Vector<TwiUser> RelateUsers,ReportData reportData){
		String keyWords=task.getTargetString();
		boolean has_next=false;
		String next_max_id=null;
		AjaxSearchAnalyser ana=new AjaxSearchAnalyser(dbo,task);
		String URL;
		int count=1;
		do{
			if(next_max_id==null||next_max_id.equals("")){
				URL=String.format(baseURL,keyWords,"");
			}else{
				URL=String.format(baseURL+max_id_str,keyWords,next_max_id);
			}
			String content=super.openLink(httpclient, URL,task,count++);
			
			Map map=null;
			if(content==null){
				System.out.println("HttpClint返回Ajax内容为空或长度不够");
				super.SaveWebOpStatus(task, URL, count, WebOperationResult.Fail, dbo);
				break;
			}
			super.SaveWebOpStatus(task, URL, count, WebOperationResult.Success, dbo);
			try {
				map = (Map)parser.parse(content);				
			}catch (ParseException e) {
				// TODO Auto-generated catch block
				LogSys.nodeLogger.debug(content);
				LogSys.nodeLogger.error("错误发生时当前采集的关键词是--"+keyWords);
				e.printStackTrace();				
				return false;				
			}
			has_next=(Boolean)map.get("has_more_items");
			String html=(String)map.get("items_html");
			AnalyserCursor res=null;
			try {
				res = ana.doAnalyse(html,RelateUsers,reportData);
			} catch (AllHasInsertedException e) {
				//系统发现重复采集故停止当前采集任务；
				has_next=false;
				LogSys.nodeLogger.debug("当前Search采集完成["+keyWords+"]");
				break;
			}catch (Exception ex){
				has_next=false;
				LogSys.nodeLogger.debug("当前SearchAnalyse解析发生错误["+keyWords+"]");
				return true;
			}	
			if(map.get("has_more_items")!=null){
				has_next=(boolean)map.get("has_more_items");
			}else{
				has_next=false;
			}
			if(has_next){
				next_max_id=(String)map.get("scroll_cursor");
			}
				
			
		}while(has_next==true);
		return true;

	}
}
