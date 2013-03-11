package com.ict.twitter.MessageBusTest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.ict.twitter.MessageBus.MessageBusNames;
import com.ict.twitter.MessageBus.MessageBussConnector;
import com.ict.twitter.plantform.LogSys;

public class MessageBusCleanner implements MessageBusNames,MessageBussConnector{

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public static void CleanTaskBus(){
		try{
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory( 
	                ActiveMQConnection.DEFAULT_USER, 
	                ActiveMQConnection.DEFAULT_PASSWORD, 
	                address); 			
			Connection connection = connectionFactory.createConnection();
			Session session = null;
			connection.start(); 
			session = connection.createSession(true, Session.CLIENT_ACKNOWLEDGE);
		    Destination destination= session.createQueue(MessageBusNames.Task);
		    MessageConsumer consumer = session.createConsumer(destination);
			Message tmp=null;
			while((tmp=consumer.receive(50))!=null){
				System.out.println(tmp.toString());
				tmp.acknowledge();
				session.commit();
			}
			consumer.close();
				
					
			LogSys.clientLogger.info("����ɡ�������еĶ�����Ϣ");
			session.close();
			connection.close();
			
		}catch(JMSException ex){
			ex.printStackTrace();
			LogSys.clientLogger.error("�����Ϣ����ʧ��~");
			LogSys.clientLogger.error(ex);
		}
	}
	public static void Clean(){
		try{
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory( 
	                ActiveMQConnection.DEFAULT_USER, 
	                ActiveMQConnection.DEFAULT_PASSWORD, 
	                address); 			
			Connection connection = connectionFactory.createConnection();
			Session session = null;
			connection.start(); 
			session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
		    for(String t:names){				
			        // Session�� һ�����ͻ������Ϣ���߳� 
			    Destination destination= session.createQueue(t);
			    MessageConsumer consumer = session.createConsumer(destination);
				Message tmp=null;
				while((tmp=consumer.receive(50))!=null){
					tmp.acknowledge();
				}
				consumer.close();
				
			}		
			LogSys.clientLogger.info("����ɡ�������еĶ�����Ϣ");
			session.close();
			connection.close();
			
		}catch(JMSException ex){
			ex.printStackTrace();
			LogSys.clientLogger.error("�����Ϣ����ʧ��~");
			LogSys.clientLogger.error(ex);
		}
	}

}
