package com.ict.twitter.MessageBus;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQSession;
import com.ict.twitter.plantform.LogSys;

public class Sender{
	public Session session;
	public Destination destination;
	public MessageProducer producer;	
	String currentQueue;
	boolean iSTopic;
	private ActiveMQConnection con;
	//传入Connection的Sender构造方法。
	public Sender(Connection connection,String queue,boolean isTopic){
		init(connection,queue,isTopic);
		
	}
	@Deprecated
	public Sender(String queue,boolean isTopic){
		Connection con=GetAceiveMqConnection.StaticGetConnection();
		init(con,queue,isTopic);
	}
	
	public void  init(Connection connection,String queue,boolean isTopic){
		this.currentQueue=queue;
		this.iSTopic=isTopic;
		try{
			con=(ActiveMQConnection)connection;
			session = con.createSession(Boolean.FALSE, Session.AUTO_ACKNOWLEDGE);			
            if(isTopic){
            	destination= session.createTopic(queue); 
            	
            }else
            	destination= session.createQueue(queue); 			
			producer=session.createProducer(destination); 
			producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
			connection.start(); 
			
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
	
	//check the connection state
	private boolean checkConnectable(){
		ActiveMQSession mySession=(ActiveMQSession)session;
		return mySession.isClosed();

	}
	public boolean Send(String t){	
		while(checkConnectable()){
			System.err.println("session 检查连接失败1s后重试");
			init(con,currentQueue,iSTopic);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		if( (session!=null)&&(producer!=null)){			
			TextMessage message;
			try {				
				message = session.createTextMessage(t);
				producer.send(message);
			} catch (JMSException e) {
				// TODO Auto-generated catch block				
				e.printStackTrace();				
			}
		}else{
			System.out.println("服务器连接失败");
			return false;
		}		
		return true;
	}
	public boolean Send(Object object){
		if(!(object instanceof Serializable)){
			System.err.println("object is not Serializable");
			return false;
		} 
		while(checkConnectable()){
			System.err.println("session 检查连接失败1s后重试");
			init(con,currentQueue,iSTopic);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
		if( (session!=null)&&(producer!=null)){			
			ObjectMessage message;
			try {				
				message = session.createObjectMessage((Serializable) object);				
				producer.send(message);				
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				LogSys.clientLogger.error("JMS发送失败~");
				LogSys.clientLogger.error(e);
				e.printStackTrace();
			} 
             //通过消息生产者发出消息 
		}else{
			System.out.println("服务器连接失败");
			return false;
		}
		
		
		return true;
	}

}
