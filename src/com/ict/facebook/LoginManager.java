package com.ict.facebook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

public class LoginManager {

	/**
	 * @param args
	 */
	String user,pass;
	DefaultHttpClient httpclient;
	public LoginManager(DefaultHttpClient _httpclient){
		httpclient=_httpclient;
		user="networktest1@126.com";
		pass="290749970abc";
	}
	public static void main(String[] args){
		ClientManager cm=new ClientManager();
		
		LoginManager lm=new LoginManager(cm.getClient("192.168.120.67", 8087));
		lm.doLogin();
	}
	
	public boolean loadCookieFromFile(){
		return true;
	}
	
	
	public boolean doLogin(){
		try{
			HttpGet httpget = new HttpGet("https://www.facebook.com/");
			HttpResponse response = httpclient.execute(httpget);
	        HttpEntity entity = response.getEntity();
	
	        System.out.println("Login form get: " + response.getStatusLine());
	        EntityUtils.consume(entity);
	
	        System.out.println("Initial set of cookies:");
	        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
	        if (cookies.isEmpty()) {
	            System.out.println("None");
	        } else {
	            for (int i = 0; i < cookies.size(); i++) {
	                System.out.println("- " + cookies.get(i).toString());
	            }
	        }
	        
	        HttpPost httpost = new HttpPost("https://www.facebook.com/login.php?login_attempt=1");
            List <NameValuePair> nvps = new ArrayList <NameValuePair>();
            nvps.add(new BasicNameValuePair("email", user));
            nvps.add(new BasicNameValuePair("pass", pass));          
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));

            response = httpclient.execute(httpost);
            entity = response.getEntity();
            EntityUtils.consume(entity);
            System.out.println("Post logon cookies:");
            cookies = httpclient.getCookieStore().getCookies();
            if (cookies.isEmpty()) {
                System.out.println("None");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    System.out.println("- " + cookies.get(i).toString());
                }
            }

		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
		return true;
		
		
	}
		
}
	

