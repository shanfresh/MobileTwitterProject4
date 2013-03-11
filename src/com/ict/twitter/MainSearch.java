package com.ict.twitter;

import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.client.DefaultHttpClient;
import com.ict.twitter.CrawlerNode.Node;
import com.ict.twitter.analyser.AboutPageAnalyser;
import com.ict.twitter.analyser.FollowingPageAnalyser;
import com.ict.twitter.analyser.SearchPageAnalyser;
import com.ict.twitter.analyser.UserTimeLineAnalyser;
import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.analyser.beans.UserRelationship;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.BasePath;
import com.ict.twitter.tools.SavitchIn;
/*
 * 1：消息队列取得顺序不一致
 * 2：没有将信息存储下来
 * 3：循环遍历的方法不正确。 * 
 * 
 */
public class MainSearch implements Runnable {
	TwitterClientManager cm;
	WebOperation webOperation;
	DefaultHttpClient httpclient;
	Node node;
	boolean working=true;
	public static int sleepCount;
	Object lock=new Object();
	public boolean isSleep=false;

	public static void main(String[] args){

	}
	
	
	public MainSearch(boolean isProxy){
		cm=new TwitterClientManager();
		
		if(isProxy){
			httpclient=cm.getClient();
		}else{
			httpclient=cm.getClientNoProxy();
		}
		webOperation=new WebOperation();
		TwitterLoginManager lm=new TwitterLoginManager(httpclient);
		lm.doLogin();
	}
	public MainSearch(Node _node,boolean isProxy){
		this(isProxy);
		this.node=_node;
	}
	public void initAnalyse(){

	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		working=true;
		try{
			doSearch();
		}catch(Exception ex){
			LogSys.nodeLogger.error(node.NodeName+"MainSearch发生致命错误");
			LogSys.nodeLogger.error(ex.getMessage());
			ex.printStackTrace();
			System.out.println("MainSearch退出了，需要注意");
			System.exit(0);
		}		
	}
	
