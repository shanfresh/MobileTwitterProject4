package com.ict.twitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserProfile;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxProfileCrawl extends AjaxCrawl {

	private String BASE_URL="/i/profiles/popup?async_social_proof=false&user_id=95112124&_=1362725282347";
	private String TEMP_URL="/i/profiles/popup?async_social_proof=false&user_id=%s&_=%s";
	private DefaultHttpClient httpclient;
	private JSONParser parser = new JSONParser();
	public AjaxProfileCrawl(DefaultHttpClient _httpclient){
		this.httpclient=_httpclient;
	}
	
	@Override
	public boolean doCrawl(String UserID, MulityInsertDataBase dbo,
			Vector<TwiUser> RelatUsers) {
		UserProfile profile = new UserProfile();
		AjaxProfileAnalyser profileana = new AjaxProfileAnalyser(dbo);
		
		String CurrentTime=Long.toString(System.currentTimeMillis());
		String URL=String.format(TEMP_URL, UserID,CurrentTime);
		String ajaxContent=super.openLink(httpclient, URL);
		String user_screen_name="";
		String htmlContent=null;
		try {
			@SuppressWarnings("unchecked")
			Map<String,String> map =(Map<String,String>)parser.parse(ajaxContent);
			user_screen_name=map.get("screen_name");
			htmlContent=map.get("html");
			if(htmlContent==null)
				return false;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		try{
			profile.setUser_id(UserID);
			profile.setUser_screen_name(user_screen_name);
			profileana.doAnylyze(htmlContent, profile);
			if(profile.getPicture_url()!=null){
				byte[] result = WebOperationAjax.getSource(httpclient, profile.getPicture_url());
				profile.setPicturedata(result);
				for(int i=0;i<result.length;i++){
					System.out.println(Byte.toString(result[i]));
				}
			}
			dbo.insertIntoUserProfile(profile);
			System.out.println("insert into profile");
			return true;
		}catch(Exception exe){
			exe.printStackTrace();
			LogSys.crawlerServLogger.error("ErrorIn AjaxProfileCrawl", exe.fillInStackTrace());
		}
		return false;
	}
	public static void main2(String[] args){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientNoProxy();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		MulityInsertDataBase dbo = new MulityInsertDataBase();
		Vector<TwiUser> users=new Vector<TwiUser>(20);
		
		AjaxProfileCrawl profilecrawl = new AjaxProfileCrawl(httpclient);
		profilecrawl.doCrawl("488092285",dbo, users);
		httpclient.getConnectionManager().shutdown();
		profilecrawl.service.shutdown();
	}
	
	
	
	
	
	
	
	private void saveByteToImageFile(String fileName,byte[] data){
		try {
			FileOutputStream fos = new FileOutputStream(new File("Output/Twitter/"+fileName));
			fos.write(data);
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static  void main(String[] args){
		MulityInsertDataBase dbo = new MulityInsertDataBase();
		dbo.getConnection();
		dbo.getDatafromprofile();
		
	}
	
	

}
