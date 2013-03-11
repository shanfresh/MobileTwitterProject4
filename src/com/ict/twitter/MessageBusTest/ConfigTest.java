package com.ict.twitter.MessageBusTest;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;

import com.ict.twitter.MessageBus.GetAceiveMqConnection;

public class ConfigTest {

	/**
	 * @param args
	 */
	public int count=0;
	public static void main(String[] args) {
		ActiveMQConnection con = GetAceiveMqConnection.StaticGetConnection();
		ConfigTest test= new ConfigTest();
		//test.Sender(con);
		
		test.Receiver(con);
		//test.Receiver(con);
	}
	public void Sender(ActiveMQConnection con){
		try {
			Session session=con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("com.foo?consumer.prefetchSize=1");
			MessageProducer pro =session.createProducer(queue);
			TextMessage tx=session.createTextMessage("~~~");
			
			for(int i=0;i<100000;i++){
				pro.send(tx);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				System.out.println("Sending index:"+i);
			}
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void Receiver(ActiveMQConnection con){
		try {
			Session session=con.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue = session.createQueue("com.foo?consumer.prefetchSize=1");
			MessageConsumer consumer =session.createConsumer(queue);
			TextMessage tx=session.createTextMessage("~~~");
			Message msg;
			while((msg=consumer.receive(1000))!=null){
				System.out.println(count++);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}			
			
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
