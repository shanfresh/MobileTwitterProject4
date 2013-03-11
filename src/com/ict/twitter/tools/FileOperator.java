package com.ict.twitter.tools;

import java.io.File;
import java.io.IOException;

public class FileOperator {

	/**
	 * @param args
	 */
	public static void main(String[] args) {		
		System.out.println(isPathExist("c:/shan"));
	}
	public static boolean isPathExist(String filePath){
		File f=new File(filePath);
		return f.exists();
		
	}
	public static boolean createFileOrDir(String filePath){
		File f=new File(filePath);
		if(f.isDirectory()&&!f.exists()){
			f.mkdirs();
		}else if(!f.exists()){
			String parent=f.getParent();
			File parentFile=new File(parent);
			System.out.println("父目录的路径是"+parentFile);
			
			parentFile.mkdirs();
			System.out.println("全体路径是"+f.getAbsolutePath());
			try {
				if(!f.getName().contains(".")){
					f.mkdir();
				}else{
					f.createNewFile();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("文件创建失败");
				e.printStackTrace();
			}			
		}
		
		return true;
	}
	public static int FileOrDictory(File f){
        if(f.exists()){        
	        if(f.isDirectory()){
	        	System.out.println("是文件夹");
	        	return 2;
	        }else if(f.exists()&&!f.isDirectory()){
	        	System.out.println("不是文件夹");
	        	return 1;
	        }else{
	        	System.out.println("文件不存在");
	        	return 0;
	        }
        }else{
        	System.out.println("文件不存在");
        	return 0;
        }

	}

}
