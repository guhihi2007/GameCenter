package cn.lt.game.bean;

/**
 * 新的下载地址和md5
 */
public class NewUrlBean {
    private int game_id;
    private String download_url;
    private String game_md5;

    public int getGame_id() {
        return game_id;
    }

    public void setGame_id(int game_id) {
        this.game_id = game_id;
    }

    public String getDownload_url() {
        return download_url;
    }

    public void setDownload_url(String download_url) {
        this.download_url = download_url;
    }

    public String getGame_md5() {
        return game_md5;
    }

    public void setGame_md5(String game_md5) {
        this.game_md5 = game_md5;
    }
}
