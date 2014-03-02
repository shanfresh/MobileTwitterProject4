package com.ict.facebook.FriendList;

import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.http.impl.client.DefaultHttpClient;
import org.json.simple.parser.ParseException;

import com.ict.facebook.FBOperation;
import com.ict.facebook.FBWebOperation;
import com.ict.facebook.WebOp;
import com.ict.facebook.WebOpenResult;
import com.ict.facebook.TimeLine.ReadTxtFile;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.DbOperation;

public class FriendListCrawl {
	boolean MONGO_USED=false;
	/**
	 * @param args
	 */
	private static String URLNAMETAG="100003538989221";
	private static String BaseURL="https://www.facebook.com/ajax/browser/list/allfriends/?uid=1027371015&infinitescroll=1&location=friends_tab_tl&start=62&__user=100003538989221&__a=1";
	private static String BaseURLTEMPLET="https://www.facebook.com/ajax/browser/list/allfriends/?uid=%1$s&infinitescroll=1&location=friends_tab_tl&start=%2$s&__user=100003538989221&__a=1";
	
	private DefaultHttpClient httpclient=null;
	static FBOperation fbOperation =  new FBOperation();
	
	public static void main(String[] args) {
		WebOp.CreateHttpclient();
		WebOp.Init();
		
		// TODO Auto-generated method stub
		FriendListCrawl friendsAnalyser = new FriendListCrawl();		
		//friendsAnalyser.doAnalyse("100003538989221");
		Vector<User> users = new Vector<User>();
		friendsAnalyser.doCrawl("100001230554499",null,users);
		FriendListCrawl friendsAnalyser2 = new FriendListCrawl();		
		//friendsAnalyser.doAnalyse("100003538989221");		
		Vector<User> users2 = new Vector<User>();
		friendsAnalyser2.doCrawl("100001230554499",null,users2);
		
		
		System.out.println("USERCOUNT:"+users.size());
		for(User t:users2){
			System.out.println(t.toString());
		}
		System.out.println("USERCOUNT2:"+users.size());
		
	}
	public FriendListCrawl(){
//		if(_httpclient!=null){
//			this.httpclient=_httpclient;
//			System.out.println("传入httpclient 不为空");
//		}
//		else{
//			ClientManager cm=new ClientManager();
//			DefaultHttpClient httpclient=cm.getClientNoProxy();	
//			LoginManager lm=new LoginManager(httpclient);
//			this.httpclient=httpclient;
//			lm.doLogin();
//		}
	}
	public void doCrawl(String userID,DbOperation dbo,Vector<User> users){
		int index=0;
		int increseCount=0;
		do{
			increseCount=0;
			String targetURL=String.format(BaseURLTEMPLET, userID,index);
			WebOpenResult res = this.doOpen(userID,index, targetURL);
			if(res.html!=null){
				//通过URL进行传递，可以适当添加新的参数
				try{
					increseCount=this.doAnalyse(res.html,true,users);					
									
				}catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println(res.html);

				}catch(NullPointerException ex){
					ex.printStackTrace();
					System.out.println(res.html);					
				}
				catch(Exception ex){
					ex.printStackTrace();
					System.out.println(res.html);
				}
				if(MONGO_USED&&!res.isMongoGet){
					FBOperation.insertFriend(userID, index, targetURL, res.html);
				}
				index+=increseCount;	
							
			}else{
				break;
			}			
			
		}while(increseCount>0);		
	}

	
	//返回当前的采集到的用户的Count:
	public int doAnalyse(String html,boolean visitWEB,Vector<User> users) throws NullPointerException, ParseException{
		String src=null;
		if(visitWEB&&html!=null){
			src=html;
		}else if(html==null&&(!visitWEB)){
			ReadTxtFile rtf=new ReadTxtFile("UsefulFile\\Facebook\\UserList.html");
			src=rtf.read().get(0);
		}else {
			System.err.println("传入的HTML 为空");
		}
		FriendsAnalyser ana=new FriendsAnalyser();
		int count=0;
		String text=ana.getHtml(src);
		Vector<User> user=ana.AnalyserHtml(text);
		users.addAll(user);
		count=user.size();
		//ana.doAnalyser("0000",text);
		return count;
	}
	private WebOpenResult doOpen(String UserID,int index,String URL){
		FBWebOperation.setLogFile("UserList.html");
		if(URL.contains("https://www.facebook.com"))
			URL=URL.substring("https://www.facebook.com".length());
		//String HTML=FBOperation.getFriend(UserID, index);
		String HTML=null;
		if(MONGO_USED&&HTML!=null&&HTML.length()>20){
			LogSys.nodeLogger.debug("Success to getFriendHTML from MongoDB!["+UserID+"]["+index+"]");
			return new WebOpenResult(true, HTML);	
		}else{
			Future<String> future=WebOp.putWebOpTask(URL);
			String html;
			try {
				html = (future.get(20, TimeUnit.SECONDS));
			} catch (InterruptedException e) {
				html=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				html=null;
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TimeoutException e) {
				// TODO Auto-generated catch block
				html=null;
				e.printStackTrace();
			} 
			if(html!=null){
				LogSys.nodeLogger.debug("WebOP Success URL:"+URL);
				LogSys.nodeLogger.debug("Content["+html+"]");
			}else{
				LogSys.nodeLogger.debug("WebOP Failed  URL:"+URL);
			}
			return new WebOpenResult(false,html);			
		}
		
		
		
	}

}
