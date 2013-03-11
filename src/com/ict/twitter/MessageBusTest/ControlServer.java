package com.ict.twitter.MessageBusTest;

import java.util.Enumeration;
import java.util.Hashtable;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.ict.twitter.MessageBus.ConsoleMsgBusNames;
import com.ict.twitter.MessageBus.GetAceiveMqConnection;
import com.ict.twitter.MessageBus.Receiver;
import com.ict.twitter.MessageBus.Sender;

public class ControlServer implements Runnable,ConsoleMsgBusNames,MessageListener,MessagebusNode{
	public String Name="Server";
	Receiver controlRecever;
	Receiver crawlInfoRecever;
	Sender mysender;
	ServerControlMessageProcess scmp;
	ServerCrawlerMessageProcess sCrawlerMessageProcess;
	public static Log log;
	public boolean isRunning=true;
	public Hashtable<ClientsID,NodeCondition> allNodeCondition;
	public Hashtable<ClientsID,ControlClient> clients;
	public String status;
	public ControlServer(){
		PropertyConfigurator.configure ("config/log4j_Main.properties" ) ;
		log= LogFactory.getLog(ControlServer.class);
		allNodeCondition=new Hashtable<ClientsID,NodeCondition>(4);
		for(int i=1;i<ClientsID.values().length-1;i++){
			NodeCondition node=new NodeCondition(ClientsID.values()[i]);
			allNodeCondition.put(ClientsID.values()[i], node);
			status="ControlServer节点"+ClientsID.values()[i]+"启动完成";
			log.info(status);
		}
		log.info("ControlServer 初始化完成");
		clients=new Hashtable<ClientsID,ControlClient>(allNodeCondition.size());
		
	}
	public void stop(){
		isRunning=false;
	}
	//启动server的时候启动clients
	public void run() {
		// TODO Auto-generated method stub
		javax.jms.Connection connection=GetAceiveMqConnection.StaticGetConnection();
		scmp=new ServerControlMessageProcess(this);
		sCrawlerMessageProcess=new ServerCrawlerMessageProcess(this);
		mysender=new Sender(connection,ControlS2C,true);
		controlRecever=new Receiver(connection,false,ControlC2S,null,false,scmp);
		
		crawlInfoRecever=new Receiver(connection,false,CrawlInfoC2S,null,false,sCrawlerMessageProcess);
		
		if(startControlClient()){
			status="INIT＿Ok";
		}		
		while(isRunning){
			try {
				Thread.sleep(20000*100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		finalize();
		System.out.println("Control Server And Clients is Stopped........5");
		System.out.println("Control Server And Clients is Stopped........4");
		System.out.println("Control Server And Clients is Stopped........3");
		System.out.println("Control Server And Clients is Stopped........2");
		System.out.println("Control Server And Clients is Stopped........1");
		System.exit(-1);

		
			
	}
	
	
	//启动controlClient;
	public boolean startControlClient(){
		Enumeration<ClientsID> allKeys=allNodeCondition.keys();
		while(allKeys.hasMoreElements()){
			ClientsID current=allKeys.nextElement();
			ControlClient client=null;
			if(current==ClientsID.TwitterWEB){
				//client=new TwitterWEBControlClient(current);
			}else if(current ==  ClientsID.FacebookWEB){
				client=new FacebookWEBControlClient(current);
			}else{
				//client=new ControlClient(current);
				
			}
			//把节点加入到管理队列中
			if(client!=null){
				client.Initiallize();
				clients.put(current, client);
			}						
		};
		return true;
	}
	
	
	@Override
	public void onMessage(Message arg0) {
		if(arg0 instanceof TextMessage){
			try {
				System.out.println("[Name: "+Name+"]:"+((TextMessage) arg0).getText());
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	protected void finalize(){
		mysender.Exit();
		controlRecever.Exit();
		crawlInfoRecever.Exit();
		System.out.println("Server is Closed");
		try {
			super.finalize();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void SendCommandToAllClinet(WorkState ws){
		ControlCommandMessage controlMsg=new ControlCommandMessage();
		controlMsg.nodeID=ClientsID.ALL;
		controlMsg.targetName=ClientsID.ALL.toString();
		controlMsg.workState=ws;
		mysender.Send(controlMsg);	
	}
	
	public void SendCommandToAllClientToStartUp(){
		SendCommandToAllClinet(WorkState.start);
		System.out.println("ControlServer Send Start Command");
	}
	public void SendCommandToAllClientToPause(){
		SendCommandToAllClinet(WorkState.pause);
		System.out.println("ControlServer Send Pause Command");
	}
	public void SendCommandToAllClientToResume(){
		SendCommandToAllClinet(WorkState.resume);
		System.out.println("ControlServer Send Resume Command");
		
	}
	public void SendCommandToAllClientToStop(){
		SendCommandToAllClinet(WorkState.stop);
		System.out.println("ControlServer Send Stop Command");
	}
	public void SendCommandToAllClientToRestart(){
		SendCommandToAllClinet(WorkState.toRestart);
		System.out.println("ControlServer Send toRestart Command");
	}
	
	public void SendComandToAllClientToDisplayNodesStatus(){
		SendCommandToAllClinet(WorkState.display);
	}
	
	public void receiveStarted(ClientsID node){
		allNodeCondition.get(node).ws=WorkState.start;
	}
	public void setState(ClientsID node,WorkState ws){
		allNodeCondition.get(node).ws=ws;
	}
}
