package cn.lt.game.ui.app.category;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.ItemView;
import cn.lt.game.ui.app.requisite.widget.AutoGridView;

/**
 * Created by wenchao on 2015/12/15.
 */
public class HotCatsView extends ItemView {

    private AutoGridView mGridView;

    public HotCatsView(Context context, BaseOnclickListener clickListener) {
        super(context);
        super.mClickListener = clickListener;
        if (mClickListener != null) {
            this.mPageName = mClickListener.getmPageName();
        }
        inflate(context, R.layout.item_hot_cats_v4, this);
        initialize();
    }

    void initialize() {
        mGridView = (AutoGridView) findViewById(R.id.hot_cats_gridView);
    }


    @Override
    public void fillLayout(ItemData<? extends BaseUIModule> data, int position, int listSize) {
        GridAdapter mAdapter = new GridAdapter(getContext(), data, this);
        mGridView.setAdapter(mAdapter);
    }

    private static class GridAdapter extends BaseAdapter {

        private ItemData itemData;

        private HotCatsView itemView;

        private Context mContext;

        public GridAdapter(Context context, ItemData<? extends BaseUIModule> data, HotCatsView itemView) {
            this.mContext = context;
            this.itemData = data;
            this.itemView = itemView;
        }

        public void setList(ItemData<? extends BaseUIModule> data) {
            itemData = data;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return ((UIModuleGroup) itemData.getmData()).getData().size();
        }

        @Override
        public Object getItem(int position) {
            return ((UIModuleGroup) itemData.getmData()).getData().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UIModuleGroup hotcatsData = (UIModuleGroup) itemData.getmData();
            List<ItemData> hotcatsList = hotcatsData.getData();
            FunctionEssence functionEssence = (FunctionEssence) ((UIModule) hotcatsList.get(position).getmData()).getData();
            FunctionEssenceHolder holder;
            if (convertView == null) {
                holder = new FunctionEssenceHolder();
                convertView = LayoutInflater.from(mContext).inflate(R.layout.item_hot_cats_subitem, parent, false);
                holder.categoryIv = (ImageView) convertView.findViewById(R.id.hot_category_iv);
                holder.categoryTv = (TextView) convertView.findViewById(R.id.hot_category_tv);
                convertView.setTag(holder);
            } else {
                holder = (FunctionEssenceHolder) convertView.getTag();
            }
            ImageloaderUtil.loadImage(mContext, functionEssence.getImage(), holder.categoryIv, false);
            holder.categoryTv.setText(functionEssence.getTitle());

            StatisticsEventData sData = StatisticsDataProductorImpl.produceStatisticsData(hotcatsList.get(position).getmPresentData(), functionEssence.getUniqueIdentifier(), itemView.mPageName, ReportEvent.ACTION_CLICK, null, functionEssence.getUniqueIdentifierBy(IdentifierType.URL), functionEssence.getDomainEssence().getDomainType().toString());
            itemView.setViewTagForClick(convertView, functionEssence, functionEssence.getDomainEssence().getDomainType(), hotcatsList.get(position).getmPresentType(), sData);
            return convertView;
        }

    }

    private static class FunctionEssenceHolder {
        TextView categoryTv;
        ImageView categoryIv;
    }
}
