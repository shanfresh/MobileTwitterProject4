package com.ict.twitter.CrawlerNode;

import com.ict.twitter.AjaxMainSearch;
import com.ict.twitter.tools.DbOperation;

public class AjaxNode extends Node{


	public AjaxNode(String name, DbOperation dbOper) {
		super(name, dbOper);

	}
	@Override
	protected void startMainSearch(){
		AjaxMainSearch ms=new AjaxMainSearch("AjaxMainSearch-"+this.NodeName+"",this);
		Thread mainThread=new Thread(ms);
		mainThread.setName("AjaxMainSearchThread-"+this.NodeName+"");
		mainThread.start();			
	}
}
