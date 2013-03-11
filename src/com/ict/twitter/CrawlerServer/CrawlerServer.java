package com.ict.twitter.CrawlerServer;

import com.ict.twitter.CrawlerMessage.MessageBusComponent;
import com.ict.twitter.CrawlerNode.ControlReceiver;
import com.ict.twitter.CrawlerNode.ControlSender;
import com.ict.twitter.CrawlerNode.NodeStep;
import com.ict.twitter.MessageBus.GetAceiveMqConnection;
import com.ict.twitter.MessageBus.MessageBusNames;
import com.ict.twitter.MessageBus.MessageBussConnector;
import com.ict.twitter.MessageBus.Receiver;
import com.ict.twitter.MessageBus.Sender;
import com.ict.twitter.MessageBusTest.ControlClient;
import com.ict.twitter.MessageBusTest.MessageBusCleanner;
import com.ict.twitter.Report.NodeReporterReceiver;
import com.ict.twitter.Report.ReportData;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.plantform.PlatFormMain;
import com.ict.twitter.task.beans.Task;

import com.ict.twitter.tools.BasePath;

import java.util.HashMap;
public class CrawlerServer  extends MessageBusComponent implements Runnable,MessageBusNames,MessageBussConnector {

	/**
	 * 必须要改！！
	 */
	public static final String Name="TwitterWEB";
	public enum ServerStep{
		init,searchStart,searchEnd,keyuserCaijiStart,keyuserCaijiEnd,normalCaijiStart,normalCaijiEnd
	}
	protected static enum OP {START, STOP, DUMP, RESTART};
	public com.ict.twitter.CrawlerServer.CrawlerServer.ServerStep currentstep=ServerStep.init;
	public int Normal_User_Deepth = 20;
	
	//控制相关
	Receiver controlReceiver;	
	ControlSender controlSender;
	//Task相关
	Sender taskSender;
	Receiver taskReceiver;
	//NormalUser 相关
	Receiver NormalReceiver;
	Receiver KeyUserReceiver;
	Receiver nodeReporterReceiver;
	
	public HashMap<String,ReportData> NodeReportData;
	public ReportData ServerReportData;
	public static long count;
	public static long TaskID;
	
	public boolean isFirstChuiZhi=true;
	public boolean isFirstBingXing=true;

	public ServerBean sb=new ServerBean();
	
