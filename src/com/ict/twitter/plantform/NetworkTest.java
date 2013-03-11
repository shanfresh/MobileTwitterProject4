package com.ict.twitter.plantform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;

import com.ict.twitter.*;
public class NetworkTest {
	public NetworkTest(){
		
	}
	public boolean[] TestNetwork(){
		
		Properties pro = new Properties();
		try {
			pro.load(new FileInputStream(new File("config/clientproperties.ini")));
			boolean ipv4=Boolean.parseBoolean(pro.getProperty("http.ipv4"));
			boolean ipv6=Boolean.parseBoolean(pro.getProperty("http.ipv6"));			
			boolean[] res={false,false};
			if(ipv4){
				boolean ipv4OK=TestIpv4(pro.getProperty("http.proxyHost"),
						 Integer.parseInt(pro.getProperty("http.proxyPort"))
						);
				res[0]=ipv4OK;	
			}
			if(ipv6){
				boolean ipv6OK=TestNetworkByIpv6();
				res[1]=ipv6OK;
			}
						
			//pro.store(new FileOutputStream(new File("config/clientproperties.ini")), "");
			return res;
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block			
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}
	
	private boolean TestIpv4(String ip,int port){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient client=cm.getClientByIpAndPort(ip, port);
		HttpHost targetHost = new HttpHost("mobile.twitter.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		HttpGet httpget=new HttpGet("/");
		try {
			client.execute(targetHost,httpget,localcontext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}catch (Exception ex){
			return false;
		}
		return true;
		
	}
	
	public boolean TestNetworkByIpv6(){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient client=cm.getClientNoProxy();
		HttpHost targetHost = new HttpHost("mobile.twitter.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		HttpGet httpget=new HttpGet("/");
		try {
			client.execute(targetHost,httpget,localcontext);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			return false;
		}catch (Exception ex){
			return false;
		}
		return true;
	}
	
}
