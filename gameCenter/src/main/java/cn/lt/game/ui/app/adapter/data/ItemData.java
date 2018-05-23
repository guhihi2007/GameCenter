package cn.lt.game.ui.app.adapter.data;

import cn.lt.game.ui.app.adapter.PresentType;

/***
 * Created by Administrator on 2015/11/12.
 */
public class ItemData<T> {
    private PresentData mPresentData = new PresentData();
    private T mData;
    private boolean isFirst;
    private boolean isLast;

    public ItemData(T mData) {
        setmData(mData);
    }

    public PresentData getmPresentData() {
        return mPresentData;
    }

    public void setmPresentData(PresentData mPresentData) {
        this.mPresentData = mPresentData;
    }

    public boolean isFirst() {
        return isFirst;
    }

    public void setIsFirst(boolean isFirst) {
        this.isFirst = isFirst;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setIsLast(boolean isLast) {
        this.isLast = isLast;
    }

    public int getPos() {
        return mPresentData.getPos();
    }

    public void setPos(int pos) {
        this.mPresentData.setPos(pos);
    }

    public int getSubPos() {
        return mPresentData.getSubPos();
    }

    public void setSubPos(int subPos) {
        this.mPresentData.setSubPos(subPos);
    }

    public T getmData() {
        return mData;
    }

    public void setmData(T mData) {
        this.mData = mData;
    }

    public PresentType getmPresentType() {
        return mPresentData.getmType();
    }

    public void setmType(PresentType mType) {
        this.mPresentData.setmType(mType);
    }

}
