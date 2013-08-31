package com.ict.twitter.CrawlerSchedul;

import java.util.TimerTask;

import com.ict.twitter.CrawlerServer.CrawlerServer;

public abstract class BasicScheduler extends TimerTask {
	protected CrawlerServer crawlserver;
	
	public BasicScheduler(CrawlerServer _crawlserver){
		this.crawlserver=_crawlserver;
	}

	@Override
	public abstract void run();

}
