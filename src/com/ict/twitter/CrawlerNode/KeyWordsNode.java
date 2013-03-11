package com.ict.twitter.CrawlerNode;

import javax.jms.Connection;
import com.ict.twitter.MessageBus.MessageBusNames;
import com.ict.twitter.MessageBus.ObjReceiver;
import com.ict.twitter.tools.DbOperation;

public class KeyWordsNode extends Node{

	//…Ë÷√Node√˚≥∆NODE_WORD_1,2,3,4,5,6,7,8,9,10;
	private Connection Taskconnection;
	public KeyWordsNode(String name,DbOperation dbOper) {
		super(name,dbOper);
	}
	@Override
	public void InitTaskReceiver(){
		taskReceiver=new ObjReceiver(Taskconnection,MessageBusNames.Task,this);
		
	}
	private void getTask(int size){
		for(int i=0;i<size;i++){
			taskReceiver.TryToReceive();
			
		}
		
		
	} 
	
	

	

}
