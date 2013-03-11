package com.ict.twitter.tools;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;

public class SaveTxtFile implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -7147140553825235084L;
	/**
	 * @param args
	 */

	
	BufferedWriter bw;
	public SaveTxtFile(String fileName,boolean isAppend){
		try {
			bw=new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName,isAppend),"utf8"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public int Append(String t){
		try {
			bw.write(t);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return -1;
		}
		return 0;
		
	}
	public void flush(){
		try {
			bw.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public int close(){
		try {
			bw.flush();
			
			bw.close();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
		
	}
	
	public static void main(String[] args){
		SaveTxtFile sxf=new SaveTxtFile("C:\\temp.txt",false);
		for(int i=0;i<=1000;i++){
			sxf.Append("���㵺"+i+"\r\n");
			sxf.flush();
		}
		sxf.close();
	}

}
