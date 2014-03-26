package com.ict.facebook;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.PropertyConfigurator;

import com.ict.twitter.plantform.LogSys;


public class WebOp {
	//直接发起连接的类；
	static DefaultHttpClient httpclient;
	static ExecutorService service = Executors.newCachedThreadPool();
	//线程池子类；
	static HttpHost targetHost;
	static BasicHttpContext localcontext;
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void setHttpclient(DefaultHttpClient _httpclient){
		httpclient = _httpclient;
	}
	public static  boolean Init(){
		
		targetHost = new HttpHost("www.facebook.com", 443, "https");
		localcontext = new BasicHttpContext();		
		return true;
	}
	
	
	
	public static Future<String> putWebOpTask(final String targetURL){
		Future<String> future = service.submit(new Callable<String>(){
			 public String call() throws Exception {
				try{
					return OpenLink(targetURL);
				}catch(Exception ex){
					ex.printStackTrace();
					return null; 	
				}
				 					 
			 } 
		});
		return future;
	}
	
	private static String OpenLink(String linkAddress){
		URI uri=null;
		HttpGet httpget=null;
		int questIndex=linkAddress.indexOf('?');
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
		StringBuffer sb=new StringBuffer();
		try {
			HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
			BufferedReader in = new BufferedReader(new A2NFilter(new InputStreamReader(response.getEntity().getContent(),"utf-8")));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				sb.append(inputLine+"\r\n");
			}
			in.close();
			EntityUtils.consume(response.getEntity());			
		} catch (ClientProtocolException e) {
			System.out.println("ClientProtocalException e");
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("IOException e");
		}finally{
			httpget.reset();
		}		
		return sb.toString();           

	}
	
	public static void CreateHttpclient(){
		com.ict.facebook.ClientManager FacebookCm = new com.ict.facebook.ClientManager();
		PropertyConfigurator.configure ("config/log4j_Main.properties" ) ;
		Properties pro=new Properties();
		try {
			pro.load(new FileInputStream("config/clientproperties.ini"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(pro.getProperty("http.ipv4").contains("True")){				
			httpclient= FacebookCm.getClient(pro.getProperty("http.proxyHost"), Integer.parseInt(pro.getProperty("http.proxyPort").trim()));
		}else if(pro.getProperty("http.ipv6").contains("True")){
			httpclient= FacebookCm.getClientNoProxy();
		}else{
			LogSys.nodeLogger.error("IPV4/IPV6 is NOT OK");
			System.exit(-1);
		}
		LoginManager lm = new LoginManager(httpclient);
		lm.doLogin();
		LogSys.nodeLogger.info("Success Login  OK");		
		WebOp.setHttpclient(httpclient);
	}
	

}
