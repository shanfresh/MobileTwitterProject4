package com.ict.twitter.CrawlerServer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;

//勇于唯一性管理的内类
public class HashArrayList<T> implements Iterable<T>{
	public int MAXSIZE=2000;
	ArrayList<T> arrayList=new ArrayList<T>();
	HashSet<T> hashSet=new HashSet<T>();
	public boolean add(T t){
		if(hashSet.contains(t)){
			return false;
		}else{
			hashSet.add(t);
			arrayList.add(t);
			return true;
		}
		
		
	}
	public T getfirst(){
		if(hashSet.size()<=0){
			return null;
		}else{
			T res=arrayList.get(0);
			hashSet.remove(res);
			arrayList.remove(0);
			return res;
		}			
	}
	@Override
	public Iterator<T> iterator() {
		// TODO Auto-generated method stub
		return arrayList.listIterator();
	}
	
	
}