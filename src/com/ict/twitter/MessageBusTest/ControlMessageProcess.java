package com.ict.twitter.MessageBusTest;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

public class ControlMessageProcess extends MessageProcess {
	ControlCommandMessage commandMessage;
	@Override
	public void onMessage(Message arg0) {
		try {
			if(arg0 instanceof ObjectMessage){
				commandMessage=(ControlCommandMessage)((ObjectMessage)arg0).getObject();				
			}else if(arg0 instanceof TextMessage){
				ControlServer.log.error("arg0 is not ObjectMessage");
				ControlServer.log.error(((TextMessage)arg0).getText());
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
