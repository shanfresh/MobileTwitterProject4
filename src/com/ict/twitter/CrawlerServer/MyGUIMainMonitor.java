package com.ict.twitter.CrawlerServer;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import com.ict.twitter.CrawlerServer.CrawlerServer.ServerStep;

public class MyGUIMainMonitor extends MainMonitor implements ActionListener  {

	/**
	 * @param args
	 */
	int KeyUsers;
	long MBTaskCount;
	int NorUsers;
	ServerStep currentStep;
	int TaskCountPerMinute;
	CrawlSpeedShowPanel panel;
	CrawlerServer crawlServer;
	
	public MyGUIMainMonitor(CrawlerServer crawlServer){
		super();
		this.crawlServer=crawlServer;
		panel=new CrawlSpeedShowPanel();
		//panel.new DataGenerator(1000).start();
		
		panel.setBounds(0, 300, 800, 200);
		jPanelBasic.add(panel);	
		this.pack();
		StartTimer();
		
		
	}
	private void StartTimer(){
		//30s 
		Timer tt=new Timer(10000, this);
		tt.start();
	}
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		MyGUIMainMonitor nn=new MyGUIMainMonitor(null);
		nn.setVisible(true);
		nn.setTextFieldValue(nn.jTextFieldKeyUser,"111");
		
	}
	
	public void setTextFieldValue(JTextField target,String value){
		target.setText(value);
	}

	public void setKeyUsers(int keyUsers) {
		KeyUsers = keyUsers;
		this.jTextFieldKeyUser.setText(String.valueOf(keyUsers));
	}

	public void setMBTaskCount(long count) {
		MBTaskCount = count;
		this.jTextFieldTaskCount.setText(String.valueOf(count));
	}

	public void setNorUsers(int norUsers) {
		NorUsers = norUsers;
		this.jTextFieldNormalUsersCount.setText(String.valueOf(norUsers));
	}

	public void setCurrentStep(ServerStep currentStep) {
		this.currentStep = currentStep;
		
		jTextFieldCurrentStep.setText(currentStep.toString());
		
	}

	public void setTaskCountPerMinute(int taskCountPerMinute) {
		TaskCountPerMinute = taskCountPerMinute;
		System.out.println("每分钟采集的数目是");
	}
	@Override
	public void actionPerformed(ActionEvent e) {
		if(crawlServer==null){
			System.err.println("crawlServer为NULL return");
			return;
		}
		int speedCountCalFromCrawlServer=0;
		try{
		
			long count=this.crawlServer.nodeManager.getTaskSizeCount();
			//当前总线上任务的大小。
			setMBTaskCount(count);
			setCurrentStep(crawlServer.currentstep);
			setNorUsers(crawlServer.sb.normalUserList.size());
			setKeyUsers(crawlServer.sb.keyUsers.size());
			speedCountCalFromCrawlServer=crawlServer.ServerReportData.message_increment+crawlServer.ServerReportData.message_rel_increment+crawlServer.ServerReportData.user_increment+crawlServer.ServerReportData.user_rel_increment;
			jTextFieldCrawlSpeed.setText(Integer.toString(speedCountCalFromCrawlServer));
			panel.addTotalObservation(speedCountCalFromCrawlServer);
			
			
		}catch(Exception ex){
			ex.printStackTrace();
			System.err.println("更新数据失败");
		}

		
	
		
		
		
	}
	


}
