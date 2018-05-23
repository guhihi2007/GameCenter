package cn.lt.game.datalayer;

/**
 * Created by Administrator on 2015/11/26.
 */
public enum NetParamName {
    ID("id"), //id
    PKG_NAME("pkgName"),  //游戏包名
    USER_ID("userId"),  //用户ID
    TYPE("type"), //类型(排行、热门分类使用）
    PAGE("page"), //页数（分页查看使用，如排行、热门分类）
    TAG_ID("tag_id"),//标签id
    SORT("sort"),//排序（分类游戏列表使用）
    Q("q"),//搜索关键字
    DATA("data"),//统计数据、检查更新、游戏管理
    CONTENT("content");//提交反馈

    private final String name;

    NetParamName(String name) {
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
