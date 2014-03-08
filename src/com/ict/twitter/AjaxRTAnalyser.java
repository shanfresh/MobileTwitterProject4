package com.ict.twitter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.AjaxAnalyser.AnalyserCursor;
import com.ict.twitter.analyser.beans.MessageReteet;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.filter.TimeTransformer;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.MulityInsertDataBase;
import com.ict.twitter.tools.ReadTxtFile;

public class AjaxRTAnalyser extends AjaxAnalyser {

	TimeTransformer ttf;
	public AjaxRTAnalyser(MulityInsertDataBase batchdb, Task task) {
		super(batchdb, task);
		ttf=new TimeTransformer();
	}

	public static void main(String[] args) {
		JSONParser parser=new JSONParser();
		Map<String, Object> map = null;
		ReadTxtFile rxf=new ReadTxtFile("C:\\Users\\shanjixi\\Desktop\\采集结果分析\\2014-03-05 12-50-21.txt");
		String html=rxf.readALL();
		try {
			map = (Map<String,Object>) parser.parse(html);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String HTML=(String) map.get("descendants");
		AjaxRTAnalyser rtana=new AjaxRTAnalyser(null, new Task(TaskType.MessageRel,HTML));
		Vector<MessageReteet> rets=new Vector<MessageReteet>();
		try {
			rtana.doAnalyser(HTML, rets);
		} catch (AllHasInsertedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	public void doAnalyser(String src,Vector<MessageReteet> messageRet) throws AllHasInsertedException{
		Document doc=Jsoup.parse(src, "/");
		Elements all=doc.getElementsByAttributeValueStarting("class", "simple-tweet tweet js-stream-tweet js-actionable-tweet js-profile-popup-actionable");
		System.out.println("当前推文下相关的用户数目："+all.size());
		for(Element t:all){			
			String user_id=ParseUserID(t);
			String user_screenname=t.attr("data-screen-name");
			String user_name=t.attr("data-name");
			String date=ParseDate(t);
			String message_id=task.getTargetString();
			MessageReteet mr=new MessageReteet();
			mr.setUser_name(user_name);
			mr.setUser_aliasname(user_screenname);
			mr.setUser_id(user_id);
			mr.setMessage_id(message_id);
			Element target=t.getElementsByAttributeValueStarting("class", "js-tweet-text tweet-text").first();
			if(target!=null){
				System.out.println(user_id+" "+user_screenname+" "+user_name+"["+date+"]");
				String content=target.text();
				mr.setContent(content);
			}else{
				System.err.println("解析失败");
				mr.setContent(null);
			}
			messageRet.add(mr);
			
		}
		
	}

	private String ParseUserID(Element t) {
		return t.attr("data-user-id");
	}
	private String ParseDate(Element t){
		try{
			Element dateEle=t.getElementsByAttributeValueStarting("class", 
					"_timestamp js-short-timestamp").first();
			String dateStr=dateEle.attr("data-time");
			return ttf.Convert(dateStr);
		}catch(Exception ex){
			System.err.println("时间内容解析出错");
			return null;
		}
		
		
	}

}
