package cn.lt.game.domain.essence;

import java.util.Map;

/**
 * Created by wcn on 2015/11/17.
 */
public abstract class UIModuleEssence implements Essence {
    public abstract Map<ImageType, String> getImageUrl();

    public abstract String getTitle();

    public abstract String getColor();

    public abstract String getImage();

    public abstract String getPage_name_410();

    public abstract String getData();

    public abstract String getHighClickType();

    public String getSummary() {
        return null;
    }

    public String getUpdateTime() {
        return null;
    }

    public String getEndTime() {
        return null;
    }


}
