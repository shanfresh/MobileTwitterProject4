package com.ict.twitter.MessageBusTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;

public class CrawlerMessageProcess extends MessageProcess{
	CrawlerMessage crawlerMessage;
	public void onMessage(Message arg0){
		if(arg0 instanceof ObjectMessage){
			try {
				crawlerMessage=(CrawlerMessage)((ObjectMessage)arg0).getObject();

				
				
				
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			System.err.println(arg0.toString());
		}	
		
	}
}
