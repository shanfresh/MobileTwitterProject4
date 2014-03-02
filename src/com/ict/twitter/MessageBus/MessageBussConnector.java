package com.ict.twitter.MessageBus;

public abstract class MessageBussConnector {
	//public static final String address="failover:(tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=10000,maxReconnectDelay=10000)";
	public static String address="failover:(tcp://127.0.0.1:61616?keepAlive=true&soTimeout=0&wireFormat.maxInactivityDuration=0)";

}
