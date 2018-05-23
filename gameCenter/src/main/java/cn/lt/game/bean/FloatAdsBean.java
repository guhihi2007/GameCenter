package cn.lt.game.bean;

/**
 * Created by chon on 2017/6/8.
 * What? How? Why?
 */

public class FloatAdsBean {

    public String id;               // 广告id
    public String title;            // 广告title
    public int click_type;          // 类型（1.仅下载 2.仅跳转 3.跳转+下载）
    public String ads_icon;         // 广告图

    public String resource_id;
    public String jump_type;        // 跳转类型
    public String download_url;     // 游戏下载地址
    public String package_name;     // 游戏包名

    public String name;             // 游戏名字
    public String reviews;          // 小编点评
    public String download_count;   // 下载次数
    public String package_md5;      // 游戏包MD5
    public long package_size;       // 游戏包大小
    public String alias;            // 游戏别名
    public String icon_url;         // 游戏icon
    public int version_code;        // 游戏版本

    public String link;             // 跳转H5链接

}
