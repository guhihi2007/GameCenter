package cn.lt.game.statistics.entity;

/**
 * Created by ltbl on 2017/6/9.
 */

public class PlatUpdateEventBean extends BaseReporetBean {
    private String downloadType;
    private String download_mode;
    private String from_version;
    private String to_version;
    private String download_action;
    private String install_type;
    private String actionType;//事件类型
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getDownload_mode() {
        return download_mode;
    }

    public void setDownload_mode(String download_mode) {
        this.download_mode = download_mode;
    }

    public String getFrom_version() {
        return from_version;
    }

    public void setFrom_version(String from_version) {
        this.from_version = from_version;
    }

    public String getTo_version() {
        return to_version;
    }

    public void setTo_version(String to_version) {
        this.to_version = to_version;
    }

    public String getDownload_action() {
        return download_action;
    }

    public void setDownload_action(String download_action) {
        this.download_action = download_action;
    }

    public String getInstall_type() {
        return install_type;
    }

    public void setInstall_type(String install_type) {
        this.install_type = install_type;
    }
}
