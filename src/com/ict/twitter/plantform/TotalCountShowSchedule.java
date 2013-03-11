package com.ict.twitter.plantform;

import java.util.TimerTask;

public class TotalCountShowSchedule extends TimerTask{
	ConsoleShowBean consoleBean;
	public TotalCountShowSchedule(ConsoleShowBean consoleBean){
		this.consoleBean=consoleBean;
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		//目前只汇报了TwitterWEB的大小
		int [] result=consoleBean.dbCounter.countTwitterWEB();
		consoleBean.TwitterAPIShow(result);
		System.out.println("汇报当前TwitterWEB的大小");
		for(int i=0;i<result.length;i++){
			System.out.print("\t"+result[i]);
		}
		System.out.println("汇报当前TwitterWEB大小结束");
	}

}
