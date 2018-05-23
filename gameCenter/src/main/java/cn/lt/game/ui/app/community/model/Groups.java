package cn.lt.game.ui.app.community.model;

import java.util.List;

/**
 * 用途：
 * 发现小组列表
 */

public class Groups {

    private int total_page;//总页数
    private List<Group> detail;//小组列表数据
    private List<Group> data;//TA的小组列表数据

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public List<Group> getDetail() {
        return detail;
    }

    public void setDetail(List<Group> detail) {
        this.detail = detail;
    }

    public List<Group> getData() {
        return data;
    }

    public void setData(List<Group> data) {
        this.data = data;
    }
}
