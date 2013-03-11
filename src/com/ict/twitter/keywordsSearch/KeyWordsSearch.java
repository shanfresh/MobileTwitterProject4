package com.ict.twitter.keywordsSearch;

import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;

import com.ict.twitter.TwitterLoginManager;
import com.ict.twitter.TwitterClientManager;
import com.ict.twitter.WebOperation;
import com.ict.twitter.analyser.SearchPageAnalyser;
import com.ict.twitter.task.TaskManager;
import com.ict.twitter.task.beans.Task;

public class KeyWordsSearch {

	/**
	 * @param args
	 */
	TwitterClientManager cm;
	DefaultHttpClient httpclient;
	TaskManager tm;
	boolean working=true;
	
	Vector<String> keywordsList;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		KeyWordsSearch kws=new KeyWordsSearch();
		kws.doSearch();
	}
	public KeyWordsSearch(){
		cm=new TwitterClientManager();
		httpclient=cm.getClient();
		new TwitterLoginManager(httpclient);
		//lm.doLogin();
		//tm=new TaskManager();
		keywordsList=new Vector<String>(100);
		keywordsList.add("王立军");
		keywordsList.add("邱进");
		keywordsList.add("团中央");
		keywordsList.add("薄熙来");
		keywordsList.add("活埋");		
	}
	public Vector<String> getKeyWordsList(){
		return keywordsList;
	}
	public void doSearch(){
		WebOperation.setLogFile("UserAbout.txt");		
		for(String t:getKeyWordsList()){
			Task task=new Task();
			task.setOwnType(Task.TaskType.Search);
			task.setTargetString(t);
			WebOperation.setLogFile("UserSearch.txt");
			//String searchPageResult=WebOperation.openLink(httpclient,task.getURL());
			SearchPageAnalyser spa=new SearchPageAnalyser();
			//spa.InitiallizeFromString(searchPageResult);
			spa.doAnalyse();			
		}
		
	}
	
}
