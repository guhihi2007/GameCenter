package cn.lt.game.ui.app.index;

import cn.lt.game.bean.GameInfoBean;
import cn.lt.game.model.GameBaseDetail;

public class IndexUtil {

    public static GameBaseDetail jsonGameToDownloadGame(GameInfoBean data) {
        if (data == null) {
            return null;
        }
        GameBaseDetail bean = new GameBaseDetail();
        bean.setId(Integer.valueOf(data.getId()));
        bean.setName(data.getName());
        bean.setCategory(data.getCat_name());
        bean.setLogoUrl(data.getIcon_url());
        bean.setPkgSize(data.getPackage_size());
        bean.setMd5(data.getPackage_md5());
        bean.setVersion(data.getVersion_name());
        bean.setVersionCode(data.getVersion_code());
        bean.setUpdateContent(data.getMark());
        bean.setPkgName(data.getPackage_name());
        bean.setDownUrl(data.getDownload_url());
        bean.setDownloadCnt(Integer.valueOf(data.getDownload_count()));
        bean.setScore(data.getComments());
        bean.setReview(data.getReviews());
        bean.setHasGift(data.isHas_gifts());
        bean.setForumId(data.getGroup_id());
        return bean;
    }
}
