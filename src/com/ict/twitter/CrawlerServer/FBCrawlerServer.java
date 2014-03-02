package com.ict.twitter.CrawlerServer;

import com.ict.twitter.CrawlerNode.NodeStep;
import com.ict.twitter.CrawlerServer.CrawlerServer.OP;
import com.ict.twitter.MessageBus.MessageBusNames;
import com.ict.twitter.MessageBusTest.ControlClient;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.plantform.PlatFormMain;

public class FBCrawlerServer extends CrawlerServer {
	public static final String Name="FacebookWEBCrawl";	
	public enum ServerStep{
		init,keyuserCaijiStart,keyuserCaijiEnd,normalCaijiStart,normalCaijiEnd
	}
	public com.ict.twitter.CrawlerServer.FBCrawlerServer.ServerStep currentstep=ServerStep.init;
	
	
	@Override 
	public void run(){
		checkArgs();
		if(op==OP.START){
			Initiallize();
			StartCrawlServer();
		}else if(op==OP.STOP){
			System.err.println("此处需要修复");
			System.exit(-1);
			//StopCrawlServer();
		}
		
	}
	public FBCrawlerServer(String[] args){
		LogSys.crawlerServLogger.info("Crawlserver初始化");
		if (args.length < 1) {
			System.err.println("Usage: CrawlerServer -Command [Start|Stop|Dump|Restart] -Deepth 10 -KeySearchCount 10");
		    return;
	    }
		this.args=args;
	}

	public static void main(String[] args){
		for(int i=0;i<args.length;i++){
			System.out.print(args[i]+" ");
		}
		FBCrawlerServer crawler =  new FBCrawlerServer(args);
		Thread mthread= new Thread(crawler);
		mthread.setName("CrawlServer");
		mthread.start();
	}
	public boolean StartCrawlServer(){
		LogSys.crawlerServLogger.info("采集器总控端开始");
		try{
			CollectionNodes();
			CrawlerServerKeyUserSearch(false);
			NorUserSearchWithDepth(Normal_User_Deepth,false);
		}catch(Exception ex){
			ex.printStackTrace();
		}
		LogSys.crawlerServLogger.info("采集器总控端停止");
		LogSys.crawlerServLogger.info("正常采集结束All is Finish");
		return true;
	}
	public boolean RestartCrawlServer(){
		LogSys.crawlerServLogger.info("FBCrawlerServer Run Method isStart");			
		LogSys.crawlerServLogger.info("FBCrawlerServer开始恢复");			
		LogSys.crawlerServLogger.info("FBCrawlerServer开始运行");
		boolean flag=true;
		CollectionNodes();
		switch(this.currentstep){
			case init:
			case keyuserCaijiStart:
				CrawlerServerKeyUserSearch(flag);
				flag=false;
			case keyuserCaijiEnd:
			case normalCaijiStart:
				NorUserSearchWithDepth(Normal_User_Deepth,flag);
				flag=false;
			case normalCaijiEnd:
			
			break;
		}
		LogSys.crawlerServLogger.info("FBCrawlerServer恢复采集结束");
		return true;		
	}

	
	
	
	//通过递归逻辑实现了自动确定从那一层开始。
	private int NorUserSearchWithDepth(int deepth,boolean isResume){
		LogSys.crawlerServLogger.info("FBCrawlServer NormalUser Turn:["+deepth+"]");
		currentstep=ServerStep.normalCaijiStart;
		LogSys.crawlerServLogger.info("普通用户采集开始");		
		if(!isResume)
			NormalCaiji();
		while(currentstep!=ServerStep.normalCaijiEnd){
			CollectNodesStatus();
			SleepWithCount(20000);
			if(nodeManager.canNextStepByTaskBusName(MessageBusNames.Task))
				currentstep=ServerStep.normalCaijiEnd;
		}
		LogSys.crawlerServLogger.info("普通用户采集结束");
		if(deepth==1){
			return 0;
		}else{
			return NorUserSearchWithDepth(deepth-1,false);
		}
	}
	
	
	
	
	
	
	private boolean CollectNodesStatus(){
		controlSender.Send("REPORTSTATUS");		
		return true;
	}
	//采集种子用户信息的任务
	@Override
	public void ChuizhiCaiji(){							
		String filelocation=basepath+"/UsefulFile/Facebook/FBKeyIDs.txt";
		sb.InitChuizhi(filelocation, this,isFirstChuiZhi);
		SleepWithCount(5000);
		sendNewStep(NodeStep.keyuser_start);		
		isFirstChuiZhi=false;		
	}
	
	

	@Override
	public void NormalCaiji(){
		LogSys.crawlerServLogger.info("【Server】-----------开始并行采集-----------------");			
		//准备并行采集的种子信息
		int count=sb.InitBingxing(this,deepth);
		LogSys.crawlerServLogger.info("【Server】总共新加并行搜索任务数"+count+"个");
		SleepWithCount(5000);
		sendNewStep(NodeStep.normaluser_start);		
	}
}
