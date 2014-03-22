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
import com.ict.twitter.analyser.beans.TimeLine;
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

	//private String TEMP_URL="/i/profiles/popup?async_social_proof=false&user_id=%s&_=%s";
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
		AjaxProfileAnalyserExtend profileanaext=new AjaxProfileAnalyserExtend();
		
		String CurrentTime=Long.toString(System.currentTimeMillis());
		String URL=String.format(TEMP_URL, UserID,CurrentTime);
		String ajaxContent=super.openLink(httpclient, URL,task,0);
		if(task.getTargetTableName().equalsIgnoreCase("null")){
			task.setTargetTableName("user_profile");
		}

		if(!this.CheckValidation(ajaxContent)){
			LogSys.nodeLogger.error("Profile网络请求失败:"+UserID);
			LogSys.nodeLogger.error("AjaxProfile——Resson:"+this.ErrorMsg+"["+URL);
			profile.setUser_name(task.getTargetString());
			profile.setIs_alive(false);
			//Hbase没有写入啊
			try {
				dbo.insertIntoUserProfile(profile,task.getTargetTableName());
			} catch (AllHasInsertedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return false;
		}
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
			LogSys.nodeLogger.error("AjaxProfile——Resson:"+this.ErrorMsg+"["+URL);
			return false;
		}
		try{
			profile.setUser_id(user_number_id);
			profile.setUser_name(user_screen_name);
			profileana.doAnylyze(htmlContent, profile);
			//byte[] result=new byte[1];
			//System.out.println("时间问题，暂时不采集用户的profile中的图片内容");
			byte[] result = WebOperationAjax.getSource(httpclient, profile.getPicture_url());
			profile.setPicturedata(result);
			//对Hbase进行插入操作
			SaveToHbase(profile);
			dbo.insertIntoUserProfile(profile,task.getTargetTableName());
			System.out.println(profile.toString());
			
		}catch(AllHasInsertedException ex){
			System.out.println("正在更新");
			dbo.UpdateUserProfileByName(profile);
			
		}catch(Exception exe){
			exe.printStackTrace();
			LogSys.crawlerServLogger.error("ErrorIn AjaxProfileCrawl", exe.fillInStackTrace());
			return false;
		}
		Vector<TimeLine> profileTimeline=new Vector<TimeLine>();
		
		try {
			profileanaext.doAnylyze(htmlContent, profileTimeline);
			TimeLine[] allTimeline=profileTimeline.toArray(new TimeLine[profileTimeline.size()]);
			dbo.insertIntoMessage(allTimeline, "message");
		} catch (AllHasInsertedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("当前用户全部Profile推文已经被插入");
			
		} catch(Exception ex){
			System.out.println("当前账户没有推文信息");
		}
		return true;
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
		Task task=new Task(TaskType.About,"usherxupeng");
		task.setTargetTableName("user_profile");
		//UserTwitterHbase userhbase=new UserTwitterHbase("user");
		//profilecrawl.SetHabae(userhbase, true);
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
	private boolean CheckValidation(String content){
		if(content==null||content.length()<=1||content.contains("Sorry, that page doesn’t exist")){
			this.ErrorMsg="Profile采集_账户被冻结";
			return false;
		}
		return true;
	}
	
	

}
