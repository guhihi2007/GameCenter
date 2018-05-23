package cn.lt.game.domain;

import cn.lt.game.ui.app.adapter.PresentType;

/**
 * Created by Administrator on 2015/11/20.
 */
public class BaseUIModule {
    protected PresentType type;

    public PresentType getUIType() {
        return type;
    }

    public void setUIType(PresentType presentType) {
        this.type = presentType;
    }

}
