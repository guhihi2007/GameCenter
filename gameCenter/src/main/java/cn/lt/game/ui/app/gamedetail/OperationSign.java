package cn.lt.game.ui.app.gamedetail;

import cn.lt.game.R;

/**
 * Created by nohc on 2017/10/30.
 * 运营标志
 */

public enum OperationSign {
    bbs("社区", R.drawable.shape_sign_bbs, R.color.sign_bbs),
    gift("礼包", R.drawable.shape_sign_gift, R.color.sign_gift),
    strategy("攻略", R.drawable.shape_sign_strategy, R.color.sign_strategy),
    official("官方", R.drawable.shape_sign_official, R.color.sign_official);

    private String sign;
    private int backgroundRes;
    private int colorRes;

    OperationSign(String sign, int resID, int colorRes) {
        this.sign = sign;
        this.backgroundRes = resID;
        this.colorRes = colorRes;
    }

    public String getSign() {
        return sign;
    }

    public int getBackgroundRes() {
        return backgroundRes;
    }

    public int getColorRes() {
        return colorRes;
    }
}
