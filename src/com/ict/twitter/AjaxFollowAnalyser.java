package com.ict.twitter;

import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserRelationship;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxFollowAnalyser extends AjaxAnalyser {


	public AjaxFollowAnalyser(MulityInsertDataBase batchdb) {
		super(batchdb);
		// TODO Auto-generated constructor stub
	}
	/**
	 * @param args
	 */
	private int count;
	public static void main(String[] args) {
		

	}
	public int doAnalyse(String currentUser,boolean isFollowing,String src,Vector<TwiUser> users) throws AllHasInsertedException{
		System.out.println(count+"\t content:"+src.substring(0,100));
		Document doc=Jsoup.parse(src, "/");
		Elements follows=doc.getElementsByAttributeValue("class","js-stream-item stream-item stream-item");
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
				System.out.printf("\t %2d,USIDNO:"+userIDNO+"userID"+userID+"\r\n",j++);	
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
