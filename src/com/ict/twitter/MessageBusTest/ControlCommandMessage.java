package com.ict.twitter.MessageBusTest;

import java.io.Serializable;

import javax.jms.Message;

public class ControlCommandMessage extends MQMessage implements Serializable{
	private static final long serialVersionUID = -6921785210001103254L;
	ClientsID nodeID;
	String targetName;
	WorkState workState;
	String information;
	ControlCommandMessage lastMs;
	public Message getMessage(){				
		return messageOutput;		
	}

}
