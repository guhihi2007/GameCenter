package cn.lt.game.domain.detail;

import cn.lt.game.bean.GameCommentBean;

/**
 * Created by Administrator on 2015/11/19.
 */
public class GameCommentDomainDetail extends GameCommentBean {
    public GameCommentDomainDetail(GameCommentBean bean) {
        this.nickname = bean.getNickname();
        this.avatar = bean.getAvatar();
        this.content = bean.getContent();
        this.created_at = bean.getCreated_at();
        this.device = bean.getDevice();
        this.star = bean.getStar();
    }
}
