package com.ict.twitter.DatabaseBean;

import com.ict.twitter.CrawlerServer.InputStatus;
import com.ict.twitter.CrawlerServer.InputType;

public class InputTaskBean {
	public int ID;
	public String TaskName,TaskParameter,TaskParameter2;
	public InputType InputType;
	public int TaskWeight,MessageRet,UserRet,UserRelRet;
	public InputStatus Status;
	public boolean ResultFlag;
}
