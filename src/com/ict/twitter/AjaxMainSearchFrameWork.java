package com.ict.twitter;

import org.apache.http.impl.client.DefaultHttpClient;
//定义了采集节点的基本属性和类型

import com.ict.twitter.CrawlerNode.AjaxNode;
import com.ict.twitter.tools.DbOperation;


public abstract class AjaxMainSearchFrameWork implements Runnable {	
	//采集基本类
	protected String Name;
	protected DbOperation DBOp;
	
	
	//基本的操作属性
	protected DefaultHttpClient httpclient;
	protected WebOperationAjax webOperation;
	protected AjaxNode node;
	public abstract void run();
	public abstract void doWork();
	

}
