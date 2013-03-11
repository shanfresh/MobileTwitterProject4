package com.ict.twitter.MessageBusTest;

import javax.jms.Message;

public class ServerControlMessageProcess extends ControlMessageProcess{
	ControlServer myserver;
	public ServerControlMessageProcess(MessagebusNode _server){
		myserver=(ControlServer)_server;		
	}
	public void onMessage(Message msg) {
		super.onMessage(msg);
		//如果不是回显状态则更新状态信息
		if(commandMessage.workState!=WorkState.show){
			myserver.setState(commandMessage.nodeID, commandMessage.workState);
		}
		
		if(commandMessage.lastMs!=null&&commandMessage.lastMs.workState==WorkState.show){
			ControlServer.log.debug(commandMessage.nodeID+"current state_________：["+commandMessage.workState+"]");
			return;
		}
		
		
		if(commandMessage.workState==WorkState.start){
			ControlServer.log.debug(commandMessage.nodeID+"is started");			
			return;
		}		
	}

}
