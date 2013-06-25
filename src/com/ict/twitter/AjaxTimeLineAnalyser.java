package com.ict.twitter;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.*;

import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;


public class AjaxTimeLineAnalyser extends AjaxAnalyser{





	public AjaxTimeLineAnalyser(MulityInsertDataBase batchdb) {
		super(batchdb);
		// TODO Auto-generated constructor stub
	}

	public AnalyserCursor doAnalyser(String src){
		Document doc=Jsoup.parse(src, "/");
		AnalyserCursor result=new AnalyserCursor();
		//doc.getelementsby
		Elements twitterMessages=doc.getElementsByAttributeValue("class","js-stream-item stream-item stream-item expanding-stream-item");
		if(twitterMessages.size()<=10){
			System.out.println("[Warning] twitter count"+twitterMessages.size());
		}
		Vector<TimeLine> vector = new Vector<TimeLine>();
		
		for(Element t:twitterMessages){
			try{
				Element content=t.getElementsByAttributeValue("class", "js-tweet-text tweet-text").first();
				Element time=t.getElementsByAttributeValue("class", "tweet-timestamp js-permalink js-nav").first();
				String timeStr=time.attr("title");
				
				Element firstDiv=t.children().first();
				String tweet_id=firstDiv.attr("data-tweet-id");
				//UserIDNO
				//String user_id=firstDiv.attr("data-user-id");
				String user_name=firstDiv.attr("data-name");				
				result.lastID=tweet_id;
				//System.out.println(tweet_id+" "+user_id+" "+user_name);
				vector.add(new TimeLine(tweet_id,user_name,content.ownText(),timeStr));
			}catch(NullPointerException ex){
				ex.printStackTrace();
			}			
		}
		TimeLine[] timelines = new TimeLine[vector.size()];
		vector.toArray(timelines);
		super.batchdb.insertIntoMessage(timelines);
		result.size=twitterMessages.size();
		return result;
		
	}
}
