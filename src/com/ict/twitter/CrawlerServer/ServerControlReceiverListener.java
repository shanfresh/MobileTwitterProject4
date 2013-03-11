package com.ict.twitter.CrawlerServer;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

import com.ict.twitter.CrawlerNode.NodeHeartBeatReport;
import com.ict.twitter.plantform.LogSys;

public class ServerControlReceiverListener implements MessageListener{
	CrawlerServer crawlServer;
	public ServerControlReceiverListener(CrawlerServer _crawlServer){crawlServer=_crawlServer;}
	
	public void onMessage(Message msg){
		//System.out.println("CrawlerServer收到控制信息"+msg.toString());
		if(msg instanceof ObjectMessage){
			ObjectMessage obj=(ObjectMessage)msg;
			try {
				NodeHeartBeatReport report=(NodeHeartBeatReport)obj.getObject();
				crawlServer.nodeManager.onHeartBeatReceive(report);
				return;
				
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch(Exception ex){
				System.out.println("错误"+msg.toString());
				ex.printStackTrace();
				LogSys.crawlerServLogger.error("监听器发现一个消息，但是不确定是否为上次遗留下来的，或者刚刚生成的");
			}
			
		}
		if(msg instanceof TextMessage){
			System.out.println("服务器的控制通道收到文本信息");
			TextMessage txtmsg=(TextMessage)msg;			
			String text=null;
			try {
				text = txtmsg.getText();
				LogSys.crawlerServLogger.info("服务器端控制消息"+text);
			} catch (JMSException e) {
				e.printStackTrace();
				return;
			}
			
		}

		

		
		
	}
	public static void main(String[] args){
		
		
	}

}
