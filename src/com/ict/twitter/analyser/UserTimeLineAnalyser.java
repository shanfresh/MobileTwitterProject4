package com.ict.twitter.analyser;
import java.util.Vector;

import org.apache.log4j.PropertyConfigurator;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.analyser.beans.MessageRelationship;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.CrawlerNode.AjaxNode;
import com.ict.twitter.CrawlerNode.Node;
import com.ict.twitter.Report.*;
public class UserTimeLineAnalyser extends Analyser{


	public UserTimeLineAnalyser(Node currentNode) {
		super(currentNode);
		// TODO Auto-generated constructor stub
	}
	@Override
	public void doAnalyse() {
		if(this.doc==null){
			System.out.println("doc尚未初始化");
			return;
		}else{
			System.out.print("主页分析开始");
		}
		try{
			AnalyseHomeTimeLine();
		}catch(Exception ex){
			LogSys.nodeLogger.error("TimeLine采集错误"+"保存采集页面信息");
			onErrorHappens(ex);
			
		}

	}
	public static void main(String[] args){
		//ContentModify("!!!!!!!");
		Task task=new Task();
		task.setOwnType(TaskType.TimeLine);
		task.setTargetString("shanjixi");
		Node node=new AjaxNode("Test",null);
		UserTimeLineAnalyser spa=new UserTimeLineAnalyser(node);
		spa.SetTask(task);
		if(spa.initaiallizeFromFile("E:/TwitterWeb/ErrorFolder/Following_31710972.html","utf8", "/"))
			spa.doAnalyse();
		else 
			System.out.println("初始化失败");
	}
	
	
	
	public void AnalyseHomeTimeLine(){
		Elements HomeTimeLineElement = null;
		try{
			HomeTimeLineElement=doc.getElementsByClass("tweet");
			System.err.print(" 用户推文大小"+HomeTimeLineElement.size());
		}catch(Exception e){
			System.err.println("没有推文发表");
			return;
		}
		Vector<TimeLine> homeTimeLines=new Vector<TimeLine>();
		Vector<MessageRelationship> messageRes=new Vector<MessageRelationship>();
		System.out.print("\t 推文数是"+HomeTimeLineElement.size());
		int timeLineCount=0,messageRelCount=0;
		for(Element t:HomeTimeLineElement){
			TimeLine timelineEle=new TimeLine();
			MessageRelationship messageReEle=new MessageRelationship();
			
			Element timeStamp=t.getElementsByClass("timestamp").first();
			
			if(timeStamp==null){
				LogSys.nodeLogger.error("找不到TimeStamp标签"+t.toString());
			}
			String tweet_id=timeStamp.child(0).attr("name");
			String time=timeStamp.child(0).ownText();
			String author=t.select("tbody tr td a").first().attr("href").substring(1);
			if(author.indexOf('?')>=0)
				author=author.substring(0,author.indexOf('?'));
			String content=t.getElementsByClass("tweet-container").first().text();
			if(tweet_id.startsWith("tweet")){
				tweet_id=tweet_id.substring(6);
			}
			
			
			timelineEle.setId(tweet_id);
			timelineEle.setAuthor(author);			
			timelineEle.setDate(time);
			timelineEle.setContent(content);										
			homeTimeLines.add(timelineEle);		
			String sqlStr=timelineEle.getString();
			if(dbOper.insert(sqlStr,ReportDataType.Message)){
				timeLineCount++;
			}
			
			Element RTEle=t.select("tbody tr.tweet-container td div.context ").first();
			
			if(RTEle!=null)				
			{
				Element href=RTEle.children().first();
				messageReEle.setId_A(href.attr("href"));
				messageReEle.setId_B(tweet_id);
				messageReEle.setrelation(RTEle.text());	
				messageRes.add(messageReEle);				
				sqlStr=messageReEle.getString();
				if(dbOper.insert(sqlStr))
					messageRelCount++;
				
			}else{
				
			}
			
		}
		if(timeLineCount>0){
			currentNode.ModifyReportMessageByType(ReportDataType.Message, timeLineCount);
		}
		if(messageRelCount>0){
			currentNode.ModifyReportMessageByType(ReportDataType.Message_rel, messageRelCount);
		}		

				
	}
	public static void ContentModify(String content){
		PropertyConfigurator.configure ("config/log4j_Main.properties" ) ;
		String test="性子野了？ RT @k_kica: 大叔说两天没见，我又变得跟小野驴似的???";
		String tweet_id="00000000000011";
		String author="SHANJIXI";
		String time="2012-10-15 00:00:00";
		TimeLine timelineEle=new TimeLine();
		timelineEle.setId(tweet_id);
		timelineEle.setAuthor(author);			
		timelineEle.setDate(time);
		timelineEle.setContent(test);											
		String sqlStr=timelineEle.getString();
		DbOperation dbOper=new DbOperation();
		if(dbOper.insert(sqlStr,ReportDataType.Message)){
			System.out.println("执行完毕");
		}else{
			System.out.println("执行错误");
		}
		
				
		
	}	
	
}
