package com.ict.twitter.CrawlerSchedul;

import java.sql.SQLException;

import com.ict.twitter.CrawlerServer.CrawlerServer;
import com.ict.twitter.DAO.DBInputTaskDAO;
import com.ict.twitter.plantform.LogSys;
import com.mongodb.DBCollection;

public class PlantScheduler extends BasicScheduler{
	final int  MaxID=17;
	public PlantScheduler(CrawlerServer _crawlserver) {
		super(_crawlserver);
		// TODO Auto-generated constructor stub
	}
	
	
	/*1:检查消息总线是否为空，如果不为空的话返回。
	 *2:获取所有的关键用户，检查上次爬取时间
	 * 
	 * @see com.ict.twitter.CrawlerSchedul.BasicScheduler#run()
	 */
	
	public void ResetInputTaskTable(){
		DBInputTaskDAO inputtask=new DBInputTaskDAO();
		try {
			inputtask.InitTable();
			CrawlServerScheduler cs=new CrawlServerScheduler(this.crawlserver);
			cs.checkUserInsert();
			LogSys.crawlerServLogger.info("定时器刷新InputTask表");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
	@Override
	public void run() {
		ResetInputTaskTable();
		
	}
	

}
