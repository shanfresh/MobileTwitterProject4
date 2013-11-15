package com.ict.twitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

public class TwitterLoginCookieStore extends BasicCookieStore{
	private static final long serialVersionUID = -5363101785685186247L;
	
	public List<Cookie> savetodb(){
		return super.getCookies();
	}
	public void resume(Object cookie) throws IOException, ClassNotFoundException
	{
		List<Cookie> cookies=(List<Cookie>)cookie;
		System.out.println("传入到可以用于恢复的cookie大小是"+cookies.size());
		Cookie[] tempcookies = new Cookie[cookies.size()];
		cookies.toArray(tempcookies);
		for(int i=0;i<tempcookies.length;i++){
			System.out.println(tempcookies[i]);
		}
		super.addCookies(tempcookies);		
	}

}
