package cn.lt.game.model;

public class CoreTask {
	private int id;
	private Runnable task;
	//优先级,此字段预留
	private int priority;
	// 1、全网络状态；2、流量；3、wifi状态
	private int networkCategory;
	// 是否是紧急状况
	private boolean isUrgent;
	//定时执行，不定时则为0
	private long time;
	// 循环时间,不循环则为0
	private long loopTime;
	// 任务类型:1、预下载；2、发送统计数据；3、统计应用使用频率；4、自动更新 ；5、检查游戏中心未启动时间
	private int taskType;
	// 最后执行时间，超过了就不执行，不定时则为0
	public long lastTime;

	public CoreTask() {
		isUrgent = false;
	}
	
	public Runnable getTask() {
		return task;
	}

	public void setTask(Runnable task) {
		this.task = task;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getNetworkCategory() {
		return networkCategory;
	}

	/**
	 * 设置执行时的网络限制
	 * @param networkCategory 1、全网络状态；2、流量；3、wifi状态
	 */
	public void setNetworkCategory(int networkCategory) {
		this.networkCategory = networkCategory;
	}

	public boolean isUrgent() {
		return isUrgent;
	}

	public void setUrgent(boolean isUrgent) {
		this.isUrgent = isUrgent;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public long getLoopTime() {
		return loopTime;
	}

	public void setLoopTime(long loopTime) {
		this.loopTime = loopTime;
	}
	
	/**
	 * 任务类型
	 * @return  taskType  1、预下载；2、发送统计数据；3、统计应用使用频率  4、自动更新  5、检查游戏中心未启动时间
	 */
	public int getTaskType() {
		return taskType;
	}

	/**
	 * 任务类型
	 * @param taskType  1、预下载；2、发送统计数据；3、统计应用使用频率  4、自动更新  5、检查游戏中心未启动时间
	 */
	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public long getLastTime() {
		return lastTime;
	}

	public void setLastTime(long lastTime) {
		this.lastTime = lastTime;
	}
}
