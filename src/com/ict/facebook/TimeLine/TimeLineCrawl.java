package com.ict.facebook.TimeLine;


import java.io.UnsupportedEncodingException;
import java.net.NoRouteToHostException;
import java.net.URLDecoder;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.json.simple.parser.ParseException;

import com.ict.facebook.*;

import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.DbOperation;

public class TimeLineCrawl {
	static ClientManager cm=new ClientManager();
	String BASE_URL=null;
	static FBOperation fbOperation =  new FBOperation();
	static boolean From_Mongo_DB=false;
	
	public static void main(String[] args) {
		WebOp.CreateHttpclient();
		WebOp.Init();
		final TimeLineCrawl testTimeLine = new TimeLineCrawl();
		final TimeLineCrawl testTimeLine2 = new TimeLineCrawl();
		Vector<FBTimeLine> fblist=new Vector<FBTimeLine>();
		testTimeLine.doCrawl("100001230554499",null,fblist);
		
		for(FBTimeLine t:fblist){
			t.show();
		}
		
		
		
	}
	public TimeLineCrawl(){
//		if(_httpclient!=null){
//			System.out.println("传入 httpclient 不为空");
//			this.httpclient=_httpclient;
//		}
//		else{
//			
//			DefaultHttpClient httpclient=cm.getClientNoProxy();	
//			LoginManager lm=new LoginManager(httpclient);
//			this.httpclient=httpclient;
//			lm.doLogin();
//		}
	}
	
	public void doCrawl(String personID,DbOperation dbo,Vector<FBTimeLine> timelines){
		String URL=generateURLByTempFile(personID);
		System.out.println("新生产的URL");
		System.out.println("https://www.facebook.com"+URL);
		WebOpenResult webres=null;
		try {
			webres = doOpen(personID,URL);
		} catch (NoRouteToHostException e) {
			System.err.println("网络连接不通畅");
			e.printStackTrace();
		}
		if(webres==null){
			LogSys.nodeLogger.error("Src 为空值，结束当前采集Task:"+personID);
			return;
		}else{
			TimeLineAnalyser ana=new TimeLineAnalyser();
			try {
				ana.doAnalyse(personID,webres.html,timelines);
			} catch (ParseException e) {
				e.printStackTrace();
				LogSys.nodeLogger.error("TimeLineAna 出错 ["+webres.html+"]");
				return;
			} catch (Exception e){
				e.printStackTrace();				
				return;
			}
			if(From_Mongo_DB&&!webres.isMongoGet){
				boolean insertres=FBOperation.insertTimeLine(personID, URL, webres.html);
				if(insertres){
					LogSys.nodeLogger.debug("Insert into MongoDB-TimeLine Success["+personID+"]");
				}
			}
		}
	}	
	
	private String generateURLByTempFile(String personID){
		//返回一个文件名来存储具体的网页链接
		if(BASE_URL==null){
			ReadTxtFile rtf=new ReadTxtFile("UsefulFile/Facebook/TempletFile/URLFile.txt");
			String baseURL=rtf.read().get(0);
			this.BASE_URL=baseURL;
		}
		String res=BASE_URL;
		if(res.contains("https://www.facebook.com"))
			res=res.substring("https://www.facebook.com".length());
		try {
			res = URLDecoder.decode(res,"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
						  //100001230554499
		res=res.replaceAll("100001230554499", personID);
		return res;
	}
	
	private WebOpenResult doOpen(String personID,String URL) throws NoRouteToHostException{
		String res=null;
		if(From_Mongo_DB&&(res=FBOperation.getTimeLine(personID))
			!=null&&res.length()>20){
			LogSys.nodeLogger.debug("Success to getTimeLineHTML from MongoDB!["+personID+"]");
			System.out.println(res);
			return new WebOpenResult(true,res);			
		}else{
			FBWebOperation.setLogFile("TimeLine.html");
			Future<String> future=WebOp.putWebOpTask(URL);
			String html;
			try {
				html = (future.get(20, TimeUnit.SECONDS));
			} catch (InterruptedException e) {
				html=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				html=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				html=null;
				e.printStackTrace();
			} 
			if(html!=null){
				LogSys.nodeLogger.debug("WebOP Success URL:"+URL);
				LogSys.nodeLogger.debug("Content["+html+"]");
				return new WebOpenResult(false,html);					
			}else{
				LogSys.nodeLogger.debug("WebOP Failed  URL:"+URL);
				return null;
			}		
			

						
		}
		
		

	}

	
	
	
	
	

}
