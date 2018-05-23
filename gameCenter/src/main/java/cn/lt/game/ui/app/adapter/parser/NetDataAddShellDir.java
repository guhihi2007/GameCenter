package cn.lt.game.ui.app.adapter.parser;

/***
 * Created by Administrator on 2015/12/10.
 */
public class NetDataAddShellDir {
    /**
     * 是否需要重新分组；
     */
    boolean needSplit;
    /**
     * 是否是一个整体，如轮播图、首页入口应作为一个整体；
     */
    boolean isWhole;
    int numberOfLine = 1;

    public boolean isWhole() {
        return isWhole;
    }

    public void setIsWhole(boolean isWhole) {
        this.isWhole = isWhole;
    }

    public boolean isNeedSplit() {
        return needSplit;
    }

    public void setNeedSplit(boolean needSplit) {
        this.needSplit = needSplit;
    }

    public int getNumberOfLine() {
        return numberOfLine;
    }

    public void setNumberOfLine(int numberOfLine) {
        this.numberOfLine = numberOfLine;
    }
}