package com.ict.twitter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.BasePath;
import com.ict.twitter.tools.SaveTxtFile;

public class WebOperation {

	/**
	 * @param args
	 */
	
	public static String logFile="OpenLinkBuffer.txt";
	public static String base=BasePath.getBase();	

	
	//"/searches?page=1&q='"+targetString+"'";
	public  static String openLink(DefaultHttpClient httpclient,String linkAddress){
		String logFileBase=base+"/Output/Twitter/";
		URI uri=null;
		HttpGet httpget=null;
		int questIndex=linkAddress.indexOf('?');
		int qIndex=linkAddress.indexOf('q');
		if(questIndex!=-1&&qIndex!=-1){
			try {
				String path=linkAddress.substring(0,linkAddress.indexOf('?'));
				String query=linkAddress.substring(linkAddress.indexOf('q'),linkAddress.length());				
				uri=new URI(null,null,path,query,null);
				LogSys.nodeLogger.debug("³¢ÊÔ´ò¿ªÍøÒ³address£º"+uri.toString());
				httpget= new HttpGet(uri);
			} catch (URISyntaxException e) {
				// TODO Auto-generated catch block
				System.out.println("Óï·¨´íÎó");			
				e.printStackTrace();
			} catch(Exception ex){
				ex.printStackTrace();
				System.exit(0);
				
			}			
		}else{
			httpget=new HttpGet(linkAddress);
		}				
		httpget.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8");
		httpget.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_0);
		httpget.getParams().setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
				
		SaveTxtFile saveTxtFile=new SaveTxtFile(logFileBase+logFile,false);
		HttpHost targetHost = new HttpHost("twitter.com", 443, "https");
		BasicHttpContext localcontext = new BasicHttpContext();
		httpclient.getParams().setParameter(HTTP.CONTENT_ENCODING, "utf-8");
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		
		StringBuffer sb=new StringBuffer();
		try {
			HttpResponse response = httpclient.execute(targetHost, httpget, localcontext);
			BufferedReader in = new BufferedReader(	new InputStreamReader(response.getEntity().getContent(),"utf-8"));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				saveTxtFile.Append(inputLine);
				saveTxtFile.Append("\n\r");
				sb.append(inputLine+"\n\r");
			}
			in.close();
			EntityUtils.consume(response.getEntity());
			
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			System.out.println("ClientProtocalException e");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("IOException e");
		}finally{
			saveTxtFile.close();			
			
		}		
		return sb.toString();           
		
	}
	
	
	//	
	public static String getLogFile() {
		return logFile;
	}

	public static void setLogFile(String logFile) {
		WebOperation.logFile = logFile;
	}
	
	public static void main(String[] args){
		TwitterClientManager cm=new TwitterClientManager();
		DefaultHttpClient httpclient = cm.getClientNoProxy();
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		String address="/i/search/timeline?src=typd&type=recent&include_available_features=1&include_entities=1&q='Ö§³Ö+±¡'";
		String t=WebOperation.openLink(httpclient,address);
		System.out.println(t);
		
	}


}
