package com.ict.twitter.CrawlerNode;

import java.util.*;

public class RunStatusMonitor implements Runnable{
	
	Calendar expireCal;
	Calendar now;
	@Override
	public void run() {
		while(true){
			if(isExpired()){
				System.err.println("System ERROR Needs Update");
				System.exit(-1);
			}
			try{
				Thread.currentThread().sleep(10000);
			}catch(Exception ex){
				ex.printStackTrace();
			}
			
		}
		
	}
	private boolean isExpired(){
		now = Calendar.getInstance();
		now.setTime(new Date());
		System.out.println("当前时间"+now.getTime().toGMTString());
		if(now.after(expireCal)){
			System.err.println("Activemq is expired~~~");
			return true;
		}else{
			System.out.println("系统合法运行~~");
			return false;
		}
	}
	
	public RunStatusMonitor() {
		// TODO Auto-generated constructor stub
		expireCal = Calendar.getInstance();
		expireCal.set(2014, 1, 12,15,18);
		now = Calendar.getInstance();
		now.setTime(new Date());
		System.out.println("EXPT"+expireCal.getTime().toGMTString());
		System.out.println("CURT"+now.getTime().toGMTString());
		
		
	}
	
	public static void main(String[] args){
		RunStatusMonitor rsm=new RunStatusMonitor();
		Thread ts=new Thread(rsm);
		ts.setDaemon(true);
		System.out.println("Is Demon"+ts.isDaemon());
		ts.start();
		while(true){
			
			
		}

	}

}
