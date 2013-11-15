package com.ict.twitter;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


import org.apache.http.*;
import org.apache.http.conn.params.*;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.*;

import com.ict.facebook.myRetryHandler;
import com.ict.twitter.tools.MyCookieStore;

public class TwitterClientManager {
	
	DefaultHttpClient httpclient;
	SchemeRegistry sr;
	PoolingClientConnectionManager cm;
	public TwitterClientManager(){

		try {
			Initiallize();
		} catch (KeyManagementException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnrecoverableKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public void Initiallize() throws KeyManagementException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException{
		Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		X509TrustManager tm = new X509TrustManager() {	        
	        public X509Certificate[] getAcceptedIssuers() {
	        	return null;
	        	}
			@Override
			public void checkClientTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}
			@Override
			public void checkServerTrusted(X509Certificate[] arg0, String arg1)
					throws CertificateException {
			}			
		};
		SSLContext ctx = null;
		try {
			ctx = SSLContext.getInstance("TLS");
			ctx.init(null, new TrustManager[]{(TrustManager) tm}, null);			
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}
		SSLContext.setDefault(ctx);
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);
		Scheme https = new Scheme("https", 443, ssf);		
		sr = new SchemeRegistry();
		sr.register(http);
		sr.register(https);	
		cm = new PoolingClientConnectionManager(sr);
		cm.setMaxTotal(200);
		cm.setDefaultMaxPerRoute(100);
		
	}
	public DefaultHttpClient getClient(){		
		httpclient = new DefaultHttpClient(cm);
		HttpHost proxy = new HttpHost("127.0.0.1", 8580);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);	
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		httpclient.setCookieStore(new TwitterLoginCookieStore());
		return httpclient;		
	}
	public DefaultHttpClient getClientByIpAndPort(String ip,int port){
		httpclient = new DefaultHttpClient(cm);
		HttpHost proxy = new HttpHost(ip, port);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000); 
		httpclient.setCookieStore(new TwitterLoginCookieStore());
		return httpclient;
	}
	public DefaultHttpClient getClientNoProxy(){

		httpclient = new DefaultHttpClient(cm);
		httpclient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		httpclient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		httpclient.setHttpRequestRetryHandler(new myRetryHandler());
		httpclient.setCookieStore(new TwitterLoginCookieStore());
		return httpclient;		
	}
	
	
	
	
	
}
