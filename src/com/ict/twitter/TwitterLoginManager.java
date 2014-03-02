package com.ict.twitter;

import java.io.BufferedReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.Test;

import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.MyCookieStore;
import com.ict.twitter.tools.ReadTxtFile;
import com.ict.twitter.tools.SaveTxtFile;
import com.ict.twitter.tools.SavitchIn;

public class TwitterLoginManager {

	class Account{
		String username,password;
	}
	/**
	 * @param args
	 */
	DefaultHttpClient httpclient;
	final String ACCOUNT_FILE_PATH="config/TwitterAccounts.txt";
	public TwitterLoginManager(DefaultHttpClient _httpclient){
		httpclient=_httpclient;

	}
	public static void main(String[] args){
		LogSys.nodeLogger.info("StartTest");
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientNoProxy();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		String[] items=lgtest.getAccounts();
		System.out.println(items[0]);
//		lgtest.disableCountsByName(items[0]);
		lgtest.forceLogin();
//		if(lgtest.checkLoginStatus()){
//			System.out.println("登录成功");
//		}else{
//			System.out.println("登录失败");
//		}
	}

	public synchronized boolean forceLogin(){
		boolean logined=false;
		String user,pass;
		String[] loginInfo=this.getAccounts();
		if(loginInfo==null){
			LogSys.nodeLogger.error("所有账户均被封禁,请解除封禁后尝试");
			SavitchIn sf = new SavitchIn();
			System.out.println("是否已经重新解除了封禁账户？");
			char q = sf.readChar();
			while(q!='y'){
				System.out.println("是否已经重新解除了封禁账户？");
				q = sf.readChar();
			}
			this.enableAllCounts();
			return forceLogin();
		}else{
			user=loginInfo[0];
			pass=loginInfo[1];
		}
		try{
			LogSys.nodeLogger.debug("准备进行用户登录操作");
			HttpGet httpget = new HttpGet("https://twitter.com/");
			HttpResponse response = httpclient.execute(httpget);
	        HttpEntity entity = response.getEntity();
	        String content=SaveToHtml(entity,"Output/Twitter/LogBefore.html");
	        String token=null;
	        if(content!=null)
	        	token=this.getToken(content);
	        System.out.println("Token："+token);	        
	        System.out.println("--------------");
	        LogSys.nodeLogger.debug("Login form get: " + response.getStatusLine());
	        EntityUtils.consume(entity);	
	        LogSys.nodeLogger.debug("Initial set of cookies:");
	        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
	        if (cookies.isEmpty()) {
	        	LogSys.nodeLogger.debug("None");
	        } else {
	            for (int i = 0; i < cookies.size(); i++) {
	            	LogSys.nodeLogger.debug("- " + cookies.get(i).toString());
	            }
	        }	        
	        HttpPost httpost = new HttpPost("https://twitter.com/sessions");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("session[username_or_email]", user));
            nvps.add(new BasicNameValuePair("session[password]", pass));
            nvps.add(new BasicNameValuePair("authenticity_token", token));
          
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            response = httpclient.execute(httpost);
            entity = response.getEntity();
            SaveToHtml(entity,"Output/Twitter/LogAfter.html");
            LogSys.nodeLogger.debug("Login form get: " + response.getStatusLine());
            EntityUtils.consume(entity);
            LogSys.nodeLogger.debug("Post logon cookies:");
            httpclient.getCookieSpecs();
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
            	LogSys.nodeLogger.debug("None");
            	logined=false;
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                	LogSys.nodeLogger.info("- " + cookies.get(i).toString());
                	if(cookies.get(i).getName().equals("auth_token")){
                		LogSys.nodeLogger.info("Success To Login To Twitter");
                		logined=true;
                	}
                }
                if(logined){
                    LogSys.nodeLogger.info("Success To Save Cookies");
                }else{
                	LogSys.nodeLogger.info("Fail To Login To Twitter RM the datFile");
                }
            }
            if(logined){
            	TwitterLoginCookieStore mycookiestore = (TwitterLoginCookieStore) httpclient.getCookieStore();
                //mycookiestore.savetofile("Output/Twitter/TwitterLoginCookie.dat");
            }else{
            	File f =  new File("Output/Twitter/TwitterLoginCookie.dat");
            	if(f!=null&&f.exists()&&f.isFile()){
            		f.delete();
            	}
            	this.disableCountsByName(user);
            	
            }
        	
            

		}catch(org.apache.http.conn.ConnectionPoolTimeoutException ex){
			ex.printStackTrace();
			return false;
		}
		catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("登录失败with User"+user+" Password"+pass);
			return false;
		}
		
		if(checkLoginStatus()){
			LogSys.nodeLogger.info("登陆成功with User"+user+" Password"+pass);
			return true;
		}else{
			LogSys.nodeLogger.info("登录失败with User"+user+" Password"+pass);
			return false;
		}
	}
		
	public boolean doLogin(){
		if(this.reLogin()){
			return true;			
		}
		return forceLogin();		
	}
	public String SaveToHtml(HttpEntity entity,String fileName){       
        try{
        	 BufferedReader br=new BufferedReader(new InputStreamReader(entity.getContent()));
             BufferedWriter bw=new BufferedWriter(new FileWriter(fileName));
        	String t="";
        	StringBuffer sb=new StringBuffer();
        	while((t=br.readLine())!=null){
        		bw.write(t+"\n\r");
        		sb.append(t+"\n\r");
        	}
        	bw.close();
        	br.close();
        	return sb.toString();
        }catch (Exception ex){
        	ex.printStackTrace();
        	return null;
        }
        
	}
	public String getToken(String html){
		Document doc=Jsoup.parse(html, "/");
		Elements elemets = doc.getElementsByAttributeValue("name","authenticity_token");
		String res=null;
		if(elemets.size()>0){
			Element ele=elemets.first();
			res=ele.attr("value");
		}		
		return res;
	}
	/*
	 * 是否恢复
	 */
	public boolean reLogin(){
		try{
			 MyCookieStore mycookiestore = new MyCookieStore();
			 mycookiestore.resume("Output/Twitter/TwitterLoginCookie.dat");
			 httpclient.setCookieStore(mycookiestore);
			 if(this.checkLoginStatus()){
				 LogSys.nodeLogger.info("恢复登录状态成功");
				 return true; 
			 }else{
				 LogSys.nodeLogger.info("恢复登录状态失败");
				 return false;
			 }
			 	
		}catch(Exception ex){
			LogSys.nodeLogger.info("恢复登录状态失败");
			ex.printStackTrace();
			return false;
		}		 
	}
	/*
	 * True:  IS  Login
	 * False: NOT Login 检查是否登录成功的函数存在很大的问题
	 */
	public boolean checkLoginStatus(){
		///%s/following/users?%sinclude_available_features=1&include_entities=1&is_forward=true
		HttpGet httpget = new HttpGet("https://twitter.com/networktest1/followers");
		try {
			HttpResponse response = httpclient.execute(httpget);
			StatusLine state =response.getStatusLine();
			int stateCode=state.getStatusCode();
			if(HttpStatus.SC_OK==stateCode){
				String res=SaveToHtml(response.getEntity(),"Output/Twitter/CheckLogin.html");
				if(res.contains("We gotta check... are you human?")||res.contains("Sign in to Twitter")||
								res.contains("<form action=\"https://twitter.com/sessions\"")
								){
					return false;
				}else{
					return true;
				}
			}else if(HttpStatus.SC_MOVED_PERMANENTLY == stateCode 
					|| HttpStatus.SC_MOVED_TEMPORARILY == stateCode
					|| HttpStatus.SC_SEE_OTHER == stateCode
					|| HttpStatus.SC_TEMPORARY_REDIRECT == stateCode){
				return false;
			}else{
				return false;
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return true;
	}
	
	private String[] getAccounts(){
		ReadTxtFile rtf = new ReadTxtFile(ACCOUNT_FILE_PATH);
		Vector<String> counts = rtf.read();
		for(int i=1;i<counts.size();i++){
			String item=counts.get(i);
			String fields[]=item.split(" ");
			if(fields[2].equalsIgnoreCase("YES")){
				return fields;
			}
		}		
		return null;		
	}

	
	private boolean enableAllCounts(){
		ReadTxtFile rtf = new ReadTxtFile(ACCOUNT_FILE_PATH);
		Vector<String> counts = rtf.read();
		for(int i=0;i<counts.size();i++){
			String item=counts.get(i);
			item=item.replaceAll("NO", "YES");
			counts.set(i, item);
		}
		SaveTxtFile stf = new SaveTxtFile(ACCOUNT_FILE_PATH,false);
		for(String t:counts){
			stf.Append(t+"\r\n");
		}
		stf.close();
		return true;	
	}
	private boolean disableCountsByName(String count){
		ReadTxtFile rtf = new ReadTxtFile(ACCOUNT_FILE_PATH);
		Vector<String> counts = rtf.read();
		for(int i=0;i<counts.size();i++){
			String item=counts.get(i);
			if(item.startsWith(count)&&item.endsWith("YES")){
				item=item.replaceAll("YES", "NO");
				counts.set(i, item);
			}
		}
		SaveTxtFile stf = new SaveTxtFile(ACCOUNT_FILE_PATH,false);
		for(String t:counts){
			stf.Append(t+"\r\n");
		}
		stf.close();
		return true;
	}
	
	

		
}
	

