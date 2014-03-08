package com.iie.twitter.analyse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import com.ict.twitter.analyser.beans.TimeLine;
import com.ict.twitter.tools.ReadTxtFile;
import com.ict.twitter.tools.SaveTxtFile;

public class DocumentAnalyser {
	File f;
	Vector<String> trueList;
	Vector<String> falsList;
	public DocumentAnalyser(String filename){
		f=new File(filename);
		if(f.exists()==false){
			System.err.println("文件不存在");
		}
		trueList=load("AnalyseSource/支持关键词.txt");
		System.out.println("支持的值大小"+trueList.size());
		falsList=load("AnalyseSource/反对关键词.txt");
		System.out.println("不支持的值大小"+falsList.size());
	}
	
	
	Vector<TimeLine> total=new Vector<TimeLine>();
	HashMap<String,Vector<TimeLine>> combine;

	public void doAna(){
		try {
			total.clear();
			BufferedReader br=new BufferedReader(new InputStreamReader(new FileInputStream(f)));
			String currentLine;
			int count=0;
			while((currentLine=br.readLine())!=null){
				if(currentLine.contains("(AjaxSearchAnalyser.java:71)")){
					count++;
					TimeLine e=convert(currentLine);
					total.add(e);
				}
			}
			br.close();
			System.out.println(total.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public TimeLine convert(String content){
		TimeLine timeline=new TimeLine();
		final String Tweet_ID_TAG="tweetID:";
		final String Date_TAG="date:";
		final String UserID_TAG="userID: ";
		final String Content_TAG="content: ";
		String tweet_id=content.substring(content.indexOf(Tweet_ID_TAG)+Tweet_ID_TAG.length(),content.indexOf(' ', content.indexOf(Tweet_ID_TAG)));
		String date=content.substring(content.indexOf(Date_TAG)+Date_TAG.length(),content.indexOf("userID:")-1);
		String userid=content.substring(content.indexOf(UserID_TAG)+UserID_TAG.length(),content.indexOf("content"));
		String content_inner=content.substring(content.indexOf(Content_TAG)+Content_TAG.length());
		//System.out.println(tweet_id+"__"+date+"__"+userid+"__"+content_inner+"__");
		timeline.setId(tweet_id);
		timeline.setDate(date);
		timeline.setAuthor(userid);
		timeline.setContent(content_inner);
		return timeline;
	}
	private void Combine(){
		combine=new HashMap<String,Vector<TimeLine>>();
		System.out.println("总的个数"+total.size());
		int baohan=0;
		int bubaohan=0;
		for(TimeLine t:total){
			if(combine.containsKey(t.getAuthor())){
				Vector<TimeLine> vect=combine.get(t.getAuthor());
				vect.add(t);
				combine.put(t.getAuthor(), vect);
				baohan++;
			}else{
				Vector<TimeLine> vect=new Vector<TimeLine>();
				vect.add(t);
				combine.put(t.getAuthor(), vect);
				bubaohan++;
			}
		}
		System.out.println("Combine"+baohan+"_"+bubaohan);
		
	}
	public int getCount(boolean flag,String content){
		int count=0;
		if(flag){
			for(String t:trueList){
				if(content.contains(t)){
					count++;
				}				
			}			
		}else{
			for(String t:falsList){
				if(content.contains(t)){
					count++;
				}				
			}
		}
		return count;
	}
	
	
	
	private void print(SaveTxtFile sxf){
		Set<String> key=combine.keySet();
		Iterator<String> it=key.iterator();
		
		while(it.hasNext()){
			String t=it.next();
			
			String line="";
			
			line+=(t+"\t");			
			line+=(combine.get(t).size()+"\t");
			Vector<TimeLine> cur=combine.get(t);
			StringBuffer sb=new StringBuffer();
			for(TimeLine timeline:cur){
				sb.append(timeline.getContent()+"||");
			}
			int truecount=getCount(true, sb.toString());
			int falsecount=getCount(false,sb.toString());
			line +=truecount+"\t";
			line +=falsecount+"\t";
			line+=sb.toString()+"\r\n";
			
			sxf.Append(line);
			System.out.print(line);
		}
		sxf.close();
		System.out.println();
		
	}
	
	private Vector<String> load(String fileName){
		
		ReadTxtFile rxf=new ReadTxtFile(fileName);
		Vector<String> result=rxf.read();
		
		return result;
	}
	
	public static void main(String[] args){
		DocumentAnalyser da=new DocumentAnalyser("C:\\Users\\shanjixi\\Desktop\\温的推文搜索\\温的推文.txt");
		da.doAna();
		da.Combine();
		Date date=new Date();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");
		String filename=sdf.format(date);
		System.out.println("分析后页面列表："+filename);
		SaveTxtFile sxf=new SaveTxtFile("AnalyseSource/"+filename,false);
		da.print(sxf);
		
		
	}
	

}