	public void stop(){
		working=false;
		try{
			httpclient.getConnectionManager().shutdown();
		}catch(Exception e){
			System.out.println("关闭客户端");
			e.printStackTrace();
		}
	}
	public void doSearch(){
		String base=BasePath.getBase();
		String WebPageContent="";
		Task one;
		AboutPageAnalyser aboutAna=new AboutPageAnalyser(node);
		UserTimeLineAnalyser userAna=new UserTimeLineAnalyser(node);
		FollowingPageAnalyser followingAna=new FollowingPageAnalyser(node);
		FollowingPageAnalyser followerAna2=new FollowingPageAnalyser(node);
		SearchPageAnalyser spa=new SearchPageAnalyser(node);
		while(working){
			//发送一次心跳信息。
			node.SendHeartBeat();
			try{
				one=node.getTask();
				while(one==null){
					one=node.getTask();
					pause(1000);
					System.out.println("当前节点["+node.NodeName+"]检测到采集任务为空");
					continue;
				}				
				//打开网页并且获取对应的网页内容，网站地址：one.getURL();
				
				final String targetUrl=one.getURL();
				LogSys.nodeLogger.info("尝试打开网页"+targetUrl);
				ExecutorService service = Executors.newCachedThreadPool();
				Future<String> future = service.submit(new Callable<String>(){
					 public String call() throws Exception {
						try{
							return WebOperation.openLink(httpclient,targetUrl);
						}catch(Exception ex){
							ex.printStackTrace();
						}
						 return ""; 						 
					 } 
				});
				try {  
					WebPageContent=(String) future.get(10000, TimeUnit.MILLISECONDS);  
		        } catch (Exception e) {  
		        	LogSys.nodeLogger.error("OpenURL Error");  
		            e.printStackTrace(); 
		        }  
				if(WebPageContent.length()<=20){
					LogSys.nodeLogger.error("网页内容为空,网页大小异常"+one.getURL());
					continue;
				}
				if(one.ownType==TaskType.About){
					System.out.println("打开关于页面");
					WebOperation.setLogFile("UserAbout.txt");	
					String UserTimeLineResult=WebPageContent;
					aboutAna.SetTask(one);
					aboutAna.InitiallizeFromString(UserTimeLineResult);
					//aboutAna.doAnalyse();
					System.out.println("用户主页分析结束");				
				}
				else if(one.ownType==TaskType.TimeLine){
					System.out.print("打开用户主页");
					WebOperation.setLogFile("UserTimeLine.html");
					System.out.print("用户网络地址："+one.getURL());
					String UserTimeLineResult=WebPageContent;
					userAna.SetTask(one);
					userAna.InitiallizeFromString(UserTimeLineResult);
					userAna.doAnalyse();
					System.out.println("用户主页分析结束");				
				}else if(one.ownType==TaskType.Following){
					System.out.println("打开Following");
					WebOperation.setLogFile("Following.txt");
					String FloResult=WebPageContent;
					followingAna.SetTask(one);				
					followingAna.InitiallizeFromString(FloResult);				
					followingAna.SetFollowType("following");				
					followingAna.doAnalyse();								
					String jmsStr=new String("");
					for(UserRelationship item:followingAna.getAllUser()){					
						StringBuffer sb=new StringBuffer();
						String name=item.user_B;
						sb.append("<name>"+name+"</name>");
						int sum=1000;
						sb.append("<sum>"+sum+"</sum>");		
						String str=sb.toString();
						jmsStr=jmsStr+str;
					}
					String count=Integer.toString(followingAna.getAllUser().size());
					jmsStr="<count>"+count+"</count>"+jmsStr;
					LogSys.nodeLogger.debug("向服务器回发NormalUserJms"+jmsStr);					
					node.addNomalUserIDs(jmsStr);
				}else if(one.ownType==TaskType.Followers){
					System.out.print("打开用户Followers粉丝");
					WebOperation.setLogFile("Following.txt");	
					String FlowerResult=WebPageContent;
					followerAna2.SetTask(one);
					followerAna2.InitiallizeFromString(FlowerResult);
					followerAna2.SetFollowType("follower");
					followerAna2.doAnalyse();
					String jmsStr="";
					for(UserRelationship item:followerAna2.getAllUser()){
						StringBuffer sb=new StringBuffer();
						String name=item.user_B;
						sb.append("<name>"+name+"</name>");
						int sum=1000;
						sb.append("<sum>"+sum+"</sum>");		
						String str=sb.toString();
						jmsStr=jmsStr+str;													
					}
					String count=Integer.toString(followerAna2.getAllUser().size());
					jmsStr="<count>"+count+"</count>"+jmsStr;
					LogSys.nodeLogger.info("向服务器回发NormalUserJms"+jmsStr);
					node.addNomalUserIDs(jmsStr);
					System.out.println("用户Followers粉丝粉丝分析结束");
					
				}else if(one.ownType==TaskType.Search){
//					long tstart=System.currentTimeMillis();
					WebOperation.setLogFile("UserSearch.txt");				
					String searchPageResult=WebPageContent;
					spa.SetTask(one);
					spa.InitiallizeFromString(searchPageResult);
					spa.doAnalyse();
					Vector<TimeLine> res=spa.getRelativeTimeLine();
					StringBuffer sb=new StringBuffer();
					for(TimeLine t:res){
						sb.append("<name>");
						sb.append(t.getAuthor());
						sb.append("</name>");
					}
					node.addKeyUserIDs(sb.toString());				
					System.out.println("SearchPage分析结束");

				}
			}catch(Exception ex){
				
			}
			
				
		}
		System.out.println("采集结束了");
				
	}	
	
	public void doKeyWordsSearch(){
		
	}
	public static String Ask(String question){
		System.out.println("【询问】"+question);
		String command=SavitchIn.readLine();
		return command;
	}
	public void pause(int count) {
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
