package com.ict.twitter.analyser;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.ict.twitter.MainSearch;
import com.ict.twitter.CrawlerNode.Node;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MYPropertyTool;
import com.ict.twitter.tools.SaveTxtFile;

public abstract class Analyser {
	
	public Document doc=null;
	public Task currentTask;
	public String docStr;
	public Node currentNode;
	DbOperation dbOper;
	Properties pro;
	public Analyser(){
		
	}
	public Analyser(Node currentNode){
		this.dbOper=currentNode.dbOper;
		
		if(dbOper==null||currentNode==null){
			System.err.println("数据库操作类为空");
			dbOper=new DbOperation();
		}
		MYPropertyTool.Init("config/clientproperties.ini");
		pro=MYPropertyTool.getPro();
		this.currentNode=currentNode;
	}
	public void SetTask(Task _task){
		currentTask=_task;
	}

	public void InitiallizeFromString(String htmlString){
		docStr=htmlString;
		doc=Jsoup.parse(htmlString);	
	}
	public boolean initaiallizeFromFile(String string, String charsetName, String baseUri){
		if (charsetName==""){
			charsetName="utf-8";
		}
		docStr=string;
		try {
			File myFile=new File(string);
			doc=Jsoup.parse(myFile, charsetName, baseUri);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}
	//实际的分析类
	public void onErrorHappens(Exception ex){			
		if(docStr.indexOf("Forbidden")>=0){
			if(MainSearch.sleepCount<=1000*30){
				MainSearch.sleepCount+=1000;
			}
			//LogSys.nodeLogger.error("采集速度过快"+" 当前采集延迟毫秒数("+MainSearch.sleepCount+").");
			
		}else if(docStr.contains("Tweets are protected")){
			//LogSys.nodeLogger.error("当前用户信息未共享("+currentTask.getTargetString()+")");
					
		}else{
			ex.printStackTrace();
			String currentTaskType=currentTask.ownType.toString();
			String ID=currentTask.getTargetString();
			String fileName=currentTaskType+"_"+ID+".html";
			String filePath=pro.getProperty("node.filepath")+fileName;
			LogSys.nodeLogger.error("Analyser 文件路径"+filePath);	
			SaveTxtFile stf=new SaveTxtFile(filePath,false);
			if(doc!=null)
				stf.Append(docStr);
			else{
				stf.Append("DOC is EMPTY");
			}
					stf.close();
		}
		
	}
	
	
	public abstract void doAnalyse();
	
	
}
