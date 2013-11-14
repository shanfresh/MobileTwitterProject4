package com.ict.twitter.CrawlerSchedul;

import com.ict.twitter.CrawlerServer.CrawlerServer;
import com.ict.twitter.DAO.DBKeyUserDAO;
import com.ict.twitter.DAO.DBKeyWordDAO;
import com.ict.twitter.DAO.bean.KeyUser;
import com.ict.twitter.DAO.bean.KeyWord;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.BasePath;
import com.ict.twitter.tools.ReadTxtFile;

import java.util.*;
public class KeyWordsScheduler extends BasicScheduler{
	private String basepath=BasePath.getBase();
	private String FilePath=basepath+"/UsefulFile/minganci_min.txt";
	
	public KeyWordsScheduler(CrawlerServer _crawlserver) {
		super(_crawlserver);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run() {//平均每天应该对关键词进行过滤一次
		Vector<String> total=this.getAllWords();
		for(int i=0;i<total.size();i++){
			Task task=new Task();
			task.setOwnType(TaskType.Search);
			task.setTargetString(total.get(i));
			super.crawlserver.addKeyWord(task);
		}
		
	}
	public Vector<String> getAllWords(){
		Vector<String> result=new Vector<String>();
		result.addAll(getText());
		Vector<KeyWord> dbkeywords=getDbKeyWords();
		
		for(int i=0;i<dbkeywords.size();i++){
			result.add(dbkeywords.get(i).word);
		}
		System.out.println(dbkeywords.size());
		return result;
		
	}
	private Vector<String> getText(){
		Vector<String> all=new Vector<String>();
		ReadTxtFile rxf=new ReadTxtFile(FilePath);
		Vector<String> allTxtName=rxf.read();
		Iterator<String> it=allTxtName.iterator();
		while(it.hasNext()){
			String name=it.next();
			all.add(name);
		}
		return all;
	}
	private Vector<KeyWord> getDbKeyWords(){
		DBKeyWordDAO dao=new DBKeyWordDAO();
		return dao.getKeyWords();
	}
	
	
	


}
