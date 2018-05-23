package cn.lt.game.domain.essence;

import java.util.List;
import java.util.Map;

import cn.lt.game.model.EntryPages;

/**
 * Created by Administrator on 2015/11/18.
 */
public class FunctionEssenceImpl extends FunctionEssence {
    private Map<ImageType, String> imageUrl = null;
    private String title = null;
    private String color = null;
    private String summary = null;
    private String updateTime = null;
    private String endTime = null;
    private String image = null;
    private String page_name_410 = null;
    private boolean hasSubFuncEss = false;
    private List<FunctionEssence> subFuncEss = null;
    private String data;

    private String high_click_type;

    @Override
    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FunctionEssenceImpl(DomainType domainType) {
        super(domainType);
    }

    public FunctionEssenceImpl setUniqueIdentifier(String uniqueIdentifier) {
        setUniqueIdentifierByType(IdentifierType.ID, uniqueIdentifier);
        return this;
    }

    public FunctionEssenceImpl setUniqueIdentifierByType(IdentifierType type, String uniqueIdentifier) {
        getDomainEssence().setUniqueIdentifierByType(type, uniqueIdentifier);
        return this;
    }

    public FunctionEssenceImpl setUniqueIdentifierMap(Map<IdentifierType, String> uniqueIdentifier) {
        getDomainEssence().setUniqueIdentifierMap(uniqueIdentifier);
        return this;
    }

    public FunctionEssenceImpl setImageUrl(Map<ImageType, String> imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    @Override
    public Map<ImageType, String> getImageUrl() {
        return imageUrl;
    }

    public FunctionEssenceImpl setTitle(String title) {
        this.title = title;
        return this;
    }

    @Override
    public String getTitle() {
        return title;
    }

    public FunctionEssenceImpl setColor(String color) {
        this.color = color;
        return this;
    }

    @Override
    public String getColor() {
        return color;
    }

    @Override
    public String getImage() {
        return image;
    }

    public FunctionEssenceImpl setImage(String image){
        this.image = image;
        return this;
    }

    @Override
    public String getPage_name_410() {
        return page_name_410;
    }

    public FunctionEssenceImpl setPage_name_410(String page_name_410){
        this.page_name_410 = page_name_410;
        return this;
    }

    public FunctionEssenceImpl setSummary(String summary) {
        this.summary = summary;
        return this;
    }

    @Override
    public String getSummary() {
        return this.summary;
    }

    public FunctionEssenceImpl setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
        return this;
    }

    @Override
    public String getUpdateTime() {
        return updateTime;
    }

    public FunctionEssenceImpl setEndTime(String endTime) {
        this.endTime = endTime;
        return this;
    }

    @Override
    public String getEndTime() {
        return endTime;
    }



    public FunctionEssenceImpl setHasSubFuncEss(boolean hasSub) {
        this.hasSubFuncEss = hasSub;
        return this;
    }

    @Override
    public boolean hasSubFuncEss() {
        return hasSubFuncEss;
    }

    public FunctionEssenceImpl setSubFunctionEssence(List<FunctionEssence> subFunc) {
        this.subFuncEss = subFunc;
        return this;
    }

    @Override
    public List<FunctionEssence> getSubFuncEss() {
        return this.subFuncEss;
    }

    public void setHigh_click_type(String high_click_type) {
        this.high_click_type = high_click_type;
    }

    @Override
    public String getHighClickType() {
        return high_click_type;
    }

    /** 根据Domaintype设置高版本跳转所需要的数据（4.3.0以上）*/
    public void verifyClickTypeDataByDomaintype(String highResource, String title) {
        switch (domainEssence.getDomainType()) {
            case GAME:
            case GIFTDETAIL:
            case GAMEGIFTLIST:
            case SPECIAL_TOPIC:
            case COMMUNITY:
            case ACTIVITY_LIST:
            case ROUTINE_ACTIVITIES:
            case APPLIST: {
                // id、title
                setUniqueIdentifierByType(IdentifierType.ID, highResource);
                break;
            }
            case ACTIVITY:
            case H5:
            case HOT_TAB:
            case HOT_DETAIL:
                //url
                setUniqueIdentifierByType(IdentifierType.URL, highResource);
                break;
            case CAT:
            case TAG:
            case PAGE:
            case KEY_WORD:
            case SPECIAL_TOPIC_LIST:
            default:
                break;
        }
        setTitle(title);
    }

    /** 入口专用--根据点击跳转类型设置高版本跳转所需要的数据（4.3.0以上）*/
    public void verifyClickTypeDataByPageName(String pageName, String highResource) {
        switch (pageName) {
            case EntryPages.applist:
            case EntryPages.topic:
            case EntryPages.game:
            case EntryPages.gift:
            case EntryPages.routine_activity: {
                setUniqueIdentifierByType(IdentifierType.ID, highResource);
                break;
            }
            case EntryPages.hot_tab:
            case EntryPages.hot_detail:
                setUniqueIdentifierByType(IdentifierType.URL, highResource);
                break;

        }
    }

}
