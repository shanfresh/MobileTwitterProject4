package com.ict.twitter.CrawlerServer;

import java.util.TimerTask;

public class NodeStatusShow extends TimerTask {
	NodeManager nodeManager;
	public NodeStatusShow(NodeManager _nodeManager){
		nodeManager=_nodeManager;
	}
	@Override
	public void run() {
		nodeManager.show();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
