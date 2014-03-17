package com.ict.twitter.CrawlerNode;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;

import com.ict.twitter.MessageBus.Receiver;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.MainType;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.SimpleXmlAnalyser;

public class TaskReceiver extends Receiver {

	public static int TaskSize=0;
	public TaskReceiver(Connection con,String queue, Node _node) {
		super(con,false,queue,_node,true,null);
	}
	
	public Task PickUpTaskMessage(){
			Message msg = null;
			try {
				msg = consumer.receive(1000);
				if(msg==null){
					return null;
				}
				TextMessage txtMessage=(TextMessage)msg;
				return StringToTask(txtMessage.getText());
			} catch(javax.jms.IllegalStateException e){
				System.out.println(e.getMessage());
				Task task=null;
				if(checkAndRetry(task)){
					return task;
				}else{
					return null;
				}
								
			} catch (JMSException e) {
				return null;

			} 
			
	}
	private boolean checkAndRetry(Task task){
		try {
			super.session.createConsumer(super.destination);
			LogSys.nodeLogger.error("restart consumer");
			Message msg = consumer.receive(1000);
			TextMessage txtMessage=(TextMessage)msg;
			task=StringToTask(txtMessage.getText());;
			if(msg!=null){
				return true;
			}else{
				return false;
			}
		}catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	private Task StringToTask(String mes){
		Task t=new Task();
		String str=mes;
		if(str==null){			
			return null;
		}
		SimpleXmlAnalyser simxml=new SimpleXmlAnalyser(str);		
		String first=simxml.getFirstValueByTag("type");		
		String valuestr=simxml.getFirstValueByTag("value");
		
		String isTrack=simxml.getFirstValueByTag("isTrack");
		String taskTrackID=simxml.getFirstValueByTag("taskTrackID");
		String MainTypeStr=simxml.getFirstValueByTag("MainType");
		String MainTypeID_Str=simxml.getFirstValueByTag("MainTypeID");
		String PageCount_Str=simxml.getFirstValueByTag("PageCount");
		String TargetTableName_Str=simxml.getFirstValueByTag("TargetTableName");
		String AddParameter_Str=simxml.getFirstValueByTag("AddParameter");
		t.setMainType(MainType.valueOf(MainTypeStr));		
		t.setOwnType(TaskType.fromString(first));
		t.setTargetString(valuestr);
		t.setAddParameter(AddParameter_Str);
		if(isTrack!=null){
			t.setTrack(Boolean.parseBoolean(isTrack));
		}else{
			t.setTrack(false);
		}
		if(t.isTrack()){
			t.setTaskTrackID(Integer.parseInt(taskTrackID));
		}
		if(MainTypeID_Str!=null){
			int MainTypeID=Integer.parseInt(MainTypeID_Str);
			t.setMainTypeID(MainTypeID);
		}
		if(PageCount_Str!=null){
			t.setPageCount(Integer.parseInt(PageCount_Str));
		}else{
			t.setPageCount(-1);
		}
		if(TargetTableName_Str!=null){
			t.setTargetTableName(TargetTableName_Str);
		}else{
			t.setTargetTableName(null);
		}
		return t;
	}
}
