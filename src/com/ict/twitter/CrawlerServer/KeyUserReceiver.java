package com.ict.twitter.CrawlerServer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.ict.twitter.MessageBus.*;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.SimpleXmlAnalyser;

public class KeyUserReceiver extends Receiver {

	public KeyUserReceiver(javax.jms.Connection connection ,String queue, CrawlerServer _server, boolean isTopic) {
		super(connection,isTopic,queue, _server,false,null);
	}

	public void onMessage(Message resmsg) {
		if (isNode) {
			System.err.println("Node 不可能接受KeyUser");
			System.exit(1);
		} else {
			if (resmsg instanceof TextMessage) {			
				try {
					String str = ((TextMessage) resmsg).getText();				
					if(str==null||str.equals("")){
						return;
					}else{
						LogSys.crawlerServLogger.debug("【Server】接收KeyUser" + str);
					}
					SimpleXmlAnalyser simxml = new SimpleXmlAnalyser(str);
					String[] users = simxml.getValueByTag("name");					

					for (int i = 0; i < users.length; i++) {
						server.addKeyUser(new NormalUser(users[i], Integer.parseInt("1000")));
					}

				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}catch(Exception e){
					e.printStackTrace();
				}
			}//END::if (resmsg instanceof TextMessage) {
			
		}

	}

}
