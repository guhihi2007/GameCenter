package cn.lt.game.domain.detail;

import cn.lt.game.bean.FeedBackBean;

/**
 * Created by Administrator on 2015/11/19.
 */
public class FeedBackDomainDetail extends FeedBackBean {
    public FeedBackDomainDetail(FeedBackBean bean) {
        this.content = bean.getContent();
        this.created_at = bean.getCreated_at();
        this.identifyUser = bean.getIdentifyUser();
        this.image_url = bean.getImage_url();
        this.thumb_url = bean.getThumb_url();
        this.id = bean.getId();
    }
}
