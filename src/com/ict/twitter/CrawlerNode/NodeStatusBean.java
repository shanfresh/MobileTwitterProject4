package com.ict.twitter.CrawlerNode;

public class NodeStatusBean {
	private Node node;      //对应的节点
	boolean isBusy=false;   // 是否正在工作
	NodeStep curStep;	
	public NodeStatusBean(Node _node){
		this.node=_node;
		isBusy=false;
		curStep=NodeStep.init;
	}
	public void setStep(NodeStep newStep){
		curStep=newStep;		
	}
	public void HeartBeat(ControlSender sender){
		NodeHeartBeatReport nodeHeartBeatReport=new NodeHeartBeatReport();
		nodeHeartBeatReport.name=node.NodeName;
		nodeHeartBeatReport.currentstep=curStep;
		nodeHeartBeatReport.taskBufferSize=node.taskBuffer.size();
		if(nodeHeartBeatReport.taskBufferSize>0){
			nodeHeartBeatReport.isBusy=true;
		}else{
			nodeHeartBeatReport.isBusy=false;
		}
		sender.Send(nodeHeartBeatReport);
	}

	
	public void receiveStep(){
		
	}

	
	
	

}
