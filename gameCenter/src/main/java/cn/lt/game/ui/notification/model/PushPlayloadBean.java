package cn.lt.game.ui.notification.model;

/**
 * Created by LinJunSheng on 2016/5/16.
 */
public class PushPlayloadBean {
    private String version_code;
    private String op;
    private String id;

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }

    public String getOp() {
        return op;
    }

    public void setOp(String op) {
        this.op = op;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
