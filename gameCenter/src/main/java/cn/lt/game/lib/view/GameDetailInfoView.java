package cn.lt.game.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.ui.app.gamedetail.OperationSign;

public class GameDetailInfoView extends RelativeLayout {
    private ImageView logoIv;
    private TextView nameTv, type, downloadCtn;
    private GameBaseDetail game;
    private LinearLayout operationSignView;
    public int id;

    public GameDetailInfoView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.gamedetailinfo, this);
        findView();
    }

    public GameDetailInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.gamedetailinfo, this);
        findView();
    }

    private void initView() {
        nameTv.setText(game.getName());
        int downloadCount = 0;
        try {
            downloadCount = Integer.parseInt(game.getDownloadCnt());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        List<String> flags = game.getFlags();
        if (flags != null && flags.size() > 0) {
            // 有运营标识
            String countText = game.getPkgSizeInM() + "  " + IntegratedDataUtil.calculateCountsV4(downloadCount);
            downloadCtn.setText(countText);
            type.setVisibility(GONE);

            for (String flag : flags) {

                OperationSign operationSign;
                try {
                    operationSign = OperationSign.valueOf(flag);
                } catch (IllegalArgumentException e) {
                    // not exist in current version
                    continue;
                }

                TextView sign = new TextView(getContext());
                sign.setTextSize(TypedValue.COMPLEX_UNIT_SP,8);
                sign.setBackgroundResource(operationSign.getBackgroundRes());
                sign.setTextColor(getResources().getColor(operationSign.getColorRes()));
                sign.setText(operationSign.getSign());
                operationSignView.addView(sign);
            }
        } else {
            downloadCtn.setText(IntegratedDataUtil.calculateCountsV4(downloadCount));
            String typeText = game.getPkgSizeInM() + "  " + game.getCategory();
            type.setText(typeText);

            operationSignView.setVisibility(GONE);
        }

        loadLogo();
    }

    private void findView() {
        logoIv = (ImageView) findViewById(R.id.appMsg_logo_Iv);
        downloadCtn = (TextView) findViewById(R.id.tv_download_count);
        nameTv = (TextView) findViewById(R.id.appMsg_gameName);
        type = (TextView) findViewById(R.id.tv_game_category);
        operationSignView = (LinearLayout) findViewById(R.id.operation_sign);
    }

    public ImageView getLogoIv() {
        return logoIv;
    }

    public void setLogoIv(ImageView logoIv) {
        this.logoIv = logoIv;
    }

    public GameBaseDetail getGame() {
        return game;
    }

    public void setGame(GameBaseDetail game) {
        this.game = game;
        initView();
    }

    private void loadLogo() {
        ImageloaderUtil.loadRoundImage(getContext(), game.getLogoUrl(), logoIv);
    }
}
