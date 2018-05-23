package cn.lt.game.ui.app.awardpoints;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.bean.PointsRecord;
import cn.lt.game.bean.PointsRecordNet;

import static cn.lt.game.db.service.DownFileService.mContext;


public class PointsRecordAdapter extends BaseAdapter {

    //    ArrayList<PointsRecord> pointsRecords = new ArrayList<>();
    private PointsRecordNet pointsRecordNet = new PointsRecordNet();
    private LayoutInflater mInflater;
    private Context context;
    private ViewHolder holder = null;
    private static final int TYPE_TOP = 0;
    private static final int TYPE_ITEM = 1;
    private int currentType;//当前item类型
 /*   public PointsRecordAdapter(Context context, ArrayList<PointsRecord> beans) {
        this.context = context;
//        this.pointsRecords = beans;
        mInflater = LayoutInflater.from(context);
    }*/
    LinearLayout.LayoutParams layoutParams = null;
    public PointsRecordAdapter(Context context, PointsRecordNet bean) {
        this.context = context;
        this.pointsRecordNet = bean;
        mInflater = LayoutInflater.from(context);
        layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public int getCount() {
        int count = pointsRecordNet.getPoint_histories().size();
        return count != 0 ? count + 1 : count;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_TOP;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View topView;
        View itemView;
        currentType = getItemViewType(position);
        if (currentType == TYPE_TOP) {
            ViewHolderTop viewHolderTop;
            if (convertView == null) {
                viewHolderTop = new ViewHolderTop();
                topView = mInflater.inflate(
                        R.layout.item_points_record_top, parent, false);
                viewHolderTop.tv_points_count = (TextView) topView.findViewById(R.id.tv_points_count);
                topView.setTag(viewHolderTop);
                convertView = topView;
            } else {
                viewHolderTop = (ViewHolderTop) convertView.getTag();
            }
            viewHolderTop.tv_points_count.setText(Html.fromHtml("<font color='#999999'>可用积分：</font>" + "<font color='#ff8800'>" + pointsRecordNet.getUse_able() + "</font>"));
//            viewHolderTop.tv_points_count.setText("可用积分" + String.valueOf(pointsRecordNet.getUse_able()));// 额外奖励
        } else {
            PointsRecord pointsRecord = pointsRecordNet.getPoint_histories().get(position - 1);
            ViewHolder viewHolder;
            if (convertView == null) {
                viewHolder = new ViewHolder();
                itemView = mInflater.inflate(
                        R.layout.item_points_record, null);
                viewHolder.mChangPadding = (LinearLayout) itemView.findViewById(R.id.mChangPadding);
                viewHolder.tv_title = (TextView) itemView.findViewById(R.id.tv_title);
                viewHolder.tv_points = (TextView) itemView.findViewById(R.id.tv_points);
                viewHolder.tv_time = (TextView) itemView.findViewById(R.id.tv_time);
                viewHolder.v_line = itemView.findViewById(R.id.v_line);
                itemView.setTag(viewHolder);
                convertView = itemView;
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            if (position == getCount() - 1) {
                layoutParams.bottomMargin = (int) mContext.getResources().getDimension(R.dimen.margin_size_8dp);
                viewHolder.mChangPadding.setLayoutParams(layoutParams);
                viewHolder.v_line.setVisibility(View.INVISIBLE);
            } else {
                layoutParams.bottomMargin = 0;
                viewHolder.mChangPadding.setLayoutParams(layoutParams);
                viewHolder.v_line.setVisibility(View.VISIBLE);
            }
            viewHolder.tv_title.setText(pointsRecord.getContent());// 合计
            viewHolder.tv_time.setText(pointsRecord.getDate());
            viewHolder.tv_points.setText(pointsRecord.getPoint() > 0 ? "+" + pointsRecord.getPoint()
                    : String.valueOf(pointsRecord.getPoint()));
            viewHolder.tv_points.setTextColor(pointsRecord.getPoint() > 0
                    ? context.getResources().getColor(R.color.light_yellow)
                    : context.getResources().getColor(R.color.theme_green));
        }
        return convertView;
    }


    private final class ViewHolder {
        TextView tv_points;
        TextView tv_time;
        TextView tv_title;
        View v_line;
        LinearLayout mChangPadding;

    }

    private final class ViewHolderTop {
        TextView tv_points_count;

    }
}
