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

import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.ReadTxtFile;
import com.ict.twitter.tools.SaveTxtFile;
public class ServerBean implements Serializable{
	private static final long serialVersionUID = -9133824015130047653L;
	public static String aname="~~~~";
	public static boolean isFirstChuizhi=true;
	public static SaveTxtFile tmpKeyId=new SaveTxtFile("UsefulFile/tmpKeyIDs.txt",false);
	public static SaveTxtFile tmpNormalId=new SaveTxtFile("UsefulFile/NormalIDs.txt",false);
	List<NormalUser> normalUserList=Collections.synchronizedList(new UserList<NormalUser>());
	List<NormalUser> keyUsers=Collections.synchronizedList(new UserList<NormalUser>());	
	boolean isdebug=true;
	
	//初始化关键词搜索
	public void InitSearch(String file,CrawlerServer server){
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
				server.addTask(task);				
				i++;
				if(isdebug&&i>=2){
					break;
				}
			}
			LogSys.crawlerServLogger.info(isdebug+"【Server】总共新加的关键词数"+i+"个");
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
			server.addTask(task);
			
			task=new Task();
			task.setOwnType(TaskType.Following);
			task.setTargetString(t);
			server.addTask(task);
			
			task=new Task();
			task.setOwnType(TaskType.Followers);
			task.setTargetString(t);
			server.addTask(task);			
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
					LogSys.crawlerServLogger.info("当前进行第"+i+"组数据导入的NormalUser总线");
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
		}
		int size=normalUserList.size();
		this.CreateNewNormalUserFile(deepth);
		normalUserList.clear();
		
		return size;

		
	}//initBingxing() Ends
	
	
	/*
	 * keyUser.contains(user) 值得商榷？？
	 */
	public void addNormalUser(NormalUser user){
		//如果种子用户列表中已经存在。 
		if(keyUsers.contains(user)){
			return ;
		}		
		normalUserList.add(user);
		tmpNormalId.Append(user.userID+"\r\n");
		tmpNormalId.flush();
		Comparator<NormalUser> comparator=new Comparator<NormalUser>(){
			public int compare(NormalUser user1, NormalUser user2) {
				// TODO Auto-generated method stub
				if(user1.sum>=user2.sum)
					return 0;
				else
					return 1;
			}		
		};
		Collections.sort(normalUserList,comparator);		
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
		tmpKeyId.Append(user.userID+"\r\n");
		tmpKeyId.flush();		
		keyUsers.add(user);
		LogSys.crawlerServLogger.debug("添加新的种子用户到Vector User"+user);
		
		
	} 
	
	


	public static void ADDKEYUSER(ServerBean sb, NormalUser nu) {
		sb.addKeyUser(nu);
		
	}
	
	public static void main(String [] args){
		ServerBean sb = new ServerBean();
		try {
			ObjectOutputStream oop=new ObjectOutputStream(new FileOutputStream("UsefulFile\\Facebook\\shanjixi.bat"));
			oop.writeObject(sb);
			oop.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void CreateNewNormalUserFile(int deepth){
		String filename=String.format("UsefulFile/NormalIDs_deepth_%s.txt", deepth);
		tmpNormalId=new SaveTxtFile(filename,false);
	}
	
}
