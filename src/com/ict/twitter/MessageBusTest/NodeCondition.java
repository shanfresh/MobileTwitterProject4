package com.ict.twitter.MessageBusTest;

public class NodeCondition {
	ClientsID name;
	WorkState ws;
	int MessageCount;
	int MessageRelationShipCount;
	int UserCount;
	int UserRelationShipCount;
	long startUptime;
	public NodeCondition(ClientsID _name){
		this.name=_name;
	}
	public void show(){		
		ControlServer.log.info(name+"---"+"messageCount:"+MessageCount+" MessageRelationShipCount:"+MessageRelationShipCount+" UserCount:"+UserCount+" UserRelationShip"+UserRelationShipCount);
	}
}
