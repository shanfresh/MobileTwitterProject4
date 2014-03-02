package com.ict.facebook;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.HttpVersion;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;

public class ClientManager {
	
	DefaultHttpClient httpclient;
	PoolingClientConnectionManager poolConnectManager;
	HttpParams params = new BasicHttpParams();
	public ClientManager(){		
		Initiallize();		
	}
	
	public void Initiallize(){
		Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
		X509TrustManager tm = new X509TrustManager() {	        
	        public X509Certificate[] getAcceptedIssuers() {return null;}
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
		SSLSocketFactory ssf = new SSLSocketFactory(ctx);		
		Scheme https = new Scheme("https", 443, ssf);		
		SchemeRegistry schemeRegistry = new SchemeRegistry();
		schemeRegistry.register(http);
		schemeRegistry.register(https);	
		
		poolConnectManager=new PoolingClientConnectionManager(schemeRegistry);
		poolConnectManager.setMaxTotal(1000);
		poolConnectManager.setDefaultMaxPerRoute(300);
		
		params.setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET,"utf-8");
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,HttpVersion.HTTP_1_0);
		params.setParameter(CoreProtocolPNames.USER_AGENT,"Mozilla/5.0 (compatible; MSIE 10.0; Windows NT 6.1; Trident/6.0)");
		params.setParameter(HTTP.CONTENT_ENCODING, "utf-8");
		params.setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 20000);
		params.setParameter(CoreConnectionPNames.SO_TIMEOUT, 10000);
		
		
	}
	public DefaultHttpClient getClient(String ip,int port){		
		httpclient = new DefaultHttpClient(poolConnectManager,params);
		HttpHost proxy = new HttpHost(ip, port);
		httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
		httpclient.setHttpRequestRetryHandler(new myRetryHandler());
		return httpclient;		
	}
	public DefaultHttpClient getClientNoProxy(){		
		httpclient = new DefaultHttpClient(poolConnectManager,params);
		httpclient.setHttpRequestRetryHandler(new myRetryHandler());
		return httpclient;		
	}
	
	
	
	
	
}
