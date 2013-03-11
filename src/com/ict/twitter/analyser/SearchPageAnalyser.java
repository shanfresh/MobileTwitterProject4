package com.ict.twitter.analyser;

import java.util.Vector;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.CrawlerNode.Node;
import com.ict.twitter.Report.*;
public class SearchPageAnalyser extends Analyser {

	public  int CurrentWordListSize=0;
	public static boolean debug=false;
	Vector<TimeLine> relativeTimeLine;
	public SearchPageAnalyser(){
		super();
	}
	public SearchPageAnalyser(Node node){
		super(node);
	}
	
	@Override
	public void doAnalyse() {
		if(this.doc==null){
			LogSys.nodeLogger.error("【doc尚未初始化】");
			return;
		}else{
			LogSys.nodeLogger.debug("【SearchPage分析开始】");
		}
		try{
			AnalyserSearchPage();
		}catch(Exception ex){
			LogSys.nodeLogger.error("SearchPage分析错误"+"保存采集页面信息+\n\r"+ex.getMessage());
			onErrorHappens(ex);			
		}
		
	}
	public static void main(String[] args){
		SearchPageAnalyser spa=new SearchPageAnalyser();
		spa.initaiallizeFromFile("UsefulFile/Testdata/UserSearch.txt","utf8", "/");
		spa.doAnalyse();
	}
	
	
	public void AnalyserSearchPage() throws Exception{
		relativeTimeLine=new Vector<TimeLine>(10);
		Elements all=null;
		try{
			all=doc.getElementsByClass("tweet");
		}catch(NullPointerException e){
			e.printStackTrace();
			return;
		}
		int messageCount=0;
		int tempCount=0;
		for(Element e:all){

			try{
				TimeLine timeline=new TimeLine();
				Element ele=e.getElementsByClass("timestamp").first().select("a").first();
				String id=ele.attr("name");
				String date=ele.ownText();
				String author=e.select("tbody tr td a").first().attr("href").substring(1);
				author=author.substring(0,author.indexOf('?'));
				String content=e.select("tbody tr.tweet-container td ").first().text();
				if(content.length()>100){
					content=content.substring(0, 100);
				}
				content=new String(content.getBytes("utf8"),"utf8");
				LogSys.nodeLogger.debug("作者ID:"+author+"|文本信息是"+content+" 日期是"+date);
				timeline.setId(id.substring(6));				
				timeline.setAuthor(author);
				timeline.setContent(content);
				timeline.setDate(date);
				//timeline.show();
				String sqlStr=timeline.getString();
				boolean res=dbOper.insert(sqlStr,ReportDataType.Message);
				if(res){
					relativeTimeLine.add(timeline);
					messageCount++;
				}
				tempCount++;
			}catch(Exception ex){
				throw ex;
			}
		}
		if(messageCount>0){
			currentNode.ModifyReportMessageByType(ReportDataType.Message, messageCount);

		}
		if(tempCount>=2){
			//currentNode.saveSearchWords(currentTask.targetString);
			CurrentWordListSize=tempCount;
		}
		
		System.out.print("本关键词对应的词条个数【"+all.size()+"】\t");
	}
	public Vector<TimeLine> getRelativeTimeLine() {
		return relativeTimeLine;
	}
	
	//SearchPage负责添加keyUser
	public void addKeyUser(){
		
	}


}
