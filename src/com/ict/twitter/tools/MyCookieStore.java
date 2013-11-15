package com.ict.twitter.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;

public class MyCookieStore extends BasicCookieStore{
	
	public void savetofile(String fileName){
		File f =  new File(fileName);
		ObjectOutputStream fos;
		try {
			fos = new ObjectOutputStream(new FileOutputStream(f));
			fos.writeObject(super.getCookies());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	public void resume(String fileName) throws IOException, ClassNotFoundException
	{
		File f =  new File(fileName);
		ObjectInputStream ois;
		
			ois = new ObjectInputStream(new FileInputStream(f));
			Object object = ois.readObject();
			if(object instanceof ArrayList<?>){
				ArrayList<Cookie> cookies = (ArrayList<Cookie>)object;
				Cookie[] tempcookies = new Cookie[cookies.size()];
				super.addCookies(cookies.toArray(tempcookies));
			}
		
		
	}


}
