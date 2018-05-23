package cn.lt.game.bean;

import android.graphics.drawable.Drawable;

import java.io.Serializable;


public class TabEntity implements Serializable{
    private String title;
    private int selectedIcon;
    private int unSelectedIcon;
    private Drawable selectedDrawable;
    private Drawable unSelectedDrawable;
    private String selectUrl;
    private String unSelectUrl;

    public String getSelectUrl() {
        return selectUrl;
    }

    public void setSelectUrl(String selectUrl) {
        this.selectUrl = selectUrl;
    }

    public String getUnSelectUrl() {
        return unSelectUrl;
    }

    public void setUnSelectUrl(String unSelectUrl) {
        this.unSelectUrl = unSelectUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    public void setSelectedIcon(int selectedIcon) {
        this.selectedIcon = selectedIcon;
    }


    public void setUnSelectedIcon(int unSelectedIcon) {
        this.unSelectedIcon = unSelectedIcon;
    }

    public Drawable getSelectedDrawable() {
        return selectedDrawable;
    }

    public void setSelectedDrawable(Drawable selectedDrawable) {
        this.selectedDrawable = selectedDrawable;
    }

    public Drawable getUnSelectedDrawable() {
        return unSelectedDrawable;
    }

    public void setUnSelectedDrawable(Drawable unSelectedDrawable) {
        this.unSelectedDrawable = unSelectedDrawable;
    }

    public TabEntity(String title, int selectedIcon, int unSelectedIcon) {
        this.title = title;
        this.selectedIcon = selectedIcon;
        this.unSelectedIcon = unSelectedIcon;
    }

    public String getTabTitle() {
        return title;
    }

    public int getTabSelectedIcon() {
        return selectedIcon;
    }

    public int getTabUnselectedIcon() {
        return unSelectedIcon;
    }
}
