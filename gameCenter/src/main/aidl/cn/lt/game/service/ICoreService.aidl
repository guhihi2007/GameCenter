package cn.lt.game.service;

import cn.lt.game.service.DownloadList;

interface ICoreService
{	
	void startDownload(in DownloadList strArr);
	void startDownloadApk(String url,String filePath,String packageName,int gameId);
	void startSendStatistics();
	void addAutoUpdate(String filePath);
	void pushNote(int id);
}