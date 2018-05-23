package cn.lt.game.statistics.entity;

/**
 * Created by ltbl on 2017/6/9.
 */

public class ErrorEventBean extends BaseReporetBean {
    private String download_mode;
    private String downloadType;
    private String package_name;
    private String id;
    private String type;
    private String remain;
    private String actionType;//事件类型

    public String getDownload_mode() {
        return download_mode;
    }

    public void setDownload_mode(String download_mode) {
        this.download_mode = download_mode;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemain() {
        return remain;
    }

    public void setRemain(String remain) {
        this.remain = remain;
    }
}
