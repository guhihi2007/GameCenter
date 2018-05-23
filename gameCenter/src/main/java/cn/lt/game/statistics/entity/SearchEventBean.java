package cn.lt.game.statistics.entity;

/**
 * Created by ltbl on 2017/6/9.
 */

public class SearchEventBean extends BaseReporetBean {
    private String page;
    private String word;
    private String actionType;//事件类型
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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }
}
