package com.ict.twitter.MessageBusTest;

import javax.jms.Message;

public class ServerCrawlerMessageProcess extends CrawlerMessageProcess {
	ControlServer myserver;
	public ServerCrawlerMessageProcess(MessagebusNode _server){
		myserver=(ControlServer)_server;		
	}
	public void onMessage(Message arg0){
		super.onMessage(arg0);
		NodeCondition nodeCondition=myserver.allNodeCondition.get(crawlerMessage.nodeID);
		System.err.println("收到采集节点的汇报信息");
		myserver.log.info(crawlerMessage.show());
		if(crawlerMessage.Incremental){			
			nodeCondition.MessageCount+=crawlerMessage.MessageCount;
			nodeCondition.MessageRelationShipCount+=crawlerMessage.MessageRelationShipCount;
			nodeCondition.UserCount+=crawlerMessage.UserCount;
			nodeCondition.UserRelationShipCount+=crawlerMessage.UserRelationShipCount;
			nodeCondition.show();
		}else{
			nodeCondition.MessageCount=crawlerMessage.MessageCount;
			nodeCondition.MessageRelationShipCount=crawlerMessage.MessageRelationShipCount;
			nodeCondition.UserCount=crawlerMessage.UserCount;
			nodeCondition.UserRelationShipCount=crawlerMessage.UserRelationShipCount;
			nodeCondition.show();
		}
		
		
		
	}

}
