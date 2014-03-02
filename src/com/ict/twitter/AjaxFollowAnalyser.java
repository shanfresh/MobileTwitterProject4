package com.ict.twitter;

import java.util.Map;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserRelationship;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;
import com.ict.twitter.tools.ReadTxtFile;

public class AjaxFollowAnalyser extends AjaxAnalyser {



	public AjaxFollowAnalyser(MulityInsertDataBase batchdb, Task task) {
		super(batchdb, task);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param args
	 */
	private int count;
	public static void main(String[] args) {
		JSONParser parser=new JSONParser();
		Map<String, Object> map = null;
		ReadTxtFile rxf=new ReadTxtFile("2014-02-27 14-42-24");
		try {
			map = (Map<String,Object>) parser.parse(rxf.readALL());
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String items_html=(String)map.get("items_html");
		AjaxFollowAnalyser afa=new AjaxFollowAnalyser(null, null);
		Vector<TwiUser> RelateUsers=new Vector<TwiUser>();
		try {
			afa.doAnalyse("BigBang_CBS",true,items_html,RelateUsers);
		} catch (AllHasInsertedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		

	}
	public int doAnalyse(String currentUser,boolean isFollowing,String src,Vector<TwiUser> users) throws AllHasInsertedException{
		Document doc=Jsoup.parse(src, "/");
		Elements follows=doc.getElementsByAttributeValueStarting("class","js-stream-item stream-item stream-item ");
		count=0;
		count+=follows.size();
		int j=1;
		Vector<UserRelationship> userrels = new Vector<UserRelationship>(20);
		for(Element ele:follows){
			if(ele.children()!=null){
				Element firstChildren=ele.children().first();
				//一串数字
				String userIDNO=firstChildren.attr("data-user-id");
				//唯一标示符
				String userID=firstChildren.attr("data-screen-name");					
				users.add(new TwiUser(userID,userID,0,0));
				userrels.add(new UserRelationship(currentUser,userID,isFollowing+""));
			}
			
		}
		UserRelationship[] rels = new  UserRelationship[userrels.size()];
		userrels.toArray(rels);
		super.batchdb.insertIntoUserRel(rels);
		return count;
	}
	

}
