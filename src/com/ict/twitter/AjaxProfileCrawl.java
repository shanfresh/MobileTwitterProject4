package com.ict.twitter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Map;
import java.util.Vector;

import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.ict.twitter.Report.ReportData;
import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.analyser.beans.UserProfile;
import com.ict.twitter.hbase.UserTwitterHbase;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.task.beans.Task;
import com.ict.twitter.task.beans.Task.TaskType;
import com.ict.twitter.tools.AllHasInsertedException;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public class AjaxProfileCrawl extends AjaxCrawl {

	//private String BASE_URL="/i/profiles/popup?async_social_proof=false&user_id=95112124&_=1362725282347";
	private String TEMP_URL="/i/profiles/popup?async_social_proof=false&screen_name=%s&_=%s";
	private DefaultHttpClient httpclient;
	private JSONParser parser = new JSONParser();
	private boolean IS_ADD_MESSAGE_ANA=true;
	
	public AjaxProfileCrawl(DefaultHttpClient _httpclient,DbOperation dboperation){
		this.httpclient=_httpclient;
		super.dboperation=dboperation;
		
	}
	
	@Override
	public boolean doCrawl(Task task, MulityInsertDataBase dbo,
			Vector<TwiUser> RelatUsers,ReportData reportData) {
		String UserID=task.getTargetString();
		UserProfile profile = new UserProfile();
		AjaxProfileAnalyser profileana = new AjaxProfileAnalyser(dbo,task);
		
		
		String CurrentTime=Long.toString(System.currentTimeMillis());
		String URL=String.format(TEMP_URL, UserID,CurrentTime);
		String ajaxContent=super.openLink(httpclient, URL,task,1);
		if(ajaxContent==null){
			LogSys.nodeLogger.error("Profile网络请求失败:"+UserID);
			super.SaveWebOpStatus(task, URL, 1, WebOperationResult.Fail, dbo);
			return false;
			
		}
		super.SaveWebOpStatus(task, URL, 1, WebOperationResult.Success, dbo);
		String user_screen_name="";
		String user_number_id="";
		String htmlContent=null;
		try {
			@SuppressWarnings("unchecked")
			Map<String,String> map =(Map<String,String>)parser.parse(ajaxContent);
			user_screen_name=map.get("screen_name");
			user_number_id=map.get("user_id");
			htmlContent=map.get("html");
			if(htmlContent==null)
				return false;

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogSys.nodeLogger.error(e.getLocalizedMessage());
			LogSys.nodeLogger.error("AjaxProfile——Error:"+URL);
			return false;
		}
		try{
			profile.setUser_id(UserID);
			profile.setUser_screen_name(user_screen_name);
			profileana.doAnylyze(htmlContent, profile);
			byte[] result=new byte[1];
			System.out.println("时间问题，暂时不采集用户的profile中的图片内容");
			//byte[] result = WebOperationAjax.getSource(httpclient, profile.getPicture_url());
			profile.setPicturedata(result);
			//对Hbase进行插入操作
			SaveToHbase(profile);
			dbo.insertIntoUserProfile(profile);
			System.out.println("insert into profile");
			System.out.println(profile.toString());
			return true;
		}catch(AllHasInsertedException ex){
			System.out.println("正在更新");
			dbo.UpdateUserProfile(profile);
			
		}catch(Exception exe){
			exe.printStackTrace();
			LogSys.crawlerServLogger.error("ErrorIn AjaxProfileCrawl", exe.fillInStackTrace());
			
		}
		return false;
	}
	public void SaveToHbase(UserProfile profile) throws IOException{
		if(this.Hbase_Enable){
			((UserTwitterHbase)hbase).InsertIntoTable(profile);
		}
	}
	public static void main(String[] args){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientByIpAndPort("192.168.120.67", 8087);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		TwitterLoginManager lgtest=new TwitterLoginManager(httpclient);
		lgtest.doLogin();
		MulityInsertDataBase dbo = new MulityInsertDataBase();
		Vector<TwiUser> users=new Vector<TwiUser>(20);
		
		AjaxProfileCrawl profilecrawl = new AjaxProfileCrawl(httpclient,null);
		Task task=new Task(TaskType.About,"wenyunchao");
		UserTwitterHbase userhbase=new UserTwitterHbase("user");
		profilecrawl.SetHabae(userhbase, true);
		profilecrawl.doCrawl(task,dbo, users,new ReportData());
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
	public static  void main2(String[] args){
		MulityInsertDataBase dbo = new MulityInsertDataBase();
		dbo.getConnection();
		dbo.getDatafromprofile();
		
	}
	
	

}
