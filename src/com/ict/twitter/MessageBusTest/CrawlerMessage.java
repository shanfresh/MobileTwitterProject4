package com.ict.twitter.MessageBusTest;

import java.io.Serializable;

public class CrawlerMessage extends MQMessage implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2487649622521088784L;
	ClientsID nodeID;
	long MessageID=0;
	boolean Incremental=true;
	String reportTime;
	int MessageCount;
	int MessageRelationShipCount;
	int UserCount;
	int UserRelationShipCount;
	public String show(){
		StringBuffer sb=new StringBuffer();
		sb.append("【ControlServer】收到客户端汇报");
		sb.append(nodeID);
		sb.append(" ");
		sb.append(MessageID);
		sb.append(" ");
		sb.append(Incremental);
		sb.append(" ");
		sb.append(reportTime);
		sb.append(" ");
		sb.append(MessageCount);
		sb.append(" ");
		sb.append(MessageRelationShipCount);
		sb.append(" ");
		sb.append(UserCount);
		sb.append(" ");
		sb.append(UserRelationShipCount);
		return sb.toString();
		
	}
}
