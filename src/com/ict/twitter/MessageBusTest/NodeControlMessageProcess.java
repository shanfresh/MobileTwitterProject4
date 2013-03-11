package com.ict.twitter.MessageBusTest;

import javax.jms.Message;

public class NodeControlMessageProcess extends ControlMessageProcess {
	ControlClient client;
	public NodeControlMessageProcess(MessagebusNode _client){
		client=(ControlClient)_client;		
	}
	public void onMessage(Message msg) {
		super.onMessage(msg);
		boolean isFail=false;
		if(commandMessage.workState==WorkState.start){
			ControlServer.log.info(client.Name+"receive Startup command");
			System.err.println("收到了启动信息"+client.Name+"receive Startup command");
			if(client.ToStart()){
				client.currentWs=WorkState.start;
				return;
			}else{
				isFail=true;
			}
		}else if(commandMessage.workState==WorkState.pause){
			ControlServer.log.info(client.Name+"  receive pause command");
			if(client.ToPause()){
				client.currentWs=WorkState.pause;
				return;
			}else{
				isFail=true;
			}
			
		}else if(commandMessage.workState==WorkState.resume){
			ControlServer.log.info(client.Name+"receive resuming command");
			if(client.ToResume()){
				client.currentWs=WorkState.start;
				return;
			}else{
				client.currentWs=WorkState.error;
				isFail=true;
			}
		}else if(commandMessage.workState==WorkState.stop){
			ControlServer.log.info(client.Name+"receive stop command");
			if(client.ToStop()){
				client.currentWs=WorkState.stop;
				return;
			}else{
				
				isFail=true;
			}
			
		}else if(commandMessage.workState==WorkState.show){
			ControlServer.log.info(client.Name+"receive show command");
			if(client.ToShow(commandMessage)){
				
			}else{
				
				isFail=true;
			}
			
		}else if(commandMessage.workState==WorkState.toRestart){
			ControlServer.log.info(client.Name+"receive Restart command");
			if(client.ToRestart()){
				
			}else{
				
				isFail=true;
			}
			
		}
		else if(commandMessage.workState==WorkState.display){
			ControlServer.log.info(client.Name+"receive display command");
			if(client.ToDisplay()){
				
			}else{
				
				isFail=true;
			}
			
		}
		if(isFail){
			client.NodeSendFailToExecuteCommand(commandMessage);
			isFail=false;
			ControlServer.log.info(client.Name+"执行命令失败"+commandMessage.workState);
		}
		
	}
}
