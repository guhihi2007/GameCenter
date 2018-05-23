package cn.lt.game.ui.app.community.model;

import java.util.List;

/**
 * 用途：
 * 话题评论列表
 */

public class Comments {

    private int total_page;//总页数
    private List<Comment> detail;//评论列表数据
    private List<Comment> data;//他的评论列表数据

    public List<Comment> getDetail() {
        return detail;
    }

    public void setDetail(List<Comment> detail) {
        this.detail = detail;
    }

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public List<Comment> getData() {
        return data;
    }

    public void setData(List<Comment> data) {
        this.data = data;
    }
}
