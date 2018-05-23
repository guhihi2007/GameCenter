package cn.lt.game.domain;

import cn.lt.game.ui.app.adapter.PresentType;

/***
 * Created by Administrator on 2015/11/19.
 */
public class UIModule<T> extends BaseUIModule {
    private T data;

    public UIModule(PresentType type) {
        this.type = type;
        this.data = null;
    }

    public UIModule(PresentType type, T data) {
        this.type = type;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
