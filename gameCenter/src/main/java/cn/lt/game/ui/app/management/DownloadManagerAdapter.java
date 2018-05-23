package cn.lt.game.ui.app.management;

import android.content.Context;
import android.net.ConnectivityManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.base.AdapterBase;
import cn.lt.game.base.Item;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.ManageInstallButton;
import cn.lt.game.ui.notification.LTNotificationManager;
import de.greenrobot.event.EventBus;

public class DownloadManagerAdapter extends AdapterBase<Item> {

    static final int TYPE_LABEL = 0;
    static final int TYPE_GAME = 1;

    DownloadManagerAdapter(Context context) {
        super(context);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        return getList().get(position).viewType;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_LABEL:
                LabelViewHolder h;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.management_label_item, parent, false);
                    h = new LabelViewHolder(convertView);
                    convertView.setTag(h);
                } else {
                    h = (LabelViewHolder) convertView.getTag();
                }
                bindLabelView(position, h);
                break;
            case TYPE_GAME:
                ViewHolder h2;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.management_download, parent, false);
                    h2 = new ViewHolder(convertView);
                    convertView.setTag(h2);
                } else {
                    h2 = (ViewHolder) convertView.getTag();
                }
                bindGameView(position, h2, true);
                break;
        }
        return convertView;
    }

    private void bindGameView(final int position, final ViewHolder h, boolean isLoadImage) {
        final GameBaseDetail game = (GameBaseDetail) getList().get(position).data;
        h.name.setText(game.getName());
        h.managementDownSize.setText(game.getPkgSizeInM());

        if (isLoadImage) {
            ImageloaderUtil.loadLTLogo(mContext, game.getLogoUrl(), h.icon);
        }

        GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(game.getId());
        if (downFile != null) {
            game.setDownInfo(downFile);
        } else {
            game.setState(DownloadState.undownload);
            game.setDownLength(0);
        }

        ManageInstallButton installButtonGroup = new ManageInstallButton(game, h.installBtn, h.downloadProgressBar, h.speed, h.networkIndication, h.managementDownSize);
        InstallButtonClickListener listener = new InstallButtonClickListener(mContext, game, installButtonGroup, false, Constant.PAGE_MANGER_DOWNLOAD);
        //给按钮设置统计数据
        StatisticsEventData mStatisticsData = new StatisticsEventData();
        mStatisticsData.setActionType(ReportEvent.ACTION_CLICK);
        mStatisticsData.setPos(-1);
        mStatisticsData.setSrc_id(game.getId() + "");
        mStatisticsData.setSubPos(position);
        mStatisticsData.setPackage_name(game.getPkgName());
        h.installBtn.setTag(R.id.statistics_data, mStatisticsData);  //进入下载管理后，位置默认为空
        h.installBtn.setOnClickListener(listener);

        //内存空间不足，图标显示为错误图标
        if (!h.speed.getText().toString().equals(game.getDownloadFailedReason())
                //不是安装包错误提示
                && !h.speed.getText().toString().equals(mContext.getString(R.string.install_fail_retry))) {
            // 网络状态 图标
            switch (NetUtils.getNetType(mContext)) {
                case -1:// 无网络状态
                    h.networkIndication.setVisibility(View.GONE);
                    break;
                case ConnectivityManager.TYPE_WIFI:
                    h.networkIndication.setVisibility(View.VISIBLE);
                    h.networkIndication.setImageResource(R.mipmap.wifi_indication);
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    h.networkIndication.setVisibility(View.VISIBLE);
                    h.networkIndication.setImageResource(R.mipmap.ng_indication);
                    break;
                default:
                    break;
            }
        } else {
            h.networkIndication.setVisibility(View.VISIBLE);
            h.networkIndication.setImageResource(R.mipmap.ic_error);
            //不显示size
            h.managementDownSize.setText("");
        }

        int state = game.getState();
        int time = game.getDownTimeLeft();
        String speed = game.getDownSpeedWithKbOrMb();
        installButtonGroup.setViewBy(state, game.getDownPercent(), speed + TimeUtils.formatIntToTimeStr(time));


        h.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleClick(game, position);
            }
        });

        h.gameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.JumpToGameDetail(mContext, game.getId());
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.PAGE_MANGER_DOWNLOAD, 0, null, 0, game.getId() + "", null, null, null, game.getPkgName(),""));
            }
        });


        h.gap.setVisibility(position == getList().size() - 1 ? View.VISIBLE : View.GONE);
    }

    private void bindLabelView(int position, LabelViewHolder h) {
        String label = (String) getList().get(position).data;
        h.name.setText(label);
    }


    private void toggleClick(final GameBaseDetail game, final int pos) {
        final MessageDialog exitDialog = new MessageDialog(mContext, "温馨提示", "确认删除该下载任务?", "取消", "确定");
        exitDialog.show();
        exitDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {

            @Override
            public void OnClick(View view) {
                // 退出程序
                if (game.getState() == InstallState.install) {
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.PAGE_MANGER_PALY, pos, "game", 0, game.getId() + "", null, Constant.RETRY_TYPE_MANUAL, "installDelete", game.getPkgName(),""));
                } else {
                    DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.PAGE_MANGER_PALY, pos, "game", 0, game.getId() + "", null, Constant.RETRY_TYPE_MANUAL, "downDelete", game.getPkgName(),""));
                }
//                getList().remove(pos);
//                notifyDataSetChanged();
                LTNotificationManager.getinstance().deleteGameNotification(game);
                FileDownloaders.remove(game.getDownUrl(), true);
                FileDownloaders.downloadNext();
                exitDialog.dismiss();
                EventBus.getDefault().post(new DownloadUpdateEvent(game, DownloadUpdateEvent.EV_DELETE));

            }
        });
        exitDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {

            @Override
            public void OnClick(View view) {
                exitDialog.dismiss();
            }
        });
    }


    public void updateView(View view, int position) {
        if (view == null) return;
        Object tag = view.getTag();
        if (tag == null) return;
        if (tag instanceof ViewHolder) {
            bindGameView(position, (ViewHolder) tag, false);
        }
    }

    public final class ViewHolder {

        RelativeLayout gameItem;
        ImageView icon;
        ImageView cancel;
        TextView name;
        ImageView networkIndication;
        TextView speed;
        TextView managementDownSize;
        //        RelativeLayout uninstallLayout;
        ProgressBar downloadProgressBar;
        Button installBtn;
        View gap;


        public ViewHolder(View itemView) {
            gameItem = (RelativeLayout) itemView.findViewById(R.id.game_item);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            cancel = (ImageView) itemView.findViewById(R.id.btn_cancel);
            name = (TextView) itemView.findViewById(R.id.name);
            networkIndication = (ImageView) itemView.findViewById(R.id.network_indication);
            speed = (TextView) itemView.findViewById(R.id.speed);
            managementDownSize = (TextView) itemView.findViewById(R.id.management_down_size);
            downloadProgressBar = (ProgressBar) itemView.findViewById(R.id.download_progress_bar);
            installBtn = (Button) itemView.findViewById(R.id.install_btn);
            gap = itemView.findViewById(R.id.gap);
        }
    }

    public final class LabelViewHolder {
        TextView name;

        LabelViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.name);
        }
    }


}
