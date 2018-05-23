package cn.lt.game.ui.app.management;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.base.AdapterBase;
import cn.lt.game.global.Constant;
import cn.lt.game.install.ApkUninstaller;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.model.AppInfo;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;

public class InstalledAdapter extends AdapterBase<GameBaseDetail> {
    private OnItemMoreClickListener mListener;

    public InstalledAdapter(Context context, List<GameBaseDetail> installedGameList) {
        super(context, installedGameList);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder h;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.management_installed_item, parent, false);
            h = new ViewHolder(convertView);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }
        bindGameView(position, h);
        return convertView;
    }

    private Map<String,AppInfo> appInfoHashMap = new HashMap<>();

    private void bindGameView(final int position, final ViewHolder h) {
        final GameBaseDetail gameBaseDetail = getList().get(position);

        AppInfo appInfo = appInfoHashMap.get(gameBaseDetail.getPkgName());
        if (appInfo == null) {
            appInfo = new AppInfo(mContext, gameBaseDetail.getPkgName());
            appInfoHashMap.put(gameBaseDetail.getPkgName(),appInfo);
        }

        String name = gameBaseDetail.getName();
        h.name.setText(TextUtils.isEmpty(name) ? appInfo.getName() : name);
        h.tag.setText(gameBaseDetail.getReview());

        try {
            int downloadCount = Integer.parseInt(gameBaseDetail.getDownloadCnt());
            String downloadCountStr = IntegratedDataUtil.calculateCountsV4(downloadCount);
            String gameSize = IntegratedDataUtil.calculateSizeMB(gameBaseDetail.getPkgSize());
            h.countSize.setText(downloadCountStr + "  " + gameSize);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        if (appInfo.getIcon() != null) {
            h.icon.setImageDrawable(appInfo.getIcon());
        } else {
            h.icon.setImageResource(R.mipmap.img_default_80x80_round);
        }

        h.unInstallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApkUninstaller.uninstall(mContext, gameBaseDetail.getPkgName());
                DCStat.clickEvent(
                        new StatisticsEventData(
                                ReportEvent.ACTION_CLICK,
                                Constant.PAGE_MANGER_PALY,
                                0,
                                null,
                                0,
                                gameBaseDetail.getId() + "",
                                null,
                                Constant.RETRY_TYPE_MANUAL,
                                "uninstallClick",gameBaseDetail.getPkgName(),""));
            }
        });
        h.gameItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.JumpToGameDetail(mContext, gameBaseDetail.getId());
                DCStat.clickEvent(
                        new StatisticsEventData(
                                ReportEvent.ACTION_CLICK, Constant.PAGE_MANGER_PALY,0, null,
                                0, gameBaseDetail.getId() + "",null,null,null, gameBaseDetail.getPkgName(),""));
            }
        });
        h.more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onclick(position, h.more, gameBaseDetail);
                }
            }
        });

        h.gapTop.setVisibility(position == 0 ? View.VISIBLE : View.GONE);
        h.gapBottom.setVisibility(position == getCount() - 1 ? View.VISIBLE : View.GONE);

    }

    public final class ViewHolder {
        RelativeLayout gameItem;
        ImageView icon;
        ImageView more;
        TextView name;
        Button unInstallBtn;
        TextView countSize;
        TextView tag;
        View gapTop,gapBottom;

        public ViewHolder(View itemView) {
            gameItem = (RelativeLayout) itemView.findViewById(R.id.game_item);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            more = (ImageView) itemView.findViewById(R.id.btn_more);
            name = (TextView) itemView.findViewById(R.id.name);
            unInstallBtn = (Button) itemView.findViewById(R.id.uninstall_btn);
            countSize = (TextView) itemView.findViewById(R.id.countSize);
            tag = (TextView) itemView.findViewById(R.id.tag);
            gapTop = itemView.findViewById(R.id.gap_top);
            gapBottom = itemView.findViewById(R.id.gap_bottom);
        }

    }


    interface OnItemMoreClickListener {
        void onclick(int position, View view, GameBaseDetail gameBaseDetail);
    }

    public void setOnItemMoreClickListener(OnItemMoreClickListener mListener) {
        this.mListener = mListener;
    }
}
