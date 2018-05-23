package cn.lt.game.ui.app.personalcenter.model;


public class UserBaseInfo implements Cloneable {
    private int id;
    private String avatar;
    private String mobile;
    private String email;
    private String nickname;
    private String sex;
    private long birthday;
    private String address;
    private String token;
    private String userName;

    private String exp;
    private String gold;
    private String level;
    private String summary;
    // 升级所需经验
    private String offsetExp;
    //邮箱是否验证
    private int email_auth;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public long getBirthday() {
        if (birthday < 0) {
            return 0;
        }
        return birthday * 1000;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday / 1000;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getGold() {
        return gold;
    }

    public void setGold(String gold) {
        this.gold = gold;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getOffsetExp() {
        return offsetExp;
    }

    public void setOffsetExp(String offsetExp) {
        this.offsetExp = offsetExp;
    }

    public int getEmail_auth() {
        return email_auth;
    }

    public void setEmail_auth(int email_auth) {
        this.email_auth = email_auth;
    }

    @Override
    public UserBaseInfo clone() {
        UserBaseInfo user = null;
        try {
            user = (UserBaseInfo) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return user;
    }

    @Override
    public String toString() {
        return "UserBaseInfo{" +
                "id=" + id +
                ", avatar='" + avatar + '\'' +
                ", mobile='" + mobile + '\'' +
                ", email='" + email + '\'' +
                ", nickname='" + nickname + '\'' +
                ", sex='" + sex + '\'' +
                ", birthday=" + birthday +
                ", address='" + address + '\'' +
                ", token='" + token + '\'' +
                ", userName='" + userName + '\'' +
                ", exp='" + exp + '\'' +
                ", gold='" + gold + '\'' +
                ", level='" + level + '\'' +
                ", summary='" + summary + '\'' +
                ", offsetExp='" + offsetExp + '\'' +
                ", email_auth=" + email_auth +
                '}';
    }
}
