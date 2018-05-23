package cn.lt.game.net;



public class NetBaseInfo {
	
	private String salt;
//	private LauchImageInfo launch_image;//暂时屏蔽
	private String server_host;      //API服务器host
	private String ucenter_host;     //用户中心host
	private String forum_host;       //游戏社区host
	private String images_host;      //图片服务器host
	private String apks_host;        //apk包服务器host
	private String dcenter_host;// 数据中心host地址

	private String bbs_host; // 社区host地址
	private String gcenter_host;//服务器host地址

	private UpdateData update;       //强制更新数据
	private int pages_analysis_limit;//页面访问统计最大记录数
	private int buttons_analysis_limit;//按钮点击统计最大记录数
	private boolean tokenExists; //token是否续期   true:成功续期，可以继续使用;false:已失效

	public String getDcenter_host() {
		return dcenter_host;
	}

	public void setDcenter_host(String dcenter_host) {
		this.dcenter_host = dcenter_host;
	}

//    public void setLaunch_image(LauchImageInfo launch_image)
//    {
//        this.launch_image = launch_image;
//    }
//
//	public LauchImageInfo getLaunch_image() {
//		return launch_image;
//	}
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}


	public String getBbs_host() {
		return bbs_host;
	}

	public void setBbs_host(String bbs_host) {
		this.bbs_host = bbs_host;
	}

	public String getGcenter_host() {
		return gcenter_host;
	}

	public void setGcenter_host(String gcenter_host) {
		this.gcenter_host = gcenter_host;
	}

	public String getServer_host() {
		return server_host;
	}
	
	public void setServer_host(String server_host) {
		this.server_host = server_host;
	}
	
	public String getUcenter_host() {
		return ucenter_host;
	}
	
	public void setUcenter_host(String ucenter_host) {
		this.ucenter_host = ucenter_host;
	}
	
	public String getForum_host() {
		return forum_host;
	}
	
	public void setForum_host(String forum_host) {
		this.forum_host = forum_host;
	}
	
	public String getImages_host() {
		return images_host;
	}
	
	public void setImages_host(String images_host) {
		this.images_host = images_host;
	}
	
	public String getApks_host() {
		return apks_host;
	}
	
	public void setApks_host(String apks_host) {
		this.apks_host = apks_host;
	}
	
	public UpdateData getUpdate() {
		return update;
	}

	public void setUpdate(UpdateData update) {
		this.update = update;
	}
	
	public int getPages_analysis_limit() {
		return pages_analysis_limit;
	}
	
	public void setPages_analysis_limit(int pages_analysis_limit) {
		this.pages_analysis_limit = pages_analysis_limit;
	}
	
	public int getButtons_analysis_limit() {
		return buttons_analysis_limit;
	}
	
	public void setButtons_analysis_limit(int buttons_analysis_limit) {
		this.buttons_analysis_limit = buttons_analysis_limit;
	}
	
	public class LauchImageInfo {
		String title;	//图片标题名称
		String image;   //启动图片链接地址
		String url;     //点击启动图片的跳转地址
		GameBaseInfo game_info;  //点击启动图片跳转到游戏详情所需信息（此结构和image、url只有一个会有效）
	}

	public class UpdateData {
		String title;           //强制更新数据 
		String version;         //最新版本号 
		int version_code;       //版本代号 
		int size;               //包大小（单位：KB） 
		String md5;             //MD5值
		String download_link;   //下载地址
		String feature;         //新版特性
        boolean is_force;       //是否强制更新
        String created_at;      //更新时间
	}
	public boolean isTokenExists() {
		return tokenExists;
	}

	public void setTokenExists(boolean tokenExists) {
		this.tokenExists = tokenExists;
	}
}
