package cn.lt.game.ui.app.adapter.data;

import cn.lt.game.ui.app.adapter.PresentType;

/***
 * Created by Administrator on 2015/12/12.
 */
public class PresentData {
    private int pos;
    private int subPos;
    private PresentType mType;

    public PresentData(int pos, int subPos, PresentType mType) {
        this();
        this.pos = pos;
        this.subPos = subPos;
        this.mType = mType;
    }

    public PresentData() {
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public int getSubPos() {
        return subPos;
    }

    public void setSubPos(int subPos) {
        this.subPos = subPos;
    }

    public PresentType getmType() {
        return mType;
    }

    public void setmType(PresentType mType) {
        this.mType = mType;
    }
}
