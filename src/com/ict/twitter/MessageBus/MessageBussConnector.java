package com.ict.twitter.MessageBus;

public interface MessageBussConnector {
	//public static final String address="failover:(tcp://127.0.0.1:61616?wireFormat.maxInactivityDuration=10000,maxReconnectDelay=10000)";
	public static final String address="failover:(tcp://localhost:61616?keepAlive=true&soTimeout=0&wireFormat.maxInactivityDuration=0)";
}
