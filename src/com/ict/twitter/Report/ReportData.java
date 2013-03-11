package com.ict.twitter.Report;

import java.io.Serializable;

public class ReportData implements Serializable{
	private static final long serialVersionUID = -4374684703587855118L;
	public int message_increment;
	public int message_rel_increment;
	public int user_increment;
	public int user_rel_increment;
	public String NodeName;
	public ReportData(){
		
	}
	public ReportData(int messageIncrement, int messageRelIncrement,
			int userIncrement, int userRelIncrement, String NodeName) {
		super();
		message_increment = messageIncrement;
		message_rel_increment = messageRelIncrement;
		user_increment = userIncrement;
		user_rel_increment = userRelIncrement;
		this.NodeName=NodeName;
	}
	public void add(ReportData newReportData){
		this.message_increment+=newReportData.message_increment;
		this.message_rel_increment+=newReportData.message_rel_increment;
		this.user_increment+=newReportData.user_increment;
		this.user_rel_increment+=newReportData.user_rel_increment;
	}
	
}
