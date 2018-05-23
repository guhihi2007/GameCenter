package cn.lt.game.domain;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.ui.app.adapter.PresentType;

/***
 * Created by Administrator on 2015/11/18.
 */
public class UIModuleGroup<T> extends BaseUIModule {
    private List<T> data;

    public UIModuleGroup(PresentType type) {
        this.type = type;
        this.data = new ArrayList<>();
    }

    public List<T> getData() {
        return data;
    }

    public void add(T element) { data.add(element); }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int size() {
        return data.size();
    }

}
