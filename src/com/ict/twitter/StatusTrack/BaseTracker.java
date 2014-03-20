package com.ict.twitter.StatusTrack;

import com.ict.twitter.task.beans.Task;

public abstract class BaseTracker {
	public abstract int AddTask(Task task) ;
	public abstract void CheckTask(Task task) ;
	public abstract void FinishTask(Task task,String ErrorMsg) ;
	public abstract void FailTask(Task task,String ErrorMsg) ;
}