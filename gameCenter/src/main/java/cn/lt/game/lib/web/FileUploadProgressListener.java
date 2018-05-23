package cn.lt.game.lib.web;

public interface FileUploadProgressListener {
	void transferred(long uploadSize, long totalSize);
	boolean getIsListener();
}
