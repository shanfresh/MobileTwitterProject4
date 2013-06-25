package com.ict.twitter;

import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.plantform.LogSys;
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
	public AjaxFollowCrawl(DefaultHttpClient httpclient,boolean isFollowing){
		if(isFollowing){
			baseUrl="/%s/following/users?%sinclude_available_features=1&include_entities=1&is_forward=true";;
		}else{
			baseUrl="/%s/followers/users?%sinclude_available_features=1&include_entities=1&is_forward=true";;
		}
		this.httpclient=httpclient;
	}
	@SuppressWarnings("unchecked")
	@Override
	public boolean doCrawl(String userID,MulityInsertDataBase batchdb,Vector<TwiUser> RelateUsers){
		
		String URL;
		boolean hasMoreItems=false;
		AjaxFollowAnalyser aa=new AjaxFollowAnalyser(batchdb);
		String nextCursor="";
		do{
			hasMoreItems=false;
			if(nextCursor.equals("")){
				URL=String.format(baseUrl, userID,"");
			}else{
				URL=String.format(baseUrl, userID,cursor+nextCursor);
			}
			String content=openLink(httpclient, URL);
			if(content==null||(content.length())<=20){
				System.out.println("网页返回为空 采集结束");
				break;
			}
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
				index=aa.doAnalyse(userID,isFollowing,items_html,RelateUsers);	
				if(hasmore!=null){
					hasMoreItems=(Boolean)hasmore;
					if(hasMoreItems)
						nextCursor=(String)map.get("cursor");
					else{
						nextCursor=null;
					}
				}
			}catch(AllHasInsertedException ex){
				LogSys.nodeLogger.error("当前采集的用户下所有Following采集完毕--"+userID);
				break;
			}
			catch (Exception e) {
				LogSys.nodeLogger.error("错误发生时当前采集的用户是--"+userID);
				e.printStackTrace();
				break;
			}			
					
		}while(hasMoreItems&&nextCursor!=null);

		return true;
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientNoProxy();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		AjaxFollowCrawl at=new AjaxFollowCrawl(httpclient,false);
		Vector<TwiUser> users=new Vector<TwiUser>(20);
		MulityInsertDataBase dbo=new MulityInsertDataBase();
		at.doCrawl("26_t_b",dbo,users);
		

	}


}
