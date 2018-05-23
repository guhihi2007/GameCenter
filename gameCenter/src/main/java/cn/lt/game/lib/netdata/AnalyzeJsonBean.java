package cn.lt.game.lib.netdata;

/**
 * Created by Administrator on 2015/11/13.
 */
public class AnalyzeJsonBean {
    // 是否自动解析
    private boolean isAutoAnalyze;
    // 自动解析为tokenType，手动解析为手动解析的方法
    private Object value;

    public AnalyzeJsonBean(boolean isAutoAnalyze, Object value) {
        this.isAutoAnalyze = isAutoAnalyze;
        this.value = value;
    }

    public boolean isAutoAnalyze() {
        return isAutoAnalyze;
    }

    public Object getValue() {
        return value;
    }

}
