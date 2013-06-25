package com.ict.twitter;

import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ict.twitter.AjaxAnalyser.AnalyserCursor;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.plantform.LogSys;
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
	
	public AjaxSearchCrawl(DefaultHttpClient _httpclient){
		this.httpclient=_httpclient;
	}
	public static void main(String[] args) {
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientNoProxy();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		AjaxSearchCrawl test=new AjaxSearchCrawl(httpclient);
		MulityInsertDataBase dbo = new MulityInsertDataBase();
		Vector<TwiUser> users=new Vector<TwiUser>(20);
		test.doCrawl("����+��",dbo,users);
		System.out.println("current Search String Size:"+users.size());
	}

	public boolean doCrawl(String keyWords,MulityInsertDataBase dbo,Vector<TwiUser> RelateUsers){
		
		boolean has_next=false;
		String next_max_id=null;
		AjaxSearchAnalyser ana=new AjaxSearchAnalyser(dbo);
		String URL;
		do{
			if(next_max_id==null||next_max_id.equals("")){
				URL=String.format(baseURL,"",keyWords);
			}else{
				URL=String.format(baseURL,max_id_str+next_max_id,keyWords);
			}
			String content=super.openLink(httpclient, URL);
			Map map=null;
			if(content==null){
				System.out.println("HttpClint����Ajax����Ϊ�ջ򳤶Ȳ���");
				break;
			}
			try {
				map = (Map)parser.parse(content);				
			}catch (ParseException e) {
				// TODO Auto-generated catch block
				LogSys.nodeLogger.debug(content);
				LogSys.nodeLogger.error("������ʱ��ǰ�ɼ��Ĺؼ�����--"+keyWords);
				e.printStackTrace();				
				break;				
			}
			has_next=(Boolean)map.get("has_more_items");
			String html=(String)map.get("items_html");
			AnalyserCursor res=null;
			try {
				res = ana.doAnalyse(html,RelateUsers);
			} catch (AllHasInsertedException e) {
				//ϵͳ�����ظ��ɼ���ֹͣ��ǰ�ɼ�����
				has_next=false;
				LogSys.nodeLogger.debug("��ǰSearch�ɼ����["+keyWords+"]");
				break;
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
