package com.ict.twitter.MessageBusTest;

import com.ict.twitter.CrawlerNode.runFBAjaxNodes;
import com.ict.twitter.CrawlerServer.FBCrawlerServer;
import com.ict.twitter.plantform.LogSys;

public class FacebookWEBControlClient extends ControlClient {
	public FBCrawlerServer server;
	public Thread serverThread;
	public FacebookWEBControlClient(ClientsID name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	@Override
	public boolean ToStart(){
		LogSys.clientLogger.info("启动FacebookWeb采集系统CrawlerServer");
		server=new FBCrawlerServer("-Command Start -Deepth 10 -KeySearchCount 10".split(" "));
		serverThread=new Thread(server);
		serverThread.setName("FacebookWebCrawlerServer");
		serverThread.start();
		LogSys.clientLogger.info("启动FacebookWeb采集系统CrawlerNodes节点");
		if(runFBAjaxNodes.run()){
			LogSys.clientLogger.info("启动FacebookWeb采集系统CrawlerNodes节点成功");
		}else{
			LogSys.clientLogger.info("启动FacebookWeb采集系统CrawlerNodes节点失败");
			return false;
		}
		return true;
	}

	@Override
	public boolean ToPause() {
		//Console 发送消息给Server
		//CrawlServer 发送给Node
		//Node 进行暂停
		LogSys.nodeLogger.info("FacebookWEBControlClient user Crawlserver to TellNodeToPause");
		server.TellNodeToPause();
		
		
		return true;
	}
	
	@Override
	public boolean ToResume() {
		//如果是停止后，恢复则需要读入当时的Task列表到ActiveMQ
		//如果不是停止后恢复则只要全部的Node开始采集即可。		
		server.TellNodeToResume();
		return true;
		
	}
	

	@Override
	public boolean ToDisplay(){
		LogSys.clientLogger.info("FacebookWeb收到展示TwitterNode 信息");
		LogSys.clientLogger.info("FacebookWeb Nodes 信息如下--------------");
		if(server!=null){
			server.nodeManager.show();
			LogSys.clientLogger.info("FacebookWeb TaskSize{"+server.nodeManager.getTaskSizeCount()+"}");			
		}
		LogSys.clientLogger.info("---------------------------------------");
		return true;
	}
	
	public boolean ToStop(){
		LogSys.clientLogger.info("FacebookWeb收到停止CrawlerServer信息");
		if(server.StopCrawlServer()){
			serverThread.stop();
			return true;
		}
		else{
			return false;
		}		
	}

	public boolean ToRestart(){
		LogSys.clientLogger.info("FacebookWeb收到恢复重启CrawlerServer信息");
		server=new FBCrawlerServer("-Command Start -Deepth 10 -KeySearchCount 10".split(" "));
		server.RestartCrawlServer();
		server.isResume=true;
		serverThread=new Thread(server);
		serverThread.setName("FacebookWebCrawlerServer");
		serverThread.start();
		if(runFBAjaxNodes.run()){
			LogSys.clientLogger.info("启动FacebookWeb采集系统CrawlerNodes节点成功");
		}else{
			LogSys.clientLogger.info("启动FacebookWeb采集系统CrawlerNodes节点失败");
			return false;
		}
		return true;
	}
	



}
