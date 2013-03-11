package com.ict.twitter.analyser;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.CrawlerNode.Node;
import com.ict.twitter.analyser.beans.TwiUser;
public class AboutPageAnalyser extends Analyser {

	
	public AboutPageAnalyser(Node currentNode) {
		super(currentNode);
	}
	public TwiUser TwiUser;

	@Override
	public void doAnalyse() {
		if(doc==null){
			System.out.println("Not ��ʼ������");
			return;
		}
		TwiUser=new TwiUser();
		AnalyseUserInfo();
		AnalyseOtherUserInfo();
		TwiUser.show();
		
		String sqlStr=TwiUser.getString();	
		dbOper.insert(sqlStr);
				
	}
	

	public void AnalyseUserInfo(){
		Element userAthor=doc.getElementsByClass("timeline-user-friend").first();
		//System.out.println(userAthor.outerHtml());
		String imageUrl=doc.getElementsByClass("list-tweet-img").first().attr("src");
	
		String auth=userAthor.getElementsByTag("strong").first().text();
		String AliasName=userAthor.getElementsByTag("strong").first().parent().ownText();
		int[] followingAndFollower=new int[2];
		int i=0;
		for(Element t:userAthor.getElementsByClass("timeline-following").first().getElementsByTag("a")){
			String res=t.text();
			if(res.indexOf(":")==-1){
				followingAndFollower[i++]=Integer.parseInt("0");
				continue;
			}
			res=res.substring(res.indexOf(":")+1,res.length());
			try{
				followingAndFollower[i++]=Integer.parseInt(res);
			}catch(Exception e){
				followingAndFollower[i]=0;
				
			}
			
		}
		TwiUser.setName(auth);
		TwiUser.setAliasName(AliasName);
		TwiUser.setFollowing(followingAndFollower[0]);
		TwiUser.setFollowers(followingAndFollower[1]);
		TwiUser.setProfileImageUrl(imageUrl);
		
		
	}
	public void AnalyseOtherUserInfo(){		
		Elements timelinefriendInfo=doc.getElementsByClass("timeline-friend").first().getElementsByTag("p");
		String allinfo[]=new String[3];
		for(int i=0;i<allinfo.length;i++){
			allinfo[i]=timelinefriendInfo.get(i).ownText();
		}
		TwiUser.setLocation(allinfo[0]);
		TwiUser.setSummarized(allinfo[1]);
		TwiUser.setWebsite(allinfo[2]);		
	}


}
