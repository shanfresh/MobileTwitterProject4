package com.ict.twitter.CrawlerNode;

import java.util.Timer;
import java.util.Vector;

import com.ict.twitter.MainSearch;
import com.ict.twitter.CrawlerMessage.MessageBusComponent;
import com.ict.twitter.MessageBus.GetAceiveMqConnection;
import com.ict.twitter.MessageBus.MessageBusNames;
import com.ict.twitter.MessageBus.Receiver;
import com.ict.twitter.MessageBus.Sender;
import com.ict.twitter.Report.NodeReporterSender;
import com.ict.twitter.Report.ReportData;
import com.ict.twitter.Report.ReportDataType;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.BasePath;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.ReadTxtFile;
import com.ict.twitter.tools.SaveTxtFile;
public abstract class Node extends MessageBusComponent implements Runnable{
	/*----------------------控制相关--------------------------------*/
	public String NodeName;
	private boolean isPaused=false;
	//本地task缓存
	public Vector<Task> taskBuffer=null;
	//控制发送、控制接受
	ControlSender controlUpload;	
	Receiver controlDownload;
	//任务接受总线
	TaskReceiver taskReceiver;	
	//状态报告类
	NodeReporterSender nodeReportSender;
	private boolean ReportNullFlag=false;
	
	
	public SaveTxtFile svt=new SaveTxtFile("UsefulFile/newMinganci.txt",true);
	public ReportData rpdata;
	//----------------------------------------------------------------------
	//普通用户信息发送
	Sender normalUserSender;
	Sender keyUserSender;
	public DbOperation dbOper;
	boolean isProxy;
	
	private NodeStatusBean nodeStatusBean;
	//----------------------------------------------------------------------//
	
	public NodeStatusBean getNodeStatusBean() {
		return nodeStatusBean;
	}
	public Node(String name,DbOperation dbOper){
		NodeName=name;
		nodeStatusBean=new NodeStatusBean(this);
		this.dbOper=dbOper;
	}
	
	//测试专用

	@Override
	public void run() {
		NodeStart();
		TimerStart();
		SendHeartBeat();
		startMainSearch();		
		

	}

	public void InitTaskReceiver(javax.jms.Connection connection){
		String URL=MessageBusNames.Task+"?consumer.prefetchSize=0";
		taskReceiver=new TaskReceiver(connection,URL,this);
	}
	public void NodeStart(){
		try{
			readProperties();			
			taskBuffer=new Vector<Task>(100);
			rpdata=new ReportData(0, 0, 0, 0,NodeName);
			javax.jms.Connection connection=GetAceiveMqConnection.StaticGetConnection();
			//创建基于Topic的 MessageReceiver
			//初始化消息总线。
			InitTaskReceiver(connection);
			controlUpload=new ControlSender(connection,MessageBusNames.ControlC2S,false);
			controlDownload=new ControlReceiver(connection,MessageBusNames.ControlS2C,this,true);				
			normalUserSender=new Sender(connection,MessageBusNames.NormalID,false);
			keyUserSender=new Sender(connection,MessageBusNames.KeyID,false);
			nodeReportSender=new NodeReporterSender(connection,MessageBusNames.ReportTwitterWEB,false);
			
			if(dbOper==null){
				dbOper=new DbOperation();
			}
		
		}catch(Exception e){
			System.err.println("客户端初始化或者启动MainSearch失败");			
			e.printStackTrace();
		}

	}
	protected void startMainSearch(){
		MainSearch ms=new MainSearch(this,isProxy);
		Thread mainThread=new Thread(ms);
		mainThread.setName("MainSearchThread-"+this.NodeName+"");
		mainThread.start();	
		
	}
	//添加新的值
	public void ModifyReportMessageByType(ReportDataType rpt,int count){
		switch (rpt){
		case Message:
			rpdata.message_increment++;
			break;
		case Message_rel:
			rpdata.message_rel_increment++;
			break;
		case User:
			rpdata.user_increment++;
			break;
		case User_rel:
			rpdata.user_rel_increment++;
			break;			
		}
	}
	

	
	private void readProperties() throws Exception{
		String base=BasePath.getBase();
		ReadTxtFile rxf=new ReadTxtFile(base+"/UsefulFile/myproperties.txt");
		Vector<String> pro=rxf.read();
		for(String t:pro){
			if(t.startsWith("http.isProxy")){
				String res=t.substring(t.indexOf('=')+1);
				if(res.equals("true")){
					this.setIsProxy(true);
				}else{
					this.setIsProxy(false);
				}
			}
		}
	}


