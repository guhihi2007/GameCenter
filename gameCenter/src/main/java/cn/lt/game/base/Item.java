package cn.lt.game.base;

/**
 * Created by wenchao on 2016/3/1.
 * 公用的item数据，包括item的类型和填充数据
 */
public class Item {
    //视图类型
    public int viewType;

    //视图填充的数据
    public Object data;

    public Item(int viewType, Object data) {
        this.viewType = viewType;
        this.data = data;
    }

    public Item() {
    }
}
