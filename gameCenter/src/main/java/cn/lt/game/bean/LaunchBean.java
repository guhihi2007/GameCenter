package cn.lt.game.bean;

/***
 * Created by Administrator on 2015/12/19.
 */
public class LaunchBean {
    private int id;
    private String title;
    private String image;
    private String url;
    private GameInfoBean game;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public GameInfoBean getGame() {
        return game;
    }

    public void setGame(GameInfoBean game) {
        this.game = game;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
