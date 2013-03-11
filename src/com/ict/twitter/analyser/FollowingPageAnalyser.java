package com.ict.twitter.analyser;

import java.util.Vector;

import org.jsoup.nodes.Element;

import com.ict.twitter.CrawlerNode.Node;
import com.ict.twitter.Report.ReportDataType;
import com.ict.twitter.analyser.beans.*;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;

public class FollowingPageAnalyser extends Analyser{

	public FollowingPageAnalyser(Node currentNode) {
		super(currentNode);
		// TODO Auto-generated constructor stub
	}

	public String followType;
	public Vector<UserRelationship> allUser;

	
	
	
	public static void main(String[] args){
		new Task(TaskType.Following,"networktest1");
		//ClientManager cm = new ClientManager();
		//DefaultHttpClient httpclient = cm.getClient();
		//WebOperation.setLogFile("networktest1_following.html");
		//String tInfluResult=WebOperation.openLink(httpclient,"/networktest1/following");
		//System.out.println("子循环Folloing用户为:"+urlStr);
		FollowingPageAnalyser mp=new FollowingPageAnalyser(null);
		//mp.InitiallizeFromString(tInfluResult);
		mp.initaiallizeFromFile("E:\\TwitterWeb\\ErrorFolder\\Following_networktest2.html", "utf8", "/");
		mp.doAnalyse();

	}
	
	public void doAnalyse() {
		// TODO Auto-generated method stub
		if(this.doc==null){
			System.out.println("doc尚未初始化");
			return;
		}else{
			System.out.println("用户Following分析开始");
		}
		try{
			AnalyseUsers();
		}catch(Exception ex){
			LogSys.nodeLogger.error("FollowingPage分析错误");
			onErrorHappens(ex);			
		}
	}
	public void AnalyseUsers() throws Exception{
		
		allUser=new Vector<UserRelationship>();   
		//获取user a
		UserRelationship userRel;
		Element userAthor=doc.getElementsByClass("user-header").first();
		String auth;
		if(userAthor!=null){	
			auth=userAthor.select("table tbody tr td.info a").first().attr("href");
		}else{			
			auth=doc.getElementsByClass("info").first().child(0).attr("href");
		}
		System.out.println("auth:"+auth);
		if(auth!=null&&auth.startsWith("/")){
			auth=auth.substring(1);
		}
		int userCount=0;int userRelCount=0;
		for(Element ele:doc.getElementsByClass("user-item")){
			try{
				String name=ele.select("tr td[class] a[href]").first().attr("href");
				name=name.substring(1,name.indexOf('?'));
				userRel=new UserRelationship();
				userRel.setUser_A(auth);
				userRel.setUser_B(name);
				userRel.setLinkType(followType);				
				String sqlStr=userRel.getString();
				if(dbOper.insert(sqlStr,ReportDataType.User_rel)){
					userRelCount++;
					allUser.add(userRel);
				}
				
				TwiUser user=new TwiUser();
				user.setName(name);
				user.setSummarized("");
				user.setAliasName(name);
				user.setFollowers(0);
				user.setFollowing(0);
				String sql=user.getString();
				if(dbOper.insert(sql,ReportDataType.User)){
					userCount++;
				}			
			}			
			catch(Exception e){
				throw e;
			}
		}
		if(userCount>0){
			currentNode.ModifyReportMessageByType(ReportDataType.User, userCount);
		}
		if(userRelCount>0){
			currentNode.ModifyReportMessageByType(ReportDataType.User_rel, userRelCount);
		}
		

		
		
	}
	public Vector<UserRelationship> getAllUser() {
		return allUser;
	}
	
	public void SetFollowType(String followType)
	{
		this.followType=followType;
	}

}
