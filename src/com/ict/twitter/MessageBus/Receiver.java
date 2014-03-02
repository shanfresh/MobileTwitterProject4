package com.ict.twitter.MessageBus;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import com.ict.twitter.CrawlerMessage.MessageBusComponent;
import com.ict.twitter.CrawlerNode.Node;
import com.ict.twitter.CrawlerServer.CrawlerServer;
import com.ict.twitter.plantform.LogSys;

public class Receiver implements MessageListener{
	public Session session;
	protected Destination destination ;
	public MessageConsumer consumer;
	public  Node node=null;
	public CrawlerServer server=null;
	public boolean isNode;
	public String ReceiverName;
	
	public Receiver(){
	
	}
	public Receiver(Connection con,
					boolean isTopic,
					String BusName,
					MessageBusComponent comp,
					boolean isActive,
					MessageListener msg)
	{
		if(con==null){
			LogSys.debugLogger.error("传入的connection为NULL");
			con=GetAceiveMqConnection.StaticGetConnection();
		}
		if(comp instanceof Node){
			node=(Node)comp;
			isNode=true;
		}else{
			server=(CrawlerServer)comp;
			isNode=false;
		}
		init(con,BusName,isTopic,msg,isActive);		
	}


	public void init(Connection connection,String queue,boolean isTopic,MessageListener mlisten,boolean isActive){
		try {
			// Session： 一个发送或接收消息的线程 
	        session= connection.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);
	        if(isTopic){
	        	destination=session.createTopic(queue);
	        }else{
	        	destination = session.createQueue(queue);		        
	        }
	        
	        consumer= session.createConsumer(destination);
	        if(isActive){
	        	System.out.println(queue);
	        	LogSys.nodeLogger.info("创建一个主动方式获取消息的Receiver:"+queue);  	
	        }else{
	        	if(mlisten==null)
	        		consumer.setMessageListener(this);
	        	else
	        		consumer.setMessageListener(mlisten);
	        }	        
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	public void Exit(){
		try {			
			session.close();			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public String Receive(){
		try{
	        Message msg = consumer.receive(1000);
	        if(msg instanceof TextMessage){
	        	TextMessage txt = (TextMessage)msg;
	        	return txt.getText();
	        }
	        else
	        	return null;
		}catch (Exception e){
			e.printStackTrace();
			return null;
		}		
	}
	public Message TryToReceive(){
		return null;
	}
	
	@Override
	public void onMessage(Message resmsg) {
		String t="";
		try {
			TextMessage txm=(TextMessage)resmsg;
			t=txm.getText();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(isNode){
			//System.out.println("【NODE】接收到消息"+resmsg.toString());
			System.out.println("["+this.ReceiverName+"]"+"文本消息"+t);
		}else{
			//System.out.println("【Server】接收到消息"+resmsg.toString());
			System.out.println("文本消息"+t);
		}
		

	}
	public void commitMessage(){
		try {
			
			session.commit();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void cancelMessage(){
		try {
			session.rollback();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void startSession(){

	}

	public Node getNode() {
		return node;
	}

	public void setNode(Node node) {
		this.node = node;
	}
	

}
