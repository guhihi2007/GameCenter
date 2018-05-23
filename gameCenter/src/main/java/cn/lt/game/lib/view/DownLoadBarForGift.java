package cn.lt.game.lib.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import cn.lt.game.R;
import cn.lt.game.db.operation.FavoriteDbOperator;
import cn.lt.game.db.service.DownFileService;
import cn.lt.game.domain.detail.GiftDomainDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.install.InstallState;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.ui.app.gamedetail.DrawableCenterTextView;
import cn.lt.game.ui.app.gamegift.GiftManger;
import cn.lt.game.ui.common.WeakView;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.DetailInstallButton;

public class DownLoadBarForGift extends FrameLayout  {
	/* 界面控件 */
	private ProgressBar downProgress;
	
	private DrawableCenterTextView btnDownloadCtrl;
	
	private DetailInstallButton installButton;
	
	private GameBaseDetail  mGame;
	
	private Context context;
	
	

	public DownLoadBarForGift(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.downloadbar_layout, this);
		/* 初始化收藏按钮的监听事件 */
		this.context = context;
		initView();
		initWeakView();
	}

	private void initState(Context context,String tableName,String pageName) {
		if(tableName == FavoriteDbOperator.GAMEDETAIL_TABLE_NAME){
			installButton = new DetailInstallButton(mGame, downProgress, btnDownloadCtrl,
					"下载");
		}else{
			installButton = new DetailInstallButton(mGame, downProgress, btnDownloadCtrl,
					"下载游戏");
		}
	
		btnDownloadCtrl.setOnClickListener(new InstallButtonClickListener(
				context,mGame, installButton,pageName));
	}

	private void initView() {
		btnDownloadCtrl = (DrawableCenterTextView) findViewById(R.id.btn_download_ctrl);
		downProgress = (ProgressBar) findViewById(R.id.download_progress_bar);

	}

	private void initWeakView() {
		new WeakView<DownLoadBarForGift>(this) {

			@Override
			public void onEventMainThread(DownloadUpdateEvent updateEvent) {
				if (updateEvent == null || updateEvent.game == null || mGame == null) return;
				if (updateEvent.game.getId() == mGame.getId()) {
					updateProgressState();
				}
			}
		};
	}


	public void updateProgressState() {
		if(mGame ==null){
			return;
		}
			
		GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(mGame
				.getId());
		if (downFile != null) {
			mGame.setDownInfo(downFile);
		} else {
			mGame.setState(DownloadState.undownload);
			mGame.setDownLength(0);
		}
		int state = mGame.getState();

		if (mGame.isCoveredApp) {
			state = InstallState.installComplete;
		}

		if(state == InstallState.installComplete){
			DownFileService DownFileService = new DownFileService(context);
			DownFileService.updateOpenTimeByPackName(mGame.getPkgName(),System.currentTimeMillis());
		}
		installButton.setViewBy(state, mGame.getDownPercent());

	}

	public void initDownLoadBar(GiftDomainDetail gift,String tableName,String pageName) {
		this.mGame = GiftManger.giftGame2GameDetail(gift.getGame(), context);
		initState(context,tableName,pageName);
		// 更新安装按键状态
		updateProgressState();
	}

}
