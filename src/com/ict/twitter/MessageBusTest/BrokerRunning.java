package com.ict.twitter.MessageBusTest;

import org.apache.activemq.broker.BrokerService;

public class BrokerRunning {
	public static void main(String[] args){
		BrokerService broker = new BrokerService();
		try {
			broker.addConnector("tcp://localhost:61616");
			broker.start();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
