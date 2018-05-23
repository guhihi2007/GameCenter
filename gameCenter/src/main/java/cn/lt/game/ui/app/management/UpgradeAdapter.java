package cn.lt.game.ui.app.management;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.AdapterBase;
import cn.lt.game.base.Item;
import cn.lt.game.download.DownloadState;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.global.Constant;
import cn.lt.game.install.InstallState;
import cn.lt.game.lib.ScreenUtils;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.model.AppInfo;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import cn.lt.game.ui.common.listener.UpgradeButtonClickListener;
import cn.lt.game.ui.installbutton.ManageUpgradeButton;

public class UpgradeAdapter extends AdapterBase<Item> {

    public static final int TYPE_LABEL_IGNORE = -1;
    public static final int TYPE_LABEL_UPGRADE = 0;
    public static final int TYPE_GAME = 1;

    private UpgradeFragment upgradeFragment;
    private InstalledAdapter.OnItemMoreClickListener mListener;

    private SparseBooleanArray mCollapseStatus;
    private boolean isIgnoreGamesShow;
    private List<Item> mIgnoreItems;

    public void setOnItemMoreClickListener(InstalledAdapter.OnItemMoreClickListener mListener) {
        this.mListener = mListener;
    }

    public UpgradeAdapter(Context context, UpgradeFragment upgradeFragment) {
        super(context);
        this.upgradeFragment = upgradeFragment;
        mCollapseStatus = new SparseBooleanArray();
        mIgnoreItems = new ArrayList<>();
    }


    public void setList(List<Item> list, List<GameBaseDetail> mIgnoreGames) {
        mIgnoreItems.clear();
        for (GameBaseDetail game : mIgnoreGames) {
            Item item = new Item(UpgradeAdapter.TYPE_GAME, game);
            mIgnoreItems.add(item);
            if (isIgnoreGamesShow) {
                list.add(item);
            }
        }
        super.setList(list);
    }


