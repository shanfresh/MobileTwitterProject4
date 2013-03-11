package com.ict.twitter.task;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Vector;


import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;


public class TaskManager {
	public File Log;
	public MessageBus mbus;
	public TaskManager(){
		mbus=new MessageBus(); 
		
		Log=new File("F://TwitterTest//Log//TaskManager.log");		
	}
	


	public void InitAnalyse(){
		mbus.newZhongzi.add("l_wxy");
		mbus.Arrayzhongzi.add("l_wxy");
		mbus.newZhongzi.add("networktest1");
		mbus.Arrayzhongzi.add("networktest1");
		mbus.newZhongzi.add("networktest2");
		mbus.Arrayzhongzi.add("networktest2");
		getTask();
	
	}
	
	public void InitSearch(Vector<String> src){
		for(String t:src){
			this.addOneSearchTask(t);
		}
	}
	
	public void InitSearch(String file){
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
				this.addOneSearchTask(t);				
				i++;
			}
			System.out.println("TaskManager总共的种子数"+i+"个");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void InitSearch(){
		this.addOneSearchTask("王立军");
		this.addOneSearchTask("邓小平");	
	}
	
	public void getTask(){
		if(mbus.newZhongzi.size()>0){
			//第一个已经用完毕了			
			String first=mbus.Arrayzhongzi.remove(0);
			mbus.newZhongzi.remove(first);
			mbus.oldZhongzi.add(first);	
			allot(first);					
			System.out.println("【添加相关任务】"+first);			
			System.out.println("【种子队列数目】"+mbus.newZhongzi.size());
		}
		
	}
	
	
	public boolean addTask(String zhongziID){
		System.out.println("【新加入种子】"+zhongziID);
		//固定在1000个中取
		if(mbus.newZhongzi.size()>=1000){
			return false;
		}		
		if(mbus.newZhongzi.contains(zhongziID)||mbus.oldZhongzi.contains(zhongziID)){
			 System.out.println("【·种子已经存在·】"+zhongziID);
			return false;
		}else{
			mbus.newZhongzi.add(zhongziID);
			mbus.Arrayzhongzi.add(zhongziID);
			mbus.showNewZhongzi();			
			return true;
		}
	}
	
	
	
	
	
	//分配任务到对应的操作
	public void allot(String t){		
		int messionTypes = Task.TaskType.values().length-1;
		for(int i=0;i<messionTypes;i++){
			//不添加对应的search 任务。
			if((Task.TaskType.values()[i]).equals(Task.TaskType.Search))
				continue;
			TaskType tk=Task.TaskType.values()[i];
			Task taskElement=new Task();
			taskElement.setTargetString(t);
			taskElement.setOwnType(tk);
			TaskMession.AddOne(taskElement);
		}
	}
	
	public void addOneSearchTask(String t){
		Task searchTask=new Task();
		searchTask.setOwnType(TaskType.Search);
		searchTask.setTargetString(t);
		TaskMession.AddOne(searchTask);		
	}
	
	

	

}
