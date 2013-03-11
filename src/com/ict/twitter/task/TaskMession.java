package com.ict.twitter.task;

import java.util.Vector;

import com.ict.twitter.task.beans.Task;

public class TaskMession {
	public static Vector<Task> allTask=new Vector<Task>();
	public static Task GetOne(){
		if(allTask.size()!=0){
			Task res=allTask.get(0);
			allTask.remove(0);
			return res;
		}else 
			return null;
	}
	public static void AddOne(Task one){
		allTask.add(one);
	}
	public static String Show(){
		StringBuffer sb=new StringBuffer();
		sb.append("ืÜด๓ะก"+allTask.size()+"\n\r");
		for(int i=0;i<allTask.size();i++){
			sb.append(allTask.get(i)+"   ");
		}		
		return sb.toString();
	}

}
