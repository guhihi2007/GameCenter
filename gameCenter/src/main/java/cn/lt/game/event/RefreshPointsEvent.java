package cn.lt.game.event;

import cn.lt.game.bean.SignPointsNet;


public class RefreshPointsEvent {
    public SignPointsNet signPointsNet;

    public RefreshPointsEvent(SignPointsNet signPointsNet) {
        this.signPointsNet = signPointsNet;
    }
}
