package com.ict.twitter.MessageBus;



import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class GetAceiveMqConnection implements MessageBussConnector{
	public static ActiveMQConnection StaticGetConnection(){
		try{
			ActiveMQConnection connection=null;
			ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory( 
	                ActiveMQConnection.DEFAULT_USER, 
	                ActiveMQConnection.DEFAULT_PASSWORD, 
	                address); 			
				connection = (ActiveMQConnection)connectionFactory.createConnection();
				connection.start();
				return connection;
		}catch(Exception ex){
			System.out.println("GetConnection Error");
			ex.printStackTrace();
			System.exit(-1);
			return null;
		}
		
	}

}
