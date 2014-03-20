package com.ict.twitter;

import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.analyser.beans.UserProfile;
import com.ict.twitter.analyser.filter.TimeTransformer;

public class AjaxProfileAnalyserExtend {
	protected TimeTransformer timeTrans=new TimeTransformer();
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	public void doAnylyze(String content,Vector<TimeLine> vectTime) throws Exception{
		Document doc=Jsoup.parse(content, "/");
		Elements allTweet=doc.getElementsByAttributeValueStarting("class", "recent-tweets clearfix");
		if(allTweet==null||allTweet.size()==0){
			throw new Exception("主要的OL没有找到");
		}
		Element root=allTweet.first();
		Elements tweets=root.children();
		if(tweets.size()>0){
			for(Element t:tweets){
				Element firstDiv=t.children().first();
				String tweet_id=firstDiv.attr("data-tweet-id");
				String user_name=firstDiv.attr("data-screen-name");
				String user_aliasname=firstDiv.attr("data-name");
				String user_id=firstDiv.attr("data-user-id");
				Element time=t.getElementsByAttributeValueStarting("class", "_timestamp js-short-timestamp").first();
				String timeStr=time.attr("data-time");
				timeStr=timeTrans.Convert(timeStr);
				Element TweetcontentEle=t.getElementsByAttributeValue("class", "js-tweet-text tweet-text").first();
				String Content=TweetcontentEle.text();
				TimeLine timeline=new TimeLine(tweet_id,user_name,Content,timeStr);
				vectTime.add(timeline);				
			}
			
		}
		
	}
}
