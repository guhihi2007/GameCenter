package cn.lt.game.statistics.entity;

/**
 * Created by ltbl on 2017/6/9.
 */

public class InstallReqEventBean extends BaseReporetBean {
    private String page;
    private String id;
    private int pos;
    private int subPos;
    private String from_page;
    private String from_id;
    private String word;
    private String downloadType;
    private String download_mode;
    private String installMode;
    private String install_type;
    private String package_name;
    private boolean isAutoInstall;
    private String actionType;//事件类型
    private String pageId;
    public String getActionType() {
        return actionType;
    }

    public void setActionType(String actionType) {
        this.actionType = actionType;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getSubPos() {
        return subPos;
    }

    public void setSubPos(int subPos) {
        this.subPos = subPos;
    }

    public String getFrom_page() {
        return from_page;
    }

    public void setFrom_page(String from_page) {
        this.from_page = from_page;
    }

    public String getFrom_id() {
        return from_id;
    }

    public void setFrom_id(String from_id) {
        this.from_id = from_id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDownloadType() {
        return downloadType;
    }

    public void setDownloadType(String downloadType) {
        this.downloadType = downloadType;
    }

    public String getDownloadMode() {
        return download_mode;
    }

    public void setDownloadMode(String downloadMode) {
        this.download_mode = downloadMode;
    }

    public String getInstallMode() {
        return installMode;
    }

    public void setInstallMode(String installMode) {
        this.installMode = installMode;
    }

    public String getInstallType() {
        return install_type;
    }

    public void setInstallType(String installType) {
        this.install_type = installType;
    }

    public String getPackageName() {
        return package_name;
    }

    public void setPackageName(String packageName) {
        this.package_name = packageName;
    }


    public boolean isAutoInstall() {
        return isAutoInstall;
    }

    public void setAutoInstall(boolean autoInstall) {
        isAutoInstall = autoInstall;
    }

    public String getPage_id() {
        return pageId;
    }

    public void setPage_id(String pageId) {
        this.pageId = pageId;
    }
}
