package com.ict.twitter.CrawlerNode;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.PropertyConfigurator;

import com.ict.facebook.LoginManager;
import com.ict.facebook.WebOp;

import com.ict.facebook.CrawlerNode.FBAjaxNode;

import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.DbOperation;

public class runFBAjaxNodes {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		run();
	}


	public static boolean run(){
		DbOperation dbOper=new DbOperation();
		com.ict.facebook.ClientManager FacebookCm = new com.ict.facebook.ClientManager();
		DefaultHttpClient httpclient=null;		
		try{
			PropertyConfigurator.configure ("config/log4j_Main.properties" ) ;
			Properties pro=new Properties();
			pro.load(new FileInputStream("config/FB_clientproperties.ini"));
			
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
			WebOp.Init();
			
			String name="FBAjaxNode";
			int nodecount=Integer.parseInt(pro.getProperty("node.nodecount"));
			for(int i=0;i<nodecount;i++){				
				String nodeName=String.format("%s-%2d", name,i);
				FBAjaxNode node=new FBAjaxNode(nodeName,dbOper);
				Thread thread=new Thread(node);
				thread.setName("nodeName");
				thread.start();	
			}
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
}
