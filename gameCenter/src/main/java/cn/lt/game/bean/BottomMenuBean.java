package cn.lt.game.bean;

/**
 * Created by Administrator on 2017/8/10.
 */

public class BottomMenuBean {
    private String selected_menu_pic;
    private String unselected_menu_pic;
    private String selected_font_color;
    private String unselected_font_color;
    private String name;
    private int id;

    public String getSelected_menu_pic() {
        return selected_menu_pic;
    }

    public void setSelected_menu_pic(String selected_menu_pic) {
        this.selected_menu_pic = selected_menu_pic;
    }

    public String getUnselected_menu_pic() {
        return unselected_menu_pic;
    }

    public void setUnselected_menu_pic(String unselected_menu_pic) {
        this.unselected_menu_pic = unselected_menu_pic;
    }

    public String getSelected_font_color() {
        return selected_font_color;
    }

    public void setSelected_font_color(String selected_font_color) {
        this.selected_font_color = selected_font_color;
    }

    public String getUnselected_font_color() {
        return unselected_font_color;
    }

    public void setUnselected_font_color(String unselected_font_color) {
        this.unselected_font_color = unselected_font_color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }
}
