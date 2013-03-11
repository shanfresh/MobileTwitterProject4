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
 * 1����Ϣ����ȡ��˳��һ��
 * 2��û�н���Ϣ�洢����
 * 3��ѭ�������ķ�������ȷ�� * 
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
			LogSys.nodeLogger.error(node.NodeName+"MainSearch������������");
			LogSys.nodeLogger.error(ex.getMessage());
			ex.printStackTrace();
			System.out.println("MainSearch�˳��ˣ���Ҫע��");
			System.exit(0);
		}		
	}
	
	public void stop(){
		working=false;
		try{
			httpclient.getConnectionManager().shutdown();
		}catch(Exception e){
			System.out.println("�رտͻ���");
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
			//����һ��������Ϣ��
			node.SendHeartBeat();
			try{
				one=node.getTask();
				while(one==null){
					one=node.getTask();
					pause(1000);
					System.out.println("��ǰ�ڵ�["+node.NodeName+"]��⵽�ɼ�����Ϊ��");
					continue;
				}				
				//����ҳ���һ�ȡ��Ӧ����ҳ���ݣ���վ��ַ��one.getURL();
				
				final String targetUrl=one.getURL();
				LogSys.nodeLogger.info("���Դ���ҳ"+targetUrl);
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
					LogSys.nodeLogger.error("��ҳ����Ϊ��,��ҳ��С�쳣"+one.getURL());
					continue;
				}
				if(one.ownType==TaskType.About){
					System.out.println("�򿪹���ҳ��");
					WebOperation.setLogFile("UserAbout.txt");	
					String UserTimeLineResult=WebPageContent;
					aboutAna.SetTask(one);
					aboutAna.InitiallizeFromString(UserTimeLineResult);
					//aboutAna.doAnalyse();
					System.out.println("�û���ҳ��������");				
				}
				else if(one.ownType==TaskType.TimeLine){
					System.out.print("���û���ҳ");
					WebOperation.setLogFile("UserTimeLine.html");
					System.out.print("�û������ַ��"+one.getURL());
					String UserTimeLineResult=WebPageContent;
					userAna.SetTask(one);
					userAna.InitiallizeFromString(UserTimeLineResult);
					userAna.doAnalyse();
					System.out.println("�û���ҳ��������");				
				}else if(one.ownType==TaskType.Following){
					System.out.println("��Following");
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
					LogSys.nodeLogger.debug("��������ط�NormalUserJms"+jmsStr);					
					node.addNomalUserIDs(jmsStr);
				}else if(one.ownType==TaskType.Followers){
					System.out.print("���û�Followers��˿");
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
					LogSys.nodeLogger.info("��������ط�NormalUserJms"+jmsStr);
					node.addNomalUserIDs(jmsStr);
					System.out.println("�û�Followers��˿��˿��������");
					
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
					System.out.println("SearchPage��������");

				}
			}catch(Exception ex){
				
			}
			
				
		}
		System.out.println("�ɼ�������");
				
	}	
	
	public void doKeyWordsSearch(){
		
	}
	public static String Ask(String question){
		System.out.println("��ѯ�ʡ�"+question);
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
