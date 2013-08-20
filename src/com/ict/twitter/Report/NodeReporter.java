package com.ict.twitter.Report;

import java.sql.SQLException;

import com.ict.twitter.plantform.LogSys;

public class NodeReporter {
	
	public static Object lock=new Object();
	public static int message_count,message_rel_count,user_count,user_rel_count;
	public  static int Operation=0;
	public static CrawlerServerReporter clr=new CrawlerServerReporter("TwitterAPI");;
	public  synchronized static void count(ReportDataType type){
		switch (type){
		case Message:
			message_count++;
			break;
		case Message_rel:
			message_rel_count++;
			break;
		case User:
			user_count++;
			break;
		case User_rel:
			user_rel_count++;
			break;
		}		
		Operation=Operation+1;
		//数据库汇报
		if(Operation>=1000){			
			doReport(clr,message_count,message_rel_count,user_count,user_rel_count);
			clear();
			System.out.println("数据库读写超过1000次，汇报");
			LogSys.clientLogger.info("数据库读写超过1000次，汇报");
		}else{
			;
		}
			
		
	}
	public synchronized static void clear(){
		Operation=0;
		message_count=0;
		message_rel_count=0;
		user_count=0;
		user_rel_count=0;
	}
	
	public static void ReportCurrentCondition(){
		doReport(clr,message_count,message_rel_count,user_count,user_rel_count);
	}
	public static void doReport(CrawlerServerReporter clr,int message,int message_rel,int user,int user_rel){
		ReportData rd=new ReportData(message,message_rel,user,user_rel,"CRAWLSERVER");
		try {
			clr.doReportIncrementByDataBase(rd);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.err.println("汇报失败");
			e.printStackTrace();
		}	
	}
	
	public static void main(String[] args){
		Runnable run=new Runnable(){
			public void run(){
				while(true){
				NodeReporter.count(ReportDataType.Message);
				NodeReporter.count(ReportDataType.Message_rel);
				
				}
			}
		};
		for(int i=0;i<20;i++){
			Thread t=new Thread(run);
			t.start();
		}
	}

}
