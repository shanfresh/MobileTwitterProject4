package com.ict.twitter.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Map.Entry;

import com.ict.twitter.plantform.PlatFormMain;

public class TwitterProperties {
	public static void show(){
		Properties pro;
		pro=new Properties();
		try {
			pro.load(new FileInputStream("config/clientproperties.ini"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			PlatFormMain.log.error("配置文件打开错误:"+"/config/clientProperties.ini");
			e.printStackTrace();
		}
		DBFactory db=new DBFactory();
		Iterator<Entry<Object, Object>> it=db.pro.entrySet().iterator();
		while(it.hasNext()){
			Entry ent=it.next();
			String t=ent.getKey().toString()+"  "+ent.getValue().toString();
			System.out.println(t);
		}
	}
}