	public boolean isResume=false;
	
	
	//管理子节点进度信息
	public NodeManager nodeManager;
	private ServerControlReceiverListener scr;
	/***************************/
	protected String args[];
	protected OP op=null;
	protected int deepth=10;
	/***************************/
	public CrawlerServer(){
		this("-Command Start -Deepth 10".split(" "));	
		
	}
	public CrawlerServer(String[] args){
		LogSys.crawlerServLogger.info("Crawlserver初始化");
		if (args.length < 1) {
			System.err.println("Usage: CrawlerServer -Command [Start|Stop|Dump|Restart] -Deepth 10");
		    return;
	    }
		this.args=args;
	}
	public static void main(String[] args){
		for(int i=0;i<args.length;i++){
			System.out.print(args[i]+" ");
		}
		CrawlerServer crawler =  new CrawlerServer(args);
		Thread mthread= new Thread(crawler);
		mthread.setName("CrawlServer");
		mthread.start();
		
	}
	@Override
	public void run() {
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
	public void checkArgs(){		
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-Command")){
				String command=args[++i];
				if(command.equals("Start"))
					op=OP.START;
				else if(command.equals("Stop")){
					op=OP.STOP;
				}else if(command.equals("Dump")){
					op=OP.DUMP;
				}else if(command.equals("Restart")){
					op=OP.RESTART;
				}					
			}else if(args[i].equals("-Deepth")){
				deepth=Integer.parseInt(args[++i]);
			}
		}
		this.Normal_User_Deepth=deepth;
		

	}

	public int run(String[] args) throws Exception {
				
		return 0;
	}
	
	


	private void showGUI(){
		MyGUIMainMonitor nn=new MyGUIMainMonitor(this);
		nn.setVisible(true);
		nn.setTextFieldValue(nn.jTextFieldKeyUser,"111");
	}
	public void Initiallize(){

		javax.jms.Connection connection=GetAceiveMqConnection.StaticGetConnection();
		LogSys.crawlerServLogger.info("--------------Server初始化-------------------");		
		basepath=BasePath.getBase();		
		NodeReportData=new HashMap<String,ReportData>();
		ServerReportData=new ReportData();
		nodeManager=new NodeManager();
		scr=new ServerControlReceiverListener(this);
		taskSender=new Sender(connection,MessageBusNames.Task+"?consumer.prefetchSize=0",false);	
		//taskReceiver=new Receiver(connection, false, MessageBusNames.Task, this, true, null);
		controlReceiver=new ControlReceiver(connection,MessageBusNames.ControlC2S,this,false,scr);
   		controlSender=new ControlSender(connection,MessageBusNames.ControlS2C,true);
		NormalReceiver=new NormalUserReceiver(connection,MessageBusNames.NormalID,this,false);
		KeyUserReceiver=new KeyUserReceiver(connection,MessageBusNames.KeyID,this,false);
		nodeReporterReceiver=new NodeReporterReceiver(connection,MessageBusNames.ReportTwitterWEB,this,false);
		
	}

	public static String basepath;
	
	public boolean StartCrawlServer(){
		LogSys.crawlerServLogger.info("采集器总控端开始");
		try{
			CollectionNodes();
			KeyWordSearch(false);		
			CrawlerServerKeyUserSearch(false);
			CrawlerServerNorUserSearch(this.deepth,false);
			LogSys.crawlerServLogger.info("正常采集结束All is Finish");
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.crawlerServLogger.error("crawlServer exit with error");
		}
		LogSys.crawlerServLogger.info("采集器总控端停止");
	
		return true;
	}
	
	
	/*Try To Stop Current CrawlServer~~
	 * 1:TellNodeToPause (don't get Task from MessageBus);
	 * 2:TaskMessage Bus Save To File
	 * 3:Save Current Status--CollentionNodes,MainSearch,KeyUser,NormalUser
	 * 3:
	 * 4:CrawlServerToStop;
	 * 5:finish;
	 * 
	 * */
	public boolean StopCrawlServer(){
		this.TellNodeToPause();
		//等待确认---miss
		CrawlerServerDumper crawlerDumper=new CrawlerServerDumper(this);
		if(crawlerDumper.TaskSaver("UsefulFile\\Facebook\\TaskDump.dat"))
			LogSys.crawlerServLogger.info("Success To Save Task To File");
		else{
			LogSys.crawlerServLogger.info("Fail To Save Task To File");
			return false;
		}
		/*保存当前状态
		 * Status;ServerBean;
		*/
		if(crawlerDumper.OtherStatusSaver("UsefulFile\\Facebook\\OtherStatusDump.dat"))
			LogSys.crawlerServLogger.info("Success To Save Status To File");
		else{
			LogSys.crawlerServLogger.info("Fail To Save Status To File");
			return false;
		}
		return true;	
		
	}
	//restart from file
	
	public boolean RestartCrawlServer(){
		LogSys.crawlerServLogger.info("采集器开始恢复");			
		LogSys.crawlerServLogger.info("采集器恢复完成开始运行");
		this.TellNodeToPause();
		CrawlerServerDumper crawlerDumper=new CrawlerServerDumper(this);
		if(crawlerDumper.TaskResume("UsefulFile\\Facebook\\TaskDump.dat"))
			LogSys.crawlerServLogger.info("Success To Resume Task From File To CrawlServer");
		else{
			LogSys.crawlerServLogger.error("Fail To Resume Task From File To CrawlServer");
			return false;
		}
		if(crawlerDumper.OtherStatusResumer("UsefulFile\\Facebook\\OtherStatusDump.dat"))
			LogSys.crawlerServLogger.info("Success To Resume Status From File To CrawlServer");		
		else{
			LogSys.crawlerServLogger.error("Fail To Resume Status From File To CrawlServer");
			return false;
		}
		this.CollectionNodes();
		this.TellNodeToResume();
		switch(this.currentstep){
			case init:
			case searchStart:
				KeyWordSearch(true);
			case searchEnd:
			case keyuserCaijiStart:
				CrawlerServerKeyUserSearch(true);
			case keyuserCaijiEnd:
			case normalCaijiStart:
				CrawlerServerNorUserSearch(this.deepth,true);
			case normalCaijiEnd:
			default:
				System.err.println("没有发现可以恢复的起点，当前状态是"+currentstep);

		}
		LogSys.crawlerServLogger.info("恢复采集结束All is Finish");
		return true;
	}
	
	
	protected void SleepWithCount(int count){
		try {
			Thread.sleep(count);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	protected void CollectionNodes(){
		int count=0;
		LogSys.crawlerServLogger.info("搜索节点15S");
		SleepWithCount(15000);		
		LogSys.crawlerServLogger.info("当前状态"+nodeManager.currentstep());
		LogSys.crawlerServLogger.info("节点个数"+nodeManager.nodecount);	
	}
	protected void KeyWordSearch(boolean isResume){
		currentstep=ServerStep.searchStart;
		LogSys.crawlerServLogger.info("关键词搜索开始");			
		
		//非常重要
		if(!isResume)
			KeyWordsSearch();
		while(currentstep!=ServerStep.searchEnd){
			CollectNodesStatus();
			SleepWithCount(60000);				
			if(nodeManager.canNextStep()){
				currentstep=ServerStep.searchEnd;
				nodeManager.show();
			}else{
				LogSys.crawlerServLogger.debug("不能进入下一个采集状态");
				
			}
		}
		LogSys.crawlerServLogger.info("关键词搜索结束");
	}
	protected void CrawlerServerKeyUserSearch(boolean isResume){
		
		currentstep=ServerStep.keyuserCaijiStart;
		LogSys.crawlerServLogger.info("关键用户采集开始");		
		if(!isResume)
			ChuizhiCaiji();
		while(currentstep!=ServerStep.keyuserCaijiEnd){
			CollectNodesStatus();
			SleepWithCount(20000);
			if(nodeManager.canNextStep())
				currentstep=ServerStep.keyuserCaijiEnd;
		}
		LogSys.crawlerServLogger.info("关键用户采集结束");
	}
	protected int CrawlerServerNorUserSearch(int deepth,boolean isResume){
		currentstep=ServerStep.normalCaijiStart;
		LogSys.crawlerServLogger.info("普通用户采集开始---深度："+deepth+"-----");		
		if(!isResume)
			NormalCaiji();
		
		while(currentstep!=ServerStep.normalCaijiEnd){
			CollectNodesStatus();
			SleepWithCount(20000);
			if(nodeManager.canNextStep())
				currentstep=ServerStep.normalCaijiEnd;
		}
		LogSys.crawlerServLogger.info("普通用户采集结束---深度："+deepth+"-----");
		if(deepth==1){
			return 0;
		}else{
			return CrawlerServerNorUserSearch(deepth-1,false);
		}
	}
	
		
	//采集种子用户信息的任务
	public void ChuizhiCaiji(){							
		String filelocation=basepath+"/UsefulFile/KeyIDs.txt";
		sb.InitChuizhi(filelocation, this,isFirstChuiZhi);
		SleepWithCount(5000);
		sendNewStep(NodeStep.keyuser_start);
		
		isFirstChuiZhi=false;
		
		
	}
	public void KeyWordsSearch(){
		String filelocation=basepath+"/UsefulFile/minganci_min.txt";
		sb.InitSearch(filelocation, this);
		SleepWithCount(5000);
		sendNewStep(NodeStep.search_start);
		
			
	}
	public void NormalCaiji(){
		LogSys.crawlerServLogger.info("【Server】-----------开始并行采集-----------------");			
		//准备并行采集的种子信息
		int count=sb.InitBingxing(this,deepth);
		LogSys.crawlerServLogger.info("【Server】总共新加并行搜索任务数"+3*count+"个");
		SleepWithCount(5000);
		sendNewStep(NodeStep.normaluser_start);
		
		
	}
	public boolean addTask(Task task){
		return taskSender.Send(task.TaskTOString());		
	}
	//添加普通用户
	public void addNormalUser(NormalUser nu){		
		sb.addNormalUser(nu);		
	}
	
	public void addKeyUser(NormalUser nu){
		sb.addKeyUser(nu);
	}
	public int showNormalUserSize(){
		return sb.normalUserList.size();
	}
	
	
	//所有节点停止工作
	public boolean stopTaskOnAllNode(){
		LogSys.crawlerServLogger.info("发送停止工作指令");
		controlSender.Send("STOPALL");
		return true;
	}

	public boolean startTaskOnAllNode(){
		LogSys.crawlerServLogger.info("通知节点启动工作");
		controlSender.Send("STARTALL");
		return true;
	}
	
	public boolean sendNewStep(NodeStep step){
		LogSys.crawlerServLogger.info("通知所有节点进入新的工作状态"+step);
		controlSender.Send("NEWSTEP "+step.toString());
		return true;
	}
	private boolean CollectNodesStatus(){
		controlSender.Send("REPORTSTATUS");		
		return true;
	}
	
	//发送至node 暂停采集
	public void TellNodeToPause(){
		LogSys.crawlerServLogger.info("CrawlerServer Send Pause");
		controlSender.Send("PAUSE");
	}
	//发送至node 恢复采集
	public void TellNodeToResume(){
		controlSender.Send("RESUME");		
	}
	
	
	
	
	
	
	/*
	 * 1：清除队列中所有的消息
	 */
	public void ResetMessageBus(){
		MessageBusCleanner.Clean();

	}

	public void getNewNode(String nodeName) {
		PlatFormMain.log.info("【控制台】接受到新的节点");
		nodeManager.addNode(nodeName);
	}
	public void oneNodeFinishWork(String nodeName,NodeStep currentstep){
		nodeManager.onnodefinish(nodeName,currentstep);
		nodeManager.onnodefinish(nodeName, currentstep);
	}
	
	public void addNormalUserByID() {
		
		
	}
	//收到HeartReport
	public void onReceiveReportFromNode(ReportData rpdata){
		ServerReportData.add(rpdata);
		if(NodeReportData.containsKey(rpdata.NodeName)){
			ReportData tmprpdata=NodeReportData.get(rpdata.NodeName);
			tmprpdata.add(rpdata);			
		}else{
			NodeReportData.put(rpdata.NodeName, rpdata);
			LogSys.crawlerServLogger.debug("Crawler 收到信息来自新节点("+rpdata.NodeName+")");
			
		}
		

	}
	public void ShowAndLog(String msg){
		LogSys.crawlerServLogger.info("【CRAWLERSERVER】"+msg);
		
	}


	

	
	
	

	

}
