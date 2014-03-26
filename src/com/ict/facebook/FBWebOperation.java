package com.ict.facebook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.ict.twitter.TwitterClientManager;
import com.ict.twitter.plantform.LogSys;


public class FBWebOperation {

	/**
	 * @param args
	 */
	public static final String logFileBase="Output\\Facebook\\";
	public static String logFile="chichao.chen.77_timeline_2012_fb_noscript=1.html";
	public static DefaultHttpClient httpclient = null;
	public static String openLink(String linkAddress) {
		if(httpclient==null){
			InitClient();
		}
		URI uri=null;
		HttpGet httpget=null;
		int questIndex=linkAddress.indexOf("?");
		if(questIndex!=-1){
			try {
				String path=linkAddress.substring(0,linkAddress.indexOf('?'));
				String query=linkAddress.substring(linkAddress.indexOf('?')+1,linkAddress.length());
				uri=new URI(null,null,path,query,null);
				httpget= new HttpGet(uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				System.out.println("语法错误");			
				e.printStackTrace();
			}			
		}else{
			try {
				uri=new URI(null,null,linkAddress,null,null);
				httpget= new HttpGet(uri);
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}			
		}				
		httpget.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8");
		httpget.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_0);
		httpget.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
		HttpHost targetHost = new HttpHost("www.facebook.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		httpclient.getParams().setParameter(HTTP.CONTENT_ENCODING, "utf-8");
		StringBuffer sb=new StringBuffer();
		
		int lineNumber=0;
		try {
			HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
			BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				lineNumber++;
				sb.append(inputLine+"\r\n");
			}
			in.close();
			EntityUtils.consume(response.getEntity());
			
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			httpget.reset();
			
		}
		/*
		 * 当访问网络遇到错误的时候，不进行重试而是返回空值、
		 */
		String res=sb.toString();
		if(lineNumber==0){
			System.err.println("当前网页的行数是"+lineNumber);
			return null;
		}else if(res.contains("请登录后再继续")){
			LogSys.nodeLogger.debug("尚未登录，正在登录，登录后重试访问连接["+linkAddress+"]");
			return null;
		}else{
			return res;
		}
	}
	public static DefaultHttpClient InitClient(){
		LogSys.nodeLogger.debug("生成HttpClient,并且进行登录");
		TwitterClientManager cm=new TwitterClientManager();
		Properties pro =  new Properties();
		try {
			pro.load(new FileInputStream(new File("config/clientproperties.ini")));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(pro.getProperty("http.ipv4").contains("True")){
			httpclient= cm.getClientByIpAndPort(pro.getProperty("http.proxyHost"), Integer.parseInt(pro.getProperty("http.proxyPort").trim()));
		}else if(pro.getProperty("http.ipv6").contains("True")){
			httpclient= cm.getClientNoProxy();
		}else{
			LogSys.nodeLogger.error(("没有可以访问的Internet"));
			System.exit(-1);
		}
		httpclient.setHttpRequestRetryHandler(new myRetryHandler());
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 1000000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 1000000); 
		LoginManager lgtest=new LoginManager(httpclient);
		lgtest.doLogin();
		return httpclient;
	}
	
	
	
	
	public static String getLogFile() {
		return logFile;
	}

	public static void setLogFile(String logFile) {
		FBWebOperation.logFile = logFile;
	}


}
