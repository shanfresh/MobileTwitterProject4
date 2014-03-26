package meilishuo;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.ReadTxtFile;
import com.ict.twitter.tools.SaveTxtFile;

public class TryToLogin {
	DefaultHttpClient httpclient=new DefaultHttpClient();
	public static void main(String[] args){
		TryToLogin tr=new TryToLogin();
		try {
			tr.getLoginPage();
			tr.showCookie();
			tr.doRequest();
			tr.showCookie();
			tr.getLoginPage();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}

	public  String getLoginPage() throws ClientProtocolException, IOException{
		//
		String TargetUrl="http://www.meilishuo.com/welcome";
		HttpPost httpost = new HttpPost(TargetUrl);
		HttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		String result=EntityUtils.toString(entity);			
		SaveTxtFile sxf=new SaveTxtFile("HTML.html", false);
		sxf.Append(result);
		return result;
	}
	public void TryLogin() throws IOException{


	}
	
	
	public void doRequest() throws IOException, IOException{
		HttpPost httpost = new HttpPost("http://www.meilishuo.com/aj/huodong/spring_carnival_add");
		 List <NameValuePair> nvps = new ArrayList <NameValuePair>();
         nvps.add(new BasicNameValuePair("type_coupon", "1"));
         nvps.add(new BasicNameValuePair("type", "pc")); 
         nvps.add(new BasicNameValuePair("code", "201403141826397f71ae5836"));
         nvps.add(new BasicNameValuePair("coupon", "1")); 
         
         httpost.setEntity(new UrlEncodedFormEntity(nvps,"UTF-8"));
         httpost.setHeader("nt", "1yQyN1tU7ssTVQ5GeZ16w6pkUGlv+Z4GPv59Yn5dVnHmvmi3pk6bUtkoARi6OQx/");
		

		
        ReadTxtFile rxf=new ReadTxtFile("COOKIELIST.txt");
        CookieStore cookieStore = new BasicCookieStore();
        Vector<String> allcookieStr=rxf.read();
   
    	for(int i=0;i<allcookieStr.size();i++){
    		String str=allcookieStr.get(i);
    		String name=str.substring(0,str.indexOf('='));
    		String value=str.substring(str.indexOf('=')+1);
    		// Populate cookies if needed
    		BasicClientCookie cookie = new BasicClientCookie(name, value);
    		cookie.setVersion(0);
    		cookie.setDomain(".meilishuo.com");
    		cookie.setPath("/");
    		cookieStore.addCookie(cookie);
    		
    	}
    	//MEILISHUO_RZ=205095526
		BasicClientCookie cookie = new BasicClientCookie("MEILISHUO_RZ", "205095526");
		cookie.setVersion(0);
		cookie.setDomain(".meilishuo.com");
		cookie.setPath("/");
		cookieStore.addCookie(cookie);
    	httpclient.setCookieStore(cookieStore);
       
    	
		HttpResponse response = httpclient.execute(httpost);
		HttpEntity entity = response.getEntity();
		String result=EntityUtils.toString(entity);
		EntityUtils.consume(entity);
		System.out.println("RESULT"+result);
		
	}
	public void showCookie(){
        List<Cookie> cookies = httpclient.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
        	LogSys.nodeLogger.debug("None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
            	LogSys.nodeLogger.debug("- " + cookies.get(i).toString());
            }
        }
	}
}
