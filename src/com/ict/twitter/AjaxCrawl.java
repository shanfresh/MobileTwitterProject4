package com.ict.twitter;

import java.util.Vector;
import java.util.concurrent.*;

import org.apache.http.impl.client.DefaultHttpClient;

import com.ict.twitter.analyser.beans.TwiUser;
import com.ict.twitter.plantform.LogSys;
import com.ict.twitter.tools.DbOperation;
import com.ict.twitter.tools.MulityInsertDataBase;

public abstract class AjaxCrawl {

	/**
	 * @param args
	 */

	public ExecutorService service = Executors.newCachedThreadPool();
	public abstract boolean doCrawl(String src, MulityInsertDataBase dbo,Vector<TwiUser> RelatUsers);


	public String openLink(final DefaultHttpClient httpclient,final String targetUrl) {
		String WebPageContent = null;
		Future<String> future = service.submit(new Callable<String>() {
			public String call() throws Exception {
				try {
					return WebOperationAjax.openLink(httpclient, targetUrl,0);
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				return null;
			}
		});
		
		try{
			WebPageContent = (String) future.get(20000, TimeUnit.MILLISECONDS);
		}catch(TimeoutException ex){
			LogSys.nodeLogger.error("OpenURL TimeOut(20s):" + targetUrl);
		}
		catch (Exception e) {
			e.printStackTrace();
			LogSys.nodeLogger.error(e.getMessage());
			LogSys.nodeLogger.error("OpenURL Error URL:" + targetUrl);
			WebPageContent = null;
		}
		if(WebPageContent == null){
			
		}else{
			
		}
		return WebPageContent;

			
	}


}
