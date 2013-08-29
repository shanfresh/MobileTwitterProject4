package com.ict.twitter.CrawlerServer;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.ict.twitter.StatusTrack.CrawlUserDB;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.ReadTxtFile;
import com.ict.twitter.tools.SaveTxtFile;
public class ServerBean implements Serializable{
	private static final long serialVersionUID = -9133824015130047653L;
	public static String aname="~~~~";
	public static boolean isFirstChuizhi=true;
	CrawlUserDB crawluser=new CrawlUserDB();
	List<NormalUser> normalUserList=Collections.synchronizedList(new UserList<NormalUser>());
	List<NormalUser> keyUsers=Collections.synchronizedList(new UserList<NormalUser>());		
	//初始化关键词搜索
	public void InitSearch(String file,int max,CrawlerServer server){
		BufferedReader br = null;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file),"utf-8"));
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			String t;
			int i=0;
			while((t=br.readLine())!=null){
				Task task=new Task();
				task.setOwnType(TaskType.Search);
				task.setTargetString(t);
				server.addKeyWord(task);				
				i++;
				if(i>max){
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//准备初始垂直采集的任务加入
	public void InitChuizhi(String file,CrawlerServer server,boolean isFirst){
		Vector<String> res=new Vector<String>();
		if(isFirst){
			server.isFirstChuiZhi=false;
			ReadTxtFile rxf=new ReadTxtFile(file);
			res=rxf.read();			
			
		}
		
		//加入KeyWordsSearch发现的关键用户		
		for(NormalUser nu:keyUsers){
			res.add(nu.userID);
		}			
		for(String t:res){			
			Task task=new Task();
			task.setOwnType(TaskType.TimeLine);
			task.setTargetString(t);
			server.addKeyUserTask(task);
			
			task=new Task(); 
			task.setOwnType(TaskType.Following);
			task.setTargetString(t);
			server.addKeyUserTask(task);
			
			task=new Task();
			task.setOwnType(TaskType.Followers);
			task.setTargetString(t);
			server.addKeyUserTask(task);
			
			task=new Task();
			task.setOwnType(TaskType.About);
			task.setTargetString(t);
			server.addKeyUserTask(task);	
		}
		
		
		
	}
	
	
	
	//初始化并行采集不要一直的生产要进行间隔~~
	//2013-03-07 消除了i+=3的致命bug,采集量会上升
	//NormalUserList size 何故等于0？？！！！
	public int InitBingxing(CrawlerServer server,int deepth){
		LogSys.crawlerServLogger.debug("CrawlerServer NormalUserList Size:"+normalUserList.size());
		for(int i=0;i<normalUserList.size();i++){
			if(i%100==0){
				try {
					LogSys.crawlerServLogger.info("当前进行第"+i+"组数据导入的NormalUser总线,共["+normalUserList.size()+"]");
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			NormalUser nu=normalUserList.get(i);
			Task task=new Task();
			task.setTargetString(nu.userID);
			task.setOwnType(TaskType.TimeLine);
			server.addTask(task);
			task.setOwnType(TaskType.Following);
			server.addTask(task);
			task.setOwnType(TaskType.Followers);
			server.addTask(task);
			task.setOwnType(TaskType.About);
			server.addTask(task);
		}
		int size=normalUserList.size();
		normalUserList.clear();		
		return size;

		
	}//initBingxing() Ends
	
	
	/*
	 * keyUser.contains(user) 值得商榷？？
	 */
	public void addNormalUser(NormalUser user,int deepth){
		//如果种子用户列表中已经存在。 
		if(keyUsers.contains(user)){
			return ;
		}		
		normalUserList.add(user);
		crawluser.insertUserItem(user.userID, -1, false, null, deepth);		
	} 
	public void showNormalUserList(){
		for(NormalUser u:normalUserList){
			System.out.print(u.userID+":"+u.sum+"\t");
		}
		System.out.println();
	}
	
	public void addKeyUser(NormalUser user){
		//如果种子用户列表中已经存在。
		if(keyUsers.contains(user)){
			return ;
		}
		crawluser.insertUserItem(user.userID, -1, true, null, 0);	
		keyUsers.add(user);
		LogSys.crawlerServLogger.debug("添加新的种子用户到Vector User"+user);
		
		
	} 
	
	


	public static void ADDKEYUSER(ServerBean sb, NormalUser nu) {
		sb.addKeyUser(nu);
		
	}
	
}
