package cn.lt.game.ui.app.community.model;

//分享Bean
public class ShareBean {
    public static final int GAME = 1;
    public static final int CLIENT = 2;
    private String text; // 任何分享都需要 必须的----------------内容
    private String titleurl;// QQ空间需要此内容-----------标题的网络链接
    private String title;
    private String gameIconUrl;

    public int shareType;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTitleurl() {
        return titleurl;
    }

    public void setTitleurl(String titleurl) {
        this.titleurl = titleurl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGameIconUrl() {
        return gameIconUrl;
    }

    public void setGameIconUrl(String gameIconUrl) {
        this.gameIconUrl = gameIconUrl;
    }

}
