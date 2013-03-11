package com.ict.twitter.CrawlerNode;

import java.io.Serializable;

import com.ict.twitter.CrawlerMessage.ControlMessage;


public class NodeHeartBeatReport extends ControlMessage implements Serializable{

	private static final long serialVersionUID = 1485748077087032639L;
	public String name;
	public boolean isBusy;
	public NodeStep currentstep;
	public int taskBufferSize;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isBusy() {
		return isBusy;
	}
	public void setBusy(boolean isBusy) {
		this.isBusy = isBusy;
	}
	public NodeStep getCurrentstep() {
		return currentstep;
	}
	public void setCurrentstep(NodeStep currentstep) {
		this.currentstep = currentstep;
	}
	public int getTaskBufferSize() {
		return taskBufferSize;
	}
	public void setTaskBufferSize(int taskBufferSize) {
		this.taskBufferSize = taskBufferSize;
	}
	
	
}