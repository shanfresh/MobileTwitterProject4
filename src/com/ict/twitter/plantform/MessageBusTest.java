package com.ict.twitter.plantform;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import com.ict.twitter.MessageBus.MessageBussConnector;

public class MessageBusTest implements MessageBussConnector {

	/**
	 * @param args
	 */
	
	public static void main(String[] args) {
		

	}
	public boolean doTest(){
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory( 
                ActiveMQConnection.DEFAULT_USER, 
                ActiveMQConnection.DEFAULT_PASSWORD, 
                address); 
		Connection connection;
		try {
			connection= connectionFactory.createConnection();
			connection.start();
			if(connection!=null){
				connection.stop();
				connection.close();
			}
		}catch (Exception ex){
			ex.printStackTrace();
			return false;
		}
		return true;
	}
	

}
