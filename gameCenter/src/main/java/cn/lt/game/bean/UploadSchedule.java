package cn.lt.game.bean;

/**
 * Created by Administrator on 2015/12/19.
 */
public class UploadSchedule {
    private long uploadSize;
    private long totalSize;

    public UploadSchedule(long uploadSize, long totalSize) {
        this.uploadSize = uploadSize;
        this.totalSize = totalSize;
    }

    public long getUploadSize() {
        return uploadSize;
    }

    public void setUploadSize(long uploadSize) {
        this.uploadSize = uploadSize;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
}
