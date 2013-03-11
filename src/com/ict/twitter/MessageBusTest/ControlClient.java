package com.ict.twitter.MessageBusTest;


import javax.jms.Connection;

import com.ict.twitter.MessageBus.ConsoleMsgBusNames;

import com.ict.twitter.MessageBus.GetAceiveMqConnection;
import com.ict.twitter.MessageBus.Receiver;
import com.ict.twitter.MessageBus.Sender;
import com.ict.twitter.plantform.LogSys;

public abstract class ControlClient implements Runnable, ConsoleMsgBusNames,MessagebusNode{
	ClientsID Name;
	WorkState currentWs;
	Receiver controlrecever;
	Sender controlSender;
	Sender crawlInfoSender;
	NodeControlMessageProcess ncmp;
	NodeCondition mycondition;
	public boolean isRunning=true;
	Connection con=null;
	public ControlClient(ClientsID name){
		this.Name=name;
		this.currentWs=WorkState.stop;
		ncmp=new NodeControlMessageProcess(this);
		mycondition=new NodeCondition(this.Name);
	
		
		
	}
	public void Initiallize(){
		Connection con=GetAceiveMqConnection.StaticGetConnection();
		controlrecever=new Receiver(con,true,ConsoleMsgBusNames.ControlS2C,null,false,ncmp);
		controlSender=new Sender(con,ConsoleMsgBusNames.ControlC2S,false);
		crawlInfoSender=new Sender(con,ConsoleMsgBusNames.CrawlInfoC2S,false);
	}

	public void run() {
		// TODO Auto-generated method stub
		while(isRunning){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception e){
				LogSys.clientLogger.error("ControlClient Exit~~~");
				e.printStackTrace();
			}			
		}		
		finalize();
	}
	public void pause(int count){
		try {
			Thread.sleep(count);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	protected void finalize(){
		controlrecever.Exit();
		controlSender.Exit();
		crawlInfoSender.Exit();
		System.out.println("Client is Closed");
		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void NodeSendStartMessage(){
		ControlCommandMessage controlMsg=new ControlCommandMessage();
		controlMsg.nodeID=Name;
		controlMsg.targetName=Name.toString();
		controlMsg.workState=WorkState.start;
		controlSender.Send(controlMsg);
		
	}
	public void NodeSendStopMessage(){
		ControlCommandMessage controlMsg=new ControlCommandMessage();
		controlMsg.nodeID=Name;
		controlMsg.targetName=Name.toString();
		controlMsg.workState=WorkState.stop;
		controlSender.Send(controlMsg);
	}
	
	//启动子节点的采集程序。

	public  abstract boolean ToStart();	
	public  abstract boolean ToPause();
	public  abstract boolean ToResume();
	
	public abstract boolean ToStop();
	public abstract boolean ToRestart();
	
	public abstract boolean ToDisplay();
	//向服务器报告当前状态
	public boolean ToShow(ControlCommandMessage lastMsg){
		ControlCommandMessage controlMsg=new ControlCommandMessage();
		controlMsg.nodeID=Name;
		controlMsg.targetName=Name.toString();
		controlMsg.workState=currentWs;
		controlMsg.lastMs=lastMsg;
		controlMsg.information=currentWs.toString();
		controlSender.Send(controlMsg);
		return true;
	}
	public void NodeSendFailToExecuteCommand(ControlCommandMessage ms){
		ControlCommandMessage controlMsg=new ControlCommandMessage();
		controlMsg.nodeID=Name;
		controlMsg.targetName=Name.toString();
		controlMsg.workState=WorkState.error;
		controlMsg.lastMs=ms;
		controlMsg.information="客户端执行命令"+ms+"失败";		
		controlSender.Send(controlMsg);
	}
	public boolean ToReport(boolean isIncremental,int message,int messageRelationship,int user,int userRelationship){
		CrawlerMessage cm=new CrawlerMessage();
		cm.nodeID=this.Name;
		if(isIncremental){
			cm.Incremental=true;
			cm.MessageCount=message;
			cm.MessageRelationShipCount=messageRelationship;
			cm.UserCount=user;
			cm.UserRelationShipCount=userRelationship;			
		}else{
			cm.Incremental=false;
			cm.MessageCount=message;
			cm.MessageRelationShipCount=messageRelationship;
			cm.UserCount=user;
			cm.UserRelationShipCount=userRelationship;	
		}
		crawlInfoSender.Send(cm);
		return true;
	}
	
}
