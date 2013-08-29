package com.ict.twitter.CrawlerNode;

import java.util.TimerTask;

public class NodeReport extends TimerTask {
	Node node;
	public NodeReport(Node _node){
		node=_node;
	}
	@Override
	public void run() {
		node.nodeReportToCrawlServer();
		
	}

}