    @Override
    public int getItemViewType(int position) {
        return getList().get(position).viewType;
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        switch (getItemViewType(position)) {
            case TYPE_LABEL_IGNORE:
                final LabelViewHolder h;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.management_label_ignore_item, parent, false);
                    h = new LabelViewHolder(convertView);
                    convertView.setTag(h);
                    convertView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (isIgnoreGamesShow) {
                                getList().removeAll(mIgnoreItems);
                            } else {
                                getList().addAll(mIgnoreItems);
                            }
                            isIgnoreGamesShow = !isIgnoreGamesShow;
                            notifyDataSetChanged();
                        }
                    });
                } else {
                    h = (LabelViewHolder) convertView.getTag();
                }
                bindLabelView(position, h);
                break;
            case TYPE_LABEL_UPGRADE:
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
                final ViewHolder h2;
                if (convertView == null) {
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.management_update_game, parent, false);
                    h2 = new ViewHolder(convertView);
                    convertView.setTag(h2);
                } else {
                    h2 = (ViewHolder) convertView.getTag();
                }
                bindGameView(position, h2);
                break;
        }
        return convertView;
    }

    private void bindLabelView(int position, LabelViewHolder h) {
        String label = (String) getList().get(position).data;
        h.name.setText(label);

        if (h.status != null) {
            // 当前是否是显示状态
            if (isIgnoreGamesShow) {
                h.status.setText(R.string.collapse_text1);
                h.expandCollapse.setImageResource(R.mipmap.drawable_collapse);
            } else {
                h.status.setText(R.string.expand_text1);
                h.expandCollapse.setImageResource(R.mipmap.drawable_expand);
            }

            h.gap.setVisibility(position == getCount() - 1 ? View.VISIBLE : View.GONE);
        }
    }

    private Map<String,AppInfo> appInfoMap = new HashMap<>();

    private void bindGameView(final int position, final ViewHolder h) {
        final GameBaseDetail gameBaseDetail = (GameBaseDetail) getList().get(position).data;
        AppInfo appInfo = appInfoMap.get(gameBaseDetail.getPkgName());
        if (appInfo == null) {
            appInfo = new AppInfo(mContext, gameBaseDetail.getPkgName());
            appInfoMap.put(gameBaseDetail.getPkgName(),appInfo);
        }

        if (appInfo.getIcon() != null) {
            h.managementGameUpdataIcon.setImageDrawable(appInfo.getIcon());
        } else {
            h.managementGameUpdataIcon.setImageResource(R.mipmap.img_default_80x80_round);
        }
//        ImageloaderUtil.loadRoundImage(mContext, gameBaseDetail.getLogoUrl(), h.managementGameUpdataIcon);
        String name = gameBaseDetail.getName();
        h.managementGameUpdataName.setText(TextUtils.isEmpty(name) ? appInfo.getName() : name);
        // SpannableString,ImageSpan实现图文混排 跑马灯效果
        Drawable drawable = mContext.getResources().getDrawable(R.mipmap.upgrade_version_arrow);
        drawable.setBounds(0, 0, (int) ScreenUtils.dpToPx(mContext, 10), (int) ScreenUtils.dpToPx(mContext, 10));
        //需要处理的文本，→是需要被替代的文本
        String text = appInfo.getVersion() + " → " + gameBaseDetail.getVersion();
        SpannableString spannable = new SpannableString(text);
        spannable.setSpan(new ForegroundColorSpan(Color.rgb(0xff, 0x88, 0x00)), text.lastIndexOf("→") + 1, text.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

        //要让图片替代指定的文字就要用ImageSpan
        ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
        //开始替换，注意第2和第3个参数表示从哪里开始替换到哪里替换结束（start和end）
        //最后一个参数类似数学中的集合,[5,12)表示从5到12，包括5但不包括12
        spannable.setSpan(span, text.indexOf("→"), text.indexOf("→") + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        // 判断是否预约wifi下载
        if (FileDownloaders.judgeIsOrderWifiDownload(gameBaseDetail.getId())) {
            h.managementGameVersion.setText("等待WIFI下载");
            h.managementGameUpdataSize.setVisibility(View.GONE);
        } else {
            h.managementGameVersion.setText(spannable);
            h.managementGameUpdataSize.setVisibility(View.VISIBLE);
            h.managementGameUpdataSize.setText(gameBaseDetail.getPkgSizeInM());
        }
        String content = gameBaseDetail.getUpdateContent();
        Spanned html = Html.fromHtml(TextUtils.isEmpty(content) ? "优化游戏启动速度，修复已知问题" : gameBaseDetail.getUpdateContent());
        h.newFeature.setText(html, mCollapseStatus, gameBaseDetail.getId());
        h.newFeature.setOnExpandStateChangeListener(new ExpandableTextView.OnExpandStateChangeListener() {
            @Override
            public void onExpandStateChanged(TextView textView, boolean isExpanded) {
                if (!isExpanded) {
                    h.newFeature.setText(Html.fromHtml(gameBaseDetail.getUpdateContent()), mCollapseStatus, gameBaseDetail.getId());
                }
            }
        });
        refreshButton(h, gameBaseDetail);
        //给按钮设置统计数据
        StatisticsEventData mStatisticsData = new StatisticsEventData();
        mStatisticsData.setActionType(ReportEvent.ACTION_CLICK);
        mStatisticsData.setPos(-1);
        mStatisticsData.setSrc_id(gameBaseDetail.getId() + "");
        mStatisticsData.setSubPos(position);
        mStatisticsData.setPackage_name(gameBaseDetail.getPkgName());
        h.installBtn.setTag(R.id.statistics_data, mStatisticsData);  //进入下载管理后，位置默认为空
        h.managementGameUpdataTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.JumpToGameDetail(mContext, gameBaseDetail.getId());
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.PAGE_MANGER_UPGRADE, 0, null, 0, gameBaseDetail.getId() + "", null, null, null, gameBaseDetail.getPkgName(),""));
            }
        });

        boolean flag = gameBaseDetail.getState() == InstallState.upgrade || gameBaseDetail.getState() == InstallState.ignore_upgrade;
        h.managementGameVersion.setVisibility(flag ? View.VISIBLE : View.GONE);
        h.networkIndication.setVisibility(flag ? View.GONE : View.VISIBLE);
        h.moreOperation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onclick(position, h.moreOperation, gameBaseDetail);
                }
            }
        });
        h.moreOperation.setVisibility(gameBaseDetail.getState() == InstallState.installing ? View.GONE : View.VISIBLE);
        h.gap.setVisibility(position == getCount() - 1 ? View.VISIBLE : View.GONE);
    }

    private void refreshButton(final ViewHolder h, final GameBaseDetail gameBaseDetail) {
        GameBaseDetail downFile = FileDownloaders.getDownFileInfoById(gameBaseDetail.getId());
        if (downFile != null) {
            gameBaseDetail.setDownInfo(downFile);
        } else {
            gameBaseDetail.setState(DownloadState.undownload);
            gameBaseDetail.setDownLength(0);
        }
        ManageUpgradeButton installButtonGroup = new ManageUpgradeButton(gameBaseDetail, h.installBtn, h.downloadProgressBar, h.managementGameUpdataSize, h.networkIndication, h.managementGameUpdataSize);
        final UpgradeButtonClickListener listener = new UpgradeButtonClickListener(mContext, gameBaseDetail, installButtonGroup, false, upgradeFragment.getPageAlias());
        h.installBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //为什么要判断是在WiFi网络下隐藏，1.是为了防止点击升级按钮闪2.是为了移动网络下能显示版本号
                if (NetUtils.isWifiNet(mContext)) {
                    h.managementGameVersion.setVisibility(View.GONE);
                }
                notifyDataSetChanged();
                listener.onClick(v);
            }
        });
        int state = gameBaseDetail.getState();
        int time = gameBaseDetail.getDownTimeLeft();
        String speed = gameBaseDetail.getDownSpeedWithKbOrMb();
        installButtonGroup.setViewBy(state, gameBaseDetail.getDownPercent(), speed + TimeUtils.formatIntToTimeStr(time));

    }

    public void updateView(View view, int position) {
        if (view == null) return;
        Object tag = view.getTag();
        if (tag == null) return;
        if (tag instanceof ViewHolder) {
            refreshButton((ViewHolder) tag, (GameBaseDetail) getList().get(position).data);
            boolean versionCanShow = ((GameBaseDetail) getList().get(position).data).getState() == InstallState.upgrade || ((GameBaseDetail) getList().get(position).data).getState() == InstallState.ignore_upgrade;
            ((ViewHolder) tag).managementGameVersion.setVisibility(versionCanShow ? View.VISIBLE : View.GONE);
        }
    }

    public final class ViewHolder {
        ImageView managementGameUpdataIcon;
        ImageView moreOperation;
        TextView managementGameUpdataName;
        TextView managementGameVersion;
        TextView managementGameUpdataSize;
        ImageView networkIndication;
        RelativeLayout managementGameUpdataTop;
        ProgressBar downloadProgressBar;
        Button installBtn;
        ExpandableTextView newFeature;
        View gap;

        public ViewHolder(View itemView) {
            managementGameUpdataTop = (RelativeLayout) itemView.findViewById(R.id.management_gameUpdata_top);
            managementGameUpdataIcon = (ImageView) itemView.findViewById(R.id.management_gameUpdata_icon);
            moreOperation = (ImageView) itemView.findViewById(R.id.btn_more);
            managementGameUpdataName = (TextView) itemView.findViewById(R.id.management_gameUpdata_name);
            managementGameVersion = (TextView) itemView.findViewById(R.id.management_game_local_version);
            managementGameUpdataSize = (TextView) itemView.findViewById(R.id.management_gameUpdata_size);
            networkIndication = (ImageView) itemView.findViewById(R.id.network_indication);
            downloadProgressBar = (ProgressBar) itemView.findViewById(R.id.download_progress_bar);
            installBtn = (Button) itemView.findViewById(R.id.install_btn);
            newFeature = (ExpandableTextView) itemView.findViewById(R.id.expand_text_view);
            gap = itemView.findViewById(R.id.gap);
        }
    }


    public final class LabelViewHolder {
        TextView name;
        TextView status;
        ImageView expandCollapse;
        View gap;

        public LabelViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.name);
            status = (TextView) itemView.findViewById(R.id.expand_collapse);
            expandCollapse = (ImageView) itemView.findViewById(R.id.iv_expand_collapse);
            gap = itemView.findViewById(R.id.gap_upgrade);
        }
    }

    public void remove(int position) {
        if (position < getCount()) {
            getList().remove(position);
            notifyDataSetChanged();
        }
    }


}
