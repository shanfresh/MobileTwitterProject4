package com.ict.twitter.plantform;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.ict.twitter.CrawlerServer.CrawlerServer;
import com.ict.twitter.MessageBusTest.MessageBusCleanner;
import com.ict.twitter.MessageBusTest.ControlServer;

public class PlatFormMain {
	public static Log log= LogFactory.getLog(ControlServer.class);
	public String status;
	ConsoleCenterView msgShow;
	ConsoleShowBean consoleShowBean;
	ControlServer controlServer;
	CrawlerServer _server;
	public static boolean CommonTest(){
		PropertyConfigurator.configure ("config/log4j_Main.properties" ) ;
		log.info("网络测试.......");
		NetworkTest nt=new NetworkTest();
		boolean[] netWork=nt.TestNetwork();
		if(!(netWork[0]||netWork[1])){
			log.info("网络不通畅,检查网络或者在代理模式下检查代理服务器.......");
			return false;
		}
		MessageBusTest mt=new MessageBusTest();
		log.info("网络通畅......");
		if(!mt.doTest()){
			log.error("消息总线异常,请检查消息总线设置.......");
			return false;
		}else{
			log.debug("消息总线工作正常");
		}
		
		return true;
	}
	public void start(){
		PropertyConfigurator.configure ("config/log4j_Main.properties" ) ;
		consoleShowBean=new ConsoleShowBean(this);
		consoleShowBean.showWindow();
		PlatFormMain.log.info("显示界面已经启动");
		
		consoleShowBean.SetStatus("初始化...");
		PlatFormMain.log.info("设置状态为初始化");
		if(!netTest()){
			PlatFormMain.log.error("网络以及Active总线异常");
			consoleShowBean.SetStatus("网络ActiveMQ异常");
			return;
		}
		if(!MongoDBTest.doTest()){
			log.error("MongoDB异常,请检查MongoDB设置.......");
			consoleShowBean.SetStatus("MongoDB异常,请检查MongoDB设置.......");
			return;
		}		
		PlatFormMain.log.info("清空消息总线");
		cleanMessageBus();
		showTaskSize();
		consoleShowBean.SetStatus("消息总线清空完毕");
		if(!startControlServer()){
			PlatFormMain.log.error("启动ControlServer异常");
			return;
		}else{
			consoleShowBean.SetStatus("【ControlServer】启动成功,节点数"+controlServer.allNodeCondition.size());
			consoleShowBean.mcs.EnableButton();
		}		
		if(!startReporter()){
			PlatFormMain.log.info("ControlCenter 数据库汇报定时器");
			consoleShowBean.SetStatus("【ControlCenter 数据库汇报定时器】启动失败");
		}else{
			PlatFormMain.log.info("【ControlCenter 数据库汇报定时器】启动成功");
		}
		
		
	} 
	

	public void SetInfoToShowWindow(String str){
		consoleShowBean.SetStatus(str);
	}
	
/*
 	public boolean startTwitterAPIServer(){
		try{
			PlatFormMain.log.info("准备启动TwitterWeb Server");
			consoleShowBean.SetStatus("启动TwitterWeb Server....");
			_server=new CrawlerServer(null);
			Thread t=new Thread(_server);
			t.start();
			PlatFormMain.log.info("启动完毕TwitterWeb Server");
			consoleShowBean.SetStatus("启动TwitterWeb Server....");
			return true;
		}catch (Exception ex){
			ex.printStackTrace();
			return false;			
		}
		
	}
*/
	
	

	public boolean startControlServer(){
		PlatFormMain.log.info("启动ControlServer中......");
		try{
			controlServer=new ControlServer();
			Thread thread=new Thread(controlServer);
			thread.start();
			//循环监测状态直到节点启动为止。
			while(controlServer.status!="INIT＿Ok"){
				SetInfoToShowWindow(controlServer.status);
				PlatFormMain.log.info("controlSer 正在启动");
				Thread.sleep(1000);				
			}
			
		}catch(Exception ex){
			ex.printStackTrace();
			return false;
			
		}		
		return true;
	}
	

	private boolean netTest(){
		return PlatFormMain.CommonTest();
	}
	private boolean cleanMessageBus(){
		MessageBusCleanner.Clean();
		return true;
	}
	private boolean showTaskSize(){
		
		return true;
	}
	
	public boolean startReporter(){
		Timer t=new Timer();
		int time=600000;
		Properties properties=new Properties();
		try {
			properties.load(new FileInputStream("config/PlatFormConsole.ini"));
			time=Integer.parseInt(properties.getProperty("report.refreshTime"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(java.lang.NumberFormatException e){
			time=600000;
		}
		t.schedule(new TotalCountShowSchedule(consoleShowBean),10000,time);
		
		return true;
	}
	
	
	public static void main(String[] args){	
		PlatFormMain pfm=new PlatFormMain();
		pfm.start();
	}

}
