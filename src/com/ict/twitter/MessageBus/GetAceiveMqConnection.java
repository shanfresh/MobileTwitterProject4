package com.ict.twitter.MessageBus;



import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class GetAceiveMqConnection extends MessageBussConnector{
	static{
		System.out.println("ActiveMQ的工厂类");
		Properties pro=new java.util.Properties();
		try {
			pro.load(new FileInputStream("config/clientproperties.ini"));
			String connectString=(String) pro.get("activemq.connectString");
			System.out.println("ActiveMQAddress="+connectString);
			MessageBussConnector.address=connectString;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
