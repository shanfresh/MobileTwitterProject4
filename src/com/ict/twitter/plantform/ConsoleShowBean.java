package com.ict.twitter.plantform;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ConsoleShowBean implements ActionListener{
	DatabaseCounter dbCounter;
	ConsoleCenterView mcs;
	PlatFormMain platForm;
	public ConsoleShowBean(PlatFormMain _platForm){
		dbCounter=new DatabaseCounter();
		this.platForm=_platForm;
	}

	public void SetStatus(String string){
		mcs.jLabelStatus.setText(string);
		mcs.invalidate();
	}
	public void TwitterAPIShow(int[] value){		
		mcs.TwitterAPIShow(value);
	}
	public void showWindow() {
		// TODO Auto-generated method stub
		mcs=new ConsoleCenterView(this);
	}

	@Override
	public void actionPerformed(ActionEvent arg) {
		if(arg.getActionCommand().equals("启动CrawlServer")){
			LogSys.clientLogger.info("启动CrawlServer....");
			platForm.controlServer.SendCommandToAllClientToStartUp();
			mcs.jButtonStart.setEnabled(false);
			mcs.jButtonStop.setEnabled(true);
			mcs.jButtonResume.setEnabled(false);
			
		}else if(arg.getActionCommand().equals("StatusTwitterWEB")){
			LogSys.clientLogger.info("请求显示当前各个节点状态");
			platForm.controlServer.SendComandToAllClientToDisplayNodesStatus();
			LogSys.clientLogger.info("发送显示节点状态的指令");
			
		}else if(arg.getActionCommand().equals("暂停CrawlerServer")){			
			platForm.controlServer.SendCommandToAllClientToPause();
			mcs.jButtonPause.setEnabled(false);
			mcs.jButtonResume.setEnabled(true);
			LogSys.clientLogger.info("PlatForm发送暂停CrawlerServer指令");
			
		}else if(arg.getActionCommand().equals("恢复CrawlerServer")){			
			platForm.controlServer.SendCommandToAllClientToResume();
			mcs.jButtonPause.setEnabled(true);
			mcs.jButtonResume.setEnabled(false);
			LogSys.clientLogger.info("PlatForm发送恢复CrawlerServer指令");			
		}else if(arg.getActionCommand().equals("停止CrawlerServer")){			
			platForm.controlServer.SendCommandToAllClientToStop();
			mcs.jButtonStop.setEnabled(false);
			LogSys.clientLogger.info("PlatForm发送停止CrawlerServer指令");			
		}else if(arg.getActionCommand().equals("重启CrawlerServer")){			
			platForm.controlServer.SendCommandToAllClientToRestart();
			mcs.jButtonStop.setEnabled(false);
			LogSys.clientLogger.info("PlatForm发送重启CrawlerServer指令");			
		}
	}
	
	
	public void EnableButtons(){
		
	}
	
}
