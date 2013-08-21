package com.ict.twitter.MessageBus;

public interface MessageBusNames {
	public static final String ControlC2S="CONTROLC2S";
	public static final String ControlS2C="CONTROLS2C";
	public static final String CrawlInfoC2S="CRAWLINFOC2S";
	public static final String KeyID="KEYID";
	public static final String NormalID="NORMALID";
	public static final String ReportTwitterWEB="REPORTTWITTERWEB";
	public static final String ReportTwitterAPI="REPORTTWITTERAPI";
	
	public static final String UrgentTask="UrgentTask"; //最高优先级4
	public static final String KeyWordAndTopicTask="KeyWordAndTopicTask"; //优先级3
	public static final String KeyUserTask="KeyUserTask";//针对重点账户 //优先级2

	
	public static final String Task="TASK";//普通优先级优先级1
	
	
	public String[] names={ControlC2S,ControlS2C,CrawlInfoC2S,KeyID,NormalID,ReportTwitterWEB,ReportTwitterAPI,UrgentTask,KeyWordAndTopicTask,KeyUserTask,Task};
	

}
