package com.ict.facebook;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import javax.net.ssl.SSLException;

import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpRequest;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.protocol.ExecutionContext;

import com.ict.twitter.plantform.LogSys;
import com.sun.net.httpserver.HttpContext;

public class myRetryHandler extends DefaultHttpRequestRetryHandler{
	public boolean retryRequest(IOException exception,int executionCount,HttpContext context) {
			LogSys.nodeLogger.info("Retry with count:"+executionCount);
	        if (executionCount >= 2) {
	            // Do not retry if over max retry count
	            return false;
	        }
	        if (exception instanceof InterruptedIOException) {
	            // Timeout
	            return true;
	        }
	        if (exception instanceof UnknownHostException) {
	            // Unknown host
	            return true;
	        }
	        if (exception instanceof ConnectException) {
	            // Connection refused
	            return true;
	        }
	        if (exception instanceof SSLException) {
	            // SSL handshake exception
	            return false;
	        }
	       
	        HttpRequest request = (HttpRequest) context.getAttributes().get(ExecutionContext.HTTP_REQUEST);
	        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest); 
	        if (idempotent) {
	            return true;
	        }
	        return false;
	 }
}

