package com.ict.twitter.CrawlerNode;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import com.ict.twitter.CrawlerServer.CrawlerServer;
import com.ict.twitter.MessageBus.*;
import com.ict.twitter.plantform.LogSys;

public class ControlReceiver extends Receiver {

	public ControlReceiver(javax.jms.Connection connection,String queue, Node _node,boolean isTopic) {
		super(connection,isTopic,queue, _node,false,null);
		isNode=true;
		this.node=_node;
		
	}	
	public ControlReceiver(javax.jms.Connection connection,String controlC2S, CrawlerServer _server,boolean isTopic,MessageListener msg) {
		super(connection,false,controlC2S, _server,false,msg);
		isNode=false;
		server=_server;
	}
	

	public void onMessage(Message _resmsg) {
		TextMessage resmsg=(TextMessage)_resmsg;
		String msg = null;		
		try {
			msg = resmsg.getText();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(isNode){
			LogSys.nodeLogger.debug("【node.NodeName"+"】"+"节点收到工作状态指令"+msg);
			if(msg.equalsIgnoreCase("STOPALL")){
				LogSys.nodeLogger.debug("客户端清空队列，停止工作with Task+"+node.taskBuffer.size()+"个.");
				node.CleanTask();
			}else if(msg.equalsIgnoreCase("STARTALL")){
				System.out.println("客户端重新启动工作");
				LogSys.nodeLogger.info("客户端目前的任务数是with Task"+node.taskBuffer.size()+"个.");
			}else if(msg.startsWith("NEWSTEP")){
				node.getNodeStatusBean().isBusy=true;				
				String[] all=msg.split(" ");
				if(all.length!=2){
					System.out.println("指令错误"+msg);
					return;
					
				}else{
					String command=all[1];
					node.getNodeStatusBean().curStep=NodeStep.valueOf(command);
					System.out.println("客户端收到新的采集状态"+command);
				}
			}else if(msg.startsWith("REPORTSTATUS")){
				node.getNodeStatusBean().HeartBeat(node.controlUpload);
				
			}else if(msg.startsWith("PAUSE")){
				System.out.println("客户端收到PAUSE指令");				
				node.Pause();
			}else if(msg.startsWith("RESUME")){
				System.out.println("客户端收到RESUME指令");	
				node.Resume();
			}
		}	
	}





}
