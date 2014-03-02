package com.ict.twitter.tools.filemonitor;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class FileMonitor {

	public static FileMonitor instance=new FileMonitor();
	
	public Timer timer;
	public FileMonitor(){
		timer=new Timer();
	}
	public static FileMonitor getInstance(){
		return instance;
	}
	
	public void AddMonitor(String fileName,String variable,FileChangeListener filelistener){
		FileChangeTimerTask task=new FileChangeTimerTask(fileName,variable,filelistener);
		timer.schedule(task, 1000, 5000);
		
	}
	
	public class FileChangeTimerTask extends TimerTask{
		private String fileName;
		private String variableName;
		private FileChangeListener filelistener;
		private long lastModified;
		private File monitingFile;
		public FileChangeTimerTask(String fileName,String variableName,FileChangeListener filelistener){
			this.fileName=fileName;
			this.variableName=variableName;
			this.filelistener=filelistener;
            this.monitingFile = new File(fileName);
            if (!this.monitingFile.exists()) {
                return;
            }
            this.lastModified = this.monitingFile.lastModified();
		}
		@Override
		public void run() {
			long modify=this.monitingFile.lastModified();
			if(modify>lastModified){
				filelistener.onchange(fileName);
				lastModified=modify;
				System.out.println(fileName+"IS Modify");
			}else{
				
			}
			
		}
		
	}
	public static void main(String[] args){
		FileMonitor fm=FileMonitor.getInstance();
		WebOpLogFlagCallBack fcc=new WebOpLogFlagCallBack();
		fm.AddMonitor("config/clientproperties.ini", "webop.savetofileflag", fcc);
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
}
