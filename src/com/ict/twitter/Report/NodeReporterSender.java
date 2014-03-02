package com.ict.twitter.Report;

import com.ict.twitter.MessageBus.MessageBussConnector;
import com.ict.twitter.MessageBus.Sender;

public class NodeReporterSender extends Sender{

	public NodeReporterSender(javax.jms.Connection connection,String queue, boolean isTopic) {
		super(connection,queue, isTopic);
	}

}
