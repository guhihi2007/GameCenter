package cn.lt.game.ui.app.selectedgame;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.model.GameBaseDetail;

public class DownloadAdDialog extends Dialog {
    private Context context;
    private GridView gridView;
    private int screenWidth = 0;
    private LinearLayout bodyLl;
    private ArrayList<GameBaseDetail> listBeans = new ArrayList<GameBaseDetail>();
    private DownloadAdAdapter adAdapter;
    private ImageView cancel;
    private Button downloadBtn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;


    public DownloadAdDialog(Context context) {
        super(context, R.style.updateInfoDialogStyle);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_download_advertisement);
        screenWidth = Utils.getScreenWidth(context);
        sharedPreferences = context.getSharedPreferences("setting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putLong(Constant.Other.DOWNLOAD_AD_TIME, System.currentTimeMillis());
        editor.apply();
        init_findViewById();
        init_View();
        init_date();
    }


    private void init_findViewById() {
        bodyLl = (LinearLayout) findViewById(R.id.download_advertisement_body);
        gridView = (GridView) findViewById(R.id.download_advertisement_gridView);
        cancel = (ImageView) findViewById(R.id.download_advertisement_cancelIv);
        downloadBtn = (Button) findViewById(R.id.download_advertisement_downloadBtn);

    }

    private void init_View() {
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (screenWidth * 0.83), FrameLayout.LayoutParams.WRAP_CONTENT);
        bodyLl.setLayoutParams(lp);

        cancel.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                DownloadAdDialog.this.cancel();
            }

        });


        downloadBtn.setOnClickListener(new android.view.View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //开始全部下载
            }

        });
    }

    private void init_date() {
        for (int i = 0; i < 9; i++) {
            GameBaseDetail bean = new GameBaseDetail();
            bean.setName("我是游戏" + i + "youxiyouxiyouxixixixixixix");
            bean.setLogoUrl("http://ecma.bdimg.com/tam-ogel/e855f7860198b7d95cb789541249c4b9_90_90.png");
            listBeans.add(bean);
        }

        adAdapter = new DownloadAdAdapter(context, listBeans);
        gridView.setAdapter(adAdapter);
    }
}
