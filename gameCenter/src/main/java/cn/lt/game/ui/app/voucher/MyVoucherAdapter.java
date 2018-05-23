package cn.lt.game.ui.app.voucher;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.bean.MyVoucherItemBean;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.detail.GameDomainDetail;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.download.H5DownloadState;
import cn.lt.game.event.DownloadUpdateEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.H5Util;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.web.WebCallBackToObject;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.RefreshAndLoadMoreListView;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.app.adapter.data.PresentData;
import cn.lt.game.ui.app.personalcenter.info.GlideImageLoader;
import cn.lt.game.ui.common.listener.ActivityButtonClickListener;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.common.listener.VoucherButtonListener;
import cn.lt.game.ui.installbutton.ActivityUpdateButtonState;
import cn.lt.game.ui.installbutton.VoucherUpdateButtonState;
import de.greenrobot.event.EventBus;

/**
 * Created by Erosion on 2018/1/16.
 */

public class MyVoucherAdapter extends BaseAdapter {
    private List<MyVoucherItemBean> itemBeans = new ArrayList<>();
    private Context context;
    private LayoutInflater mInflater;
    private boolean isLastPage;

    public MyVoucherAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
        EventBus.getDefault().register(this);
    }

    public void setList(List<MyVoucherItemBean> itemBeans,boolean isLastPage) {
        this.itemBeans = itemBeans;
        this.isLastPage = isLastPage;
    }

    @Override
    public int getCount() {
        return itemBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return itemBeans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void onEventMainThread(DownloadUpdateEvent updateEvent) {
        if (updateEvent == null || updateEvent.game == null) return;
        LogUtils.i("Erosion", "pos:" + updateEvent.game.pos);
        notifyDataSetChanged();
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.my_voucher_item, null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.voucherName = (TextView) convertView.findViewById(R.id.voucher_name);
            holder.timeLimit = (TextView) convertView.findViewById(R.id.time_limit);
            holder.maxMoney = (TextView) convertView.findViewById(R.id.max_money);
            holder.remainingDay = (ImageView) convertView.findViewById(R.id.ticket_time_logo_iv);
            holder.progressBar = (ProgressBar) convertView.findViewById(R.id.download_progress_bar);
            holder.downloadButton = (Button) convertView.findViewById(R.id.grid_item_button);
            holder.downloadRL = (RelativeLayout) convertView.findViewById(R.id.down_load_bar);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final MyVoucherItemBean bean = itemBeans.get(position);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.relativeLayout.getLayoutParams();
        if (isLastPage && position == itemBeans.size() - 1) {
            layoutParams.setMargins(0, 0,0,DensityUtil.dip2px(context,8));
        } else {
            layoutParams.setMargins(0, 0,0,DensityUtil.dip2px(context,0));
        }

        final GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(bean.getGame_id());
        if (downFile != null) {
            VoucherUpdateButtonState installButtonGroup = new VoucherUpdateButtonState(downFile, holder.downloadButton, holder.progressBar);
            installButtonGroup.setViewBy(downFile.getState(), downFile.getDownPercent());
            StatisticsEventData sData = produceStatisticsData(position + 1, bean.getGame_id() + "", Constant.PAGE_MY_VOUCHER, ReportEvent.ACTION_CLICK, null, null, null, bean.getPackage_name());
            holder.downloadButton.setTag(R.id.statistics_data, sData);
        } else {
            VoucherUpdateButtonState installButtonGroup = new VoucherUpdateButtonState(null, holder.downloadButton, holder.progressBar);
            installButtonGroup.setViewBy(DownloadState.undownload, 0);
        }

        holder.downloadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.i("Erosion", "holder.downloadRL" + position + ",id:" + bean.getGame_id());
                if (downFile != null) {
                    LogUtils.i("Erosion", "statue===" + downFile.getState());
                    downFile.pos = position;
                    VoucherUpdateButtonState installButtonGroup = new VoucherUpdateButtonState(downFile, holder.downloadButton, holder.progressBar);
                    installButtonGroup.setViewBy(downFile.getState(), downFile.getDownPercent());
                    VoucherButtonListener listener = new VoucherButtonListener(context, downFile, installButtonGroup, Constant.PAGE_MY_VOUCHER);
                    listener.realOnClick(holder.downloadButton, Constant.PAGE_MY_VOUCHER);
                } else {
                    requestGameDetailData(bean.getGame_id(), position, holder);
                }
            }
        });
        upDateData(holder, bean);
        return convertView;
    }

    private void upDateData(final ViewHolder holder, final MyVoucherItemBean bean) {
        ImageloaderUtil.loadRoundImage(context,bean.getGame_icon(),holder.icon);
        holder.voucherName.setText(bean.getName());
        holder.timeLimit.setText("有效期：" + bean.getStart_at() + "~" + bean.getEnd_at());
        if (bean.getCp_id().equals("0")) {
            holder.downloadRL.setVisibility(View.GONE);
        } else {
            holder.downloadRL.setVisibility(View.VISIBLE);
        }
        if (bean.getMin_money().equals("0")) {
            holder.maxMoney.setText("无门槛使用");
        } else {
            holder.maxMoney.setText("充值满" + bean.getMin_money() + "元可用");
        }

        if (bean.getLast_time() == 3) {
            holder.remainingDay.setVisibility(View.VISIBLE);
            holder.remainingDay.setImageResource(R.mipmap.game_day3);
        } else if (bean.getLast_time() == 2) {
            holder.remainingDay.setVisibility(View.VISIBLE);
            holder.remainingDay.setImageResource(R.mipmap.game_day2);
        } else if (bean.getLast_time() == 1) {
            holder.remainingDay.setVisibility(View.VISIBLE);
            holder.remainingDay.setImageResource(R.mipmap.game_day1);
        } else {
            holder.remainingDay.setVisibility(View.GONE);
        }

    }

    /**
     * 请求游戏详情信息
     */
    private void requestGameDetailData(final int gameId, final int pos, final ViewHolder holder) {
        if (FileDownloaders.couldDownload(context)) {
            Map<String, String> param = new HashMap<>();
            param.put("id", gameId + "");
            Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.getGameDetailUriByIdOrPkgName(gameId + ""), param, new WebCallBackToObject<UIModuleList>() {
                /**
                 * 网络请求出错时调用
                 *
                 * @param statusCode 异常编号
                 * @param error      异常信息
                 */
                @Override
                public void onFailure(int statusCode, Throwable error) {
                    ToastUtils.showToast(context, "游戏不存在或已下架");
                }

                @Override
                protected void handle(UIModuleList uiData) {
                    UIModule uiModule = (UIModule) uiData.get(0);
                    GameDomainDetail domainDetail = (GameDomainDetail) uiModule.getData();
                    GameBaseDetail game = new GameBaseDetail();// 存放游戏信息
                    game.setGameDetail(domainDetail);
                    if (TextUtils.isEmpty(game.getDownUrl())) {
                        ToastUtils.showToast(context, "下载地址不存在");
                        DCStat.downloadFialedEvent(game, "下载地址不存在");
                    } else {
                        LogUtils.i("Erosion", "fefewfewfee");
                        StatisticsEventData sData = produceStatisticsData(pos + 1, gameId + "", Constant.PAGE_MY_VOUCHER, ReportEvent.ACTION_CLICK, null, null, null, game.getPkgName());
                        holder.downloadButton.setTag(R.id.statistics_data, sData);
                        game.pos = pos;
                        VoucherUpdateButtonState installButtonGroup = new VoucherUpdateButtonState(game, holder.downloadButton, holder.progressBar);
                        installButtonGroup.setViewBy(DownloadState.undownload, 0);
                        VoucherButtonListener listener = new VoucherButtonListener(context, game, installButtonGroup, Constant.PAGE_MY_VOUCHER);
                        listener.realOnClick(holder.downloadButton, Constant.PAGE_MY_VOUCHER);

                    }
                }
            });
        } else {
            ToastUtils.showToast(context, "请检查网络！");
        }
    }


    public StatisticsEventData produceStatisticsData(int pos, String id, String pageName, String action, String downloadType, String mark, String srcType, String packageName) {
        StatisticsEventData mStatisticsData = null;
        try {
            mStatisticsData = new StatisticsEventData();
            mStatisticsData.setPresentType("djq");
            mStatisticsData.setSubPos(0);
            mStatisticsData.setPos(pos);
            mStatisticsData.setSrc_id(id);
            mStatisticsData.setPage(pageName);
            mStatisticsData.setActionType(action);
            mStatisticsData.setPackage_name(packageName);
            mStatisticsData.setDownloadType(downloadType);
            mStatisticsData.setRemark(mark);
            mStatisticsData.setSrcType(srcType);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mStatisticsData;
    }

    class ViewHolder {
        ImageView icon;
        TextView voucherName;
        TextView timeLimit;
        TextView maxMoney;
        ImageView remainingDay;
        ProgressBar progressBar;
        Button downloadButton;
        RelativeLayout downloadRL;
        RelativeLayout relativeLayout;
    }
}
