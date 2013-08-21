package com.ict.twitter.CrawlerSchedul;

import java.util.TimerTask;
import java.util.Vector;

import com.ict.twitter.CrawlerServer.CrawlerServer;
import com.ict.twitter.CrawlerServer.InputType;
import com.ict.twitter.DatabaseBean.DBBeanInputTask;
import com.ict.twitter.DatabaseBean.InputTaskBean;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;

public class CrawlServerScheduler extends TimerTask{
	private CrawlerServer crawlserver;
	private DBBeanInputTask inputTaskOp;
	public CrawlServerScheduler(CrawlerServer crawlserver){
		this.crawlserver=crawlserver;
		inputTaskOp=new DBBeanInputTask();
	}	
	public void run() {
		checkUserInsert();
		
	}
	//检查是否有用户插入的数据
	public void checkUserInsert(){
		if(inputTaskOp.CheckHasNewInput()){//当发现存在新插入的数据时，将新插入的数据加入到采集任务表
			LogSys.crawlerServLogger.debug("发现新的需要加入的任务");
			Vector<InputTaskBean> vector=inputTaskOp.GetAllTask();
			for(int i=0;i<vector.size();i++){
				LogSys.crawlerServLogger.debug("【调度器】加入的任务:"+vector.get(i).TaskName+":"+vector.get(i).InputType);
				InserIntoMessbus(vector.get(i));
				inputTaskOp.ModifyStatus("Crawling", vector.get(i).ID);
			}			
		}
	}
	private void InserIntoMessbus(InputTaskBean inputTask){
		//根据获取到的InputTask调用CrawlServer方法进行加入操作
		if(inputTask.InputType==InputType.Topic){
			String parameter=inputTask.TaskParameter;
			String[] keywords=parameter.split(" ");
			for(int i=0;i<keywords.length;i++){
				Task task=new Task();
				task.setTrack(true);
				task.setTaskTrackID(inputTask.ID);//需要修改
				task.setOwnType(TaskType.Search);
				task.setTargetString(keywords[i]);
				crawlserver.addKeyWordAndTopicTask(task);
			}
			String parameter2=inputTask.TaskParameter2;
			String[] keywords2=parameter.split(" ");
			for(int i=0;i<keywords2.length;i++){
				Task task=new Task();
				task.setTrack(true);
				task.setTaskTrackID(inputTask.ID);//需要修改
				task.setOwnType(TaskType.TimeLine);
				task.setTargetString(keywords2[i]);
				crawlserver.addKeyWordAndTopicTask(task);
				task.setOwnType(TaskType.Following);////搜索用户的粉丝
				task.setTargetString(keywords2[i]);
				crawlserver.addKeyWordAndTopicTask(task);
				task.setOwnType(TaskType.Followers);//搜索用户的关注
				task.setTargetString(keywords2[i]);
				crawlserver.addKeyWordAndTopicTask(task);
				task.setOwnType(TaskType.About);//搜索用户的主页
				task.setTargetString(keywords2[i]);
				crawlserver.addKeyWordAndTopicTask(task);
			}

		}
		if(inputTask.InputType==InputType.KeyUser){
			Task task=new Task();
			task.setTrack(true);
			task.setTaskTrackID(inputTask.ID);//要跟踪的ID是
			task.setTargetString(inputTask.TaskName);//把主题的名称也作为关键词进行搜索
			task.setOwnType(TaskType.TimeLine);//关注重点账户的推文
			crawlserver.addKeyUserTask(task);
			task.setOwnType(TaskType.Following);
			crawlserver.addKeyUserTask(task);
			task.setOwnType(TaskType.Followers);
			crawlserver.addKeyUserTask(task);
		}
		if(inputTask.InputType==InputType.KeyWord){
			Task task=new Task();
			task.setTrack(true);
			task.setTaskTrackID(inputTask.ID);//要跟踪的ID是
			task.setTargetString(inputTask.TaskName);//把主题的名称也作为关键词进行搜索
			task.setOwnType(TaskType.Search);//设置类型为Search类型
			crawlserver.addKeyUserTask(task);
			
		}
		if(inputTask.InputType==InputType.NorUser){
			Task task=new Task();
			task.setTrack(true);
			task.setTaskTrackID(inputTask.ID);//要跟踪的ID是
			task.setTargetString(inputTask.TaskName);//对普通用户的检索等于对普通用户的时间，粉丝等进行检索
			task.setOwnType(TaskType.TimeLine);//
			crawlserver.addTask(task);
			task.setOwnType(TaskType.Following);
			crawlserver.addTask(task);
			task.setOwnType(TaskType.Followers);
			crawlserver.addTask(task);
		}
	}
	
}
