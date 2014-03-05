package com.ict.twitter;

import java.io.IOException;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.AjaxAnalyser;
import com.ict.twitter.Report.ReportData;
import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.hbase.MessageTwitterHbase;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxSearchAnalyser extends AjaxAnalyser {

	
	public AjaxSearchAnalyser(MulityInsertDataBase batchdb, Task task) {
		super(batchdb, task);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public AnalyserCursor doAnalyse (String src,Vector<TwiUser> users,ReportData reportData) throws AllHasInsertedException,Exception{
		Document doc=Jsoup.parse(src, "/");
		Elements follows=doc.getElementsByAttributeValueStarting("class","js-stream-item stream-item stream-item expanding-stream-item");
		
		int j=1;
		String tweetID="";
		Vector<TimeLine> timelines = new Vector<TimeLine>();
		for(Element ele:follows){
			if(ele.children()!=null){
				Element firstChildren=ele.children().first();
				//一串数字
				tweetID=firstChildren.attr("data-tweet-id");
				//唯一标示符
				String userID=firstChildren.attr("data-screen-name");
				String content;
				String date;
				try{
					content=firstChildren.getElementsByAttributeValue("class", "js-tweet-text tweet-text").first().text();			
				}catch(NullPointerException ex){
					content="null";
				}
				try{
					date=firstChildren.getElementsByAttributeValueStarting("class", "_timestamp js-short-timestamp").first().attr("data-time");
					date=timeTrans.Convert(date);
				}catch(NullPointerException ex){
					date="null";
				}

				TwiUser user=new TwiUser();
				user.setName(userID);
				user.setAliasName(userID);
				user.setFollowers(0);
				user.setFollowing(0);
				users.add(user);
				timelines.add(new TimeLine(tweetID,userID,content,date));
				LogSys.nodeLogger.debug(""+(j++) +" tweetID:"+tweetID+" date:"+date+" userID: "+userID+"content: "+content);
				
			};			
		}
		TimeLine[] TimeLineArray = new TimeLine[timelines.size()];
		timelines.toArray(TimeLineArray);
		if(super.isdebug){
			for(int i=0;i<TimeLineArray.length;i++)
				TimeLineArray[i].show();
		}
		for(int i=0;i<TimeLineArray.length;i++){
			TimeLineArray[i].setMainTypeID(task.getMainTypeID());
			TimeLineArray[i].setTaskTrackID(task.getTaskTrackID());
		}
		//hbase
		//((MessageTwitterHbase)hbase).InsertIntoTable(TimeLineArray);
		super.batchdb.insertIntoMessage(TimeLineArray);
		
		AnalyserCursor res=new AnalyserCursor(tweetID,follows.size());
		reportData.message_increment+=timelines.size();
		reportData.user_increment+=follows.size();
		return res;
	}

}