	//汇报当前采集的总量
	
	private void TimerStart(){
		try{
			Timer timer=new Timer();
			timer.schedule(new NodeReport(this), 1000, 60000);
		}catch(Exception ex){
			ex.printStackTrace();
			LogSys.nodeLogger.error("启动定时器失败");
		}
	}

	
	
	//Node 的报告类
	public void nodeReportToCrawlServer(){
		nodeReportSender.Send(rpdata);
		rpdata=new ReportData();
		rpdata.NodeName=this.NodeName;
	}
	
	//节点启动
	public void SendHeartBeat(){
		nodeStatusBean.HeartBeat(controlUpload);
	}

	
	//节点任务为空
	private void SendToServerTaskIsOver(){
		if(taskBuffer.size()!=0){
			System.err.println("SendToServerTaskIsOver TaskSize不为0");
			return;
		}
		else{
			try{
				if(taskBuffer.size()>0)
					return;
			}catch(Exception sleep){
				sleep.printStackTrace();
			}			
			if(nodeStatusBean.curStep==NodeStep.init){
				nodeStatusBean.HeartBeat(controlUpload);
			}
			else if(nodeStatusBean.curStep==NodeStep.search_start){
				nodeStatusBean.curStep=NodeStep.search_end;}
			else if(nodeStatusBean.curStep==NodeStep.keyuser_start){
				nodeStatusBean.curStep=NodeStep.keyuser_end;}
			else if(nodeStatusBean.curStep==NodeStep.normaluser_start){
				nodeStatusBean.curStep=NodeStep.normaluser_end;
			}
			nodeStatusBean.HeartBeat(controlUpload);
		
		}
	}
	
	public void doTask(Task task){
		
		taskBuffer.add(task);	
	}
	
	public void Pause(){
		this.isPaused=true;
		LogSys.nodeLogger.debug(this.NodeName+" is Paused");
	}
	public void Resume(){
		this.isPaused=false;
		LogSys.nodeLogger.debug(this.NodeName+" is Resumed");
	}
	
	public Task getTask(){
		try{
			Task task=null;
			if(isPaused){
				return null;
			}else{
				task=taskReceiver.PickUpTaskMessage();
				if(task==null&&!ReportNullFlag){
					ShowAndLog("【任务缓存为空】");
					ReportNullFlag=true;
				}else if(task!=null){
					ReportNullFlag=false;
				}
			}
			return task;
			
		}catch(Exception e){
			try{
				Thread.sleep(1000);
			}catch(Exception sleep){
				sleep.printStackTrace();
			}			
			return null;
		}
	}
	public boolean CleanTask(){
		try{
			taskBuffer.removeAllElements();
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}		
		return true;
	}

	
	

	//添加普通用户信息
	public void addNomalUserIDs(String jms){
		normalUserSender.Send(jms);
	}
	public void addKeyUserIDs(String jms){
		keyUserSender.Send(jms);
	}

	


	public void setIsProxy(boolean _isProxy){
		this.isProxy=_isProxy;
	}
	public void setStep(NodeStep newStep){
		nodeStatusBean.curStep=newStep;
	}
	public synchronized void saveSearchWords(String t){
		svt.Append(t);
		svt.Append("\r\n");
		svt.flush();
	}
	private void ShowAndLog(String msg){
		LogSys.nodeLogger.info("【"+this.NodeName+"】: "+msg);		
	}
	public void InitTaskReceiver() {
		// TODO Auto-generated method stub
		
	}
	


}
