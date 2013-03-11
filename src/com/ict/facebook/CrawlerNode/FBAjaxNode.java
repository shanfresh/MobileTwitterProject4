package com.ict.facebook.CrawlerNode;

import com.ict.facebook.FBAjaxMainSearch;
import com.ict.twitter.CrawlerNode.AjaxNode;
import com.ict.twitter.tools.DbOperation;

public class FBAjaxNode extends AjaxNode {

	public FBAjaxNode(String name, DbOperation dbOper) {
		super(name, dbOper);
	}
	
	@Override
	protected void startMainSearch(){
		FBAjaxMainSearch ms=new FBAjaxMainSearch("FBAjaxMainSearch-"+this.NodeName+"",this);
		Thread mainThread=new Thread(ms);
		mainThread.setName("FBAjaxMainSearch"+this.NodeName+"");
		mainThread.start();			
	}

	public static void main(String[] args) {
		

	}

}
