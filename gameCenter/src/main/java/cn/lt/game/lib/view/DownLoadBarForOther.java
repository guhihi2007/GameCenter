package cn.lt.game.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import cn.lt.game.R;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.install.InstallState;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.ui.app.gamedetail.DrawableCenterTextView;
import cn.lt.game.ui.common.WeakView;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.DetailInstallButton;

public class DownLoadBarForOther extends FrameLayout {
    /* 界面控件 */
    private ProgressBar downProgress;
    private DrawableCenterTextView btnDownloadCtrl;
    private DetailInstallButton installButton;
    private GameBaseDetail game;
    private Context context;

    public DownLoadBarForOther(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        LayoutInflater.from(context).inflate(R.layout.downloadbar_layout, this);
        this.context = context;
        initView();
        initWeakView();
    }

    private void initState(Context context, String pageName) {
        // TODO Auto-generated method stub
        installButton = new DetailInstallButton(game, downProgress, btnDownloadCtrl, "下载游戏");

        btnDownloadCtrl.setOnClickListener(new InstallButtonClickListener(context, game, installButton,pageName));

    }

    private void initView() {
        btnDownloadCtrl = (DrawableCenterTextView) findViewById(R.id.btn_download_ctrl);
        downProgress = (ProgressBar) findViewById(R.id.download_progress_bar);
    }

    private void initWeakView() {
        new WeakView<DownLoadBarForOther>(this) {

            @Override
            public void onEventMainThread(DownloadUpdateEvent updateEvent) {
                if (updateEvent == null || updateEvent.game == null || game == null) return;
                if (updateEvent.game.getId() == game.getId()) {
                    updateProgressState();
                }
            }
        };
    }


    public void updateProgressState() {
        if (game == null) {
            return;
        }

        GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(game.getId());
        if (downFile != null) {
            game.setDownInfo(downFile);
        } else {
            game.setState(DownloadState.undownload);
            game.setDownLength(0);
        }
        int state = game.getState();
        if (game.isCoveredApp) {
            state = InstallState.installComplete;
        }
        installButton.setViewBy(state, game.getDownPercent());
    }

    public void initDownLoadBar(GameBaseDetail game, String pageName) {
        this.game = game;
        initState(context, pageName);
        // 更新安装按键状态
        updateProgressState();
    }

    public interface PullToBottomCallBack{
        void pullToBottom();

    }

    /**
     * 自动滚动到底部回调
     */
    private PullToBottomCallBack pullToBottomCallBack;

    public void setPullToBottomCallBack(PullToBottomCallBack pullToBottomCallBack) {
        this.pullToBottomCallBack = pullToBottomCallBack;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (MotionEvent.ACTION_UP == event.getAction()) {
            if (pullToBottomCallBack!=null){
                pullToBottomCallBack.pullToBottom();
            }
        }
        return super.dispatchTouchEvent(event);
    }


    /**
     * 自动点击下载
     */
    public void goDownloadGame() {
        btnDownloadCtrl.performClick();
    }
}
