package cn.lt.game.ui.app.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.adapter.factory.ItemViewFactory;
import cn.lt.game.ui.app.adapter.listener.BaseOnclickListener;
import cn.lt.game.ui.app.adapter.weight.IndexItemEntryView;
import cn.lt.game.ui.app.adapter.weight.ItemBannerView;
import cn.lt.game.ui.app.adapter.weight.ItemGameActivityView;
import cn.lt.game.ui.app.adapter.weight.ItemGarbGiftView;
import cn.lt.game.ui.app.adapter.weight.ItemGiftLastestView;
import cn.lt.game.ui.app.adapter.weight.ItemSingleGameView;
import cn.lt.game.ui.app.adapter.weight.ItemView;

public class LTBaseAdapter extends BaseAdapter implements IDownloadingStatusExtractor {

    private List<ItemData<? extends BaseUIModule>> mList;
    /**
     * 保存游戏的下载地址；
     */
    private List<String> mUrls;
    private BaseOnclickListener mClickListener;
    private Context mContext;

    public LTBaseAdapter(Context context, BaseOnclickListener clickListener) {
        this.mClickListener = clickListener;
        this.mContext = context;
    }

    public List<String> getmUrls() {
        return mUrls;
    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).getmPresentType().viewType;
    }

    /**
     * 返回所有的类型的数量
     */
    @Override
    public int getViewTypeCount() {
        return PresentType.values().length;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemView view = null;
        try {
            ItemData<? extends BaseUIModule> data = mList.get(position);
            if (convertView == null) {
                //创建view;
                view = ItemViewFactory.produceItemView(data.getmPresentType(), mClickListener, mContext);
                convertView = view;
                convertView.setTag(view);
            } else {
                view = (ItemView) convertView.getTag();
            }
            view.fillLayout(data, position, mList.size());
            setItemDecoration(data.getmPresentType(), view, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return convertView;
    }

    /**
     * 控制Item条纹的显示与隐藏
     *
     * @param presentType1
     * @param view
     * @param pos
     */
    private void setItemDecoration(PresentType presentType1, ItemView view, int pos) {
        PresentType presentType = presentType1;
        LogUtils.i("yyy", "setItemDecoration");
        if (presentType == PresentType.entry) {
            LogUtils.i("yyy", "PresentType.entry" + pos);
            IndexItemEntryView i = (IndexItemEntryView) view.getTag();
            if (pos != 0) {
                if (mList.get(pos - 1).getmPresentType() == PresentType.game) {
                    i.mChangePadding.setPadding(0, DensityUtil.dip2px(mContext, 8), 0, DensityUtil.dip2px(mContext, 8));
                    return;
                }
            }
            i.mChangePadding.setPadding(0, 0, 0, DensityUtil.dip2px(mContext, 8));
        } else if (presentType == PresentType.banner) {
            ItemBannerView i = (ItemBannerView) view.getTag();
            if (pos != 0) {
                if (mList.get(pos - 1).getmPresentType() == PresentType.game) {
                    i.mChangPadding.setPadding(DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8));
                    return;
                }
            }
            i.mChangPadding.setPadding(DensityUtil.dip2px(mContext, 8), 0, DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8));
        } else if (presentType == PresentType.super_push) {

            ItemSingleGameView i = (ItemSingleGameView) view.getTag();
            i.mBottomLine.setVisibility(View.GONE);
            if (pos != 0) {
                if (mList.get(pos - 1).getmPresentType() == PresentType.game) {
                    i.mChangPadding.setPadding(DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8));
                    return;
                }
            }
            i.mChangPadding.setPadding(DensityUtil.dip2px(mContext, 8), 0, DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8));
        } else if (presentType == PresentType.game) {
            ItemSingleGameView i = (ItemSingleGameView) view.getTag();
            if (pos + 1 >= mList.size()) {
                i.mChangPadding.setPadding(DensityUtil.dip2px(mContext, 8), 0, DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8));
                i.mBottomLine.setVisibility(View.GONE);
            } else if (mList.get(pos + 1).getmPresentType() == PresentType.hot) {
                i.mChangPadding.setPadding(DensityUtil.dip2px(mContext, 8), 0, DensityUtil.dip2px(mContext, 8), DensityUtil.dip2px(mContext, 8));
            } else {
                i.mChangPadding.setPadding(DensityUtil.dip2px(mContext, 8), 0, DensityUtil.dip2px(mContext, 8), 0);

                if (mList.get(pos + 1).getmPresentType() != PresentType.game) {
                    i.mBottomLine.setVisibility(View.GONE);
                } else {
                    i.mBottomLine.setVisibility(View.VISIBLE);
                }
            }
        } else if (presentType == PresentType.hot_gifts) {
            ItemGarbGiftView i = (ItemGarbGiftView) view.getTag();
            if (pos + 1 == mList.size()) {
                ((LinearLayout.LayoutParams) (i.mChangPadding.getLayoutParams())).bottomMargin = (int) mContext.getResources().getDimension(R.dimen.margin_size_8dp);
            } else {
                ((LinearLayout.LayoutParams) (i.mChangPadding.getLayoutParams())).bottomMargin = 0;
            }
        } else if (presentType == PresentType.new_gifts) {
            ItemGiftLastestView i = (ItemGiftLastestView) view.getTag();
            if (pos + 1 == mList.size()) {
                ((LinearLayout.LayoutParams) (i.mChangPadding.getLayoutParams())).bottomMargin = (int) mContext.getResources().getDimension(R.dimen.margin_size_8dp);
            } else {
                ((LinearLayout.LayoutParams) (i.mChangPadding.getLayoutParams())).bottomMargin = 0;
            }
        } else if (presentType == PresentType.game_activity) {
            ItemGameActivityView i = (ItemGameActivityView) view.getTag();
            if (pos + 1 == mList.size()) {
                ((LinearLayout.LayoutParams) (i.mChangPadding.getLayoutParams())).bottomMargin = (int) mContext.getResources().getDimension(R.dimen.margin_size_8dp);
                i.mBottomLine.setVisibility(View.GONE);
            } else {
                ((LinearLayout.LayoutParams) (i.mChangPadding.getLayoutParams())).bottomMargin = 0;
                i.mBottomLine.setVisibility(View.VISIBLE);
            }
        }
    }


    /**
     * 设置数据集，直接改变数据集的类容，之前数据将清除掉；
     *
     * @param list
     */
    public void setList(List<ItemData<? extends BaseUIModule>> list) {
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            this.mList = list;
        }
        this.notifyDataSetChanged();
        mUrls = getAllGameDownlLink();
    }

    /**
     * 往数据集中添加数据，使用该方法时可以保证现有数据不变，将新增的数据以追加的方式添加到数据集中；
     *
     * @param list
     */
    public void addList(List<ItemData<? extends BaseUIModule>> list) {
        if (list == null) {
            this.mList = new ArrayList<>();
        } else {
            if (this.mList == null) {
                this.mList = new ArrayList<>();
            }
            this.mList.addAll(list);
        }
        this.notifyDataSetChanged();
        mUrls = getAllGameDownlLink();
    }

    /**
     * 清除数据集的数据；
     */
    public void resetList() {
        if (mList != null) {
            mList.clear();
            this.notifyDataSetChanged();
        }
        mUrls = null;
    }

    @SuppressWarnings("unchecked")
    public List<String> getAllGameDownlLink() {
        List<String> urls = null;
        try {
            if (mList != null) {
                urls = new ArrayList<>();
                for (ItemData item : mList) {
                    switch (item.getmPresentData().getmType()) {
                        case super_push:
                        case game:
                        case search_top10:
                        case game_manage:
                        case search_null:
//                            LogUtils.i("ttt", "search_null 走了");
                            String temp = getUrlsFromModule((UIModule<GameDomainBaseDetail>) item.getmData());
                            if (!TextUtils.isEmpty(temp)) {
                                urls.add(temp);
                            }
                            break;
                        case hot:
                            List<String> temps = getUrlsFromGroup((UIModuleGroup<ItemData<UIModule<GameDomainBaseDetail>>>) item.getmData());
                            if (temps != null && temps.size() > 0) {
                                urls.addAll(temps);
                            }
                            break;
                        case carousel:
                        case entry:
                        case banner:
                        case game_detail:
                        case hot_cats:
                        case all_cats:
                        case topic:
                        case topic_detail:
                        case hot_gifts:
                        case new_gifts:
                        case gifts_search_ofgame:
                        case gifts_search_lists:
                        case my_gifts:
                        case game_gifts_summary:
                        case game_gifts_lists:
                        case gifts_detail:
                        case get_gift_code:
                        case activity:
                        case hot_tags:
                        case hot_words:
                        case query_ads:
                        case query_data:
                        case comments:
                        case update:
                        case text_feedback:
                        case image_feedback:
                        case text:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return urls;
    }

    @Override
    public List<String> getUrlsFromGroup(UIModuleGroup<ItemData<UIModule<GameDomainBaseDetail>>> group) {
        List<String> temps = null;
        try {
            if (group != null) {
                temps = new ArrayList<>();
                List<ItemData<UIModule<GameDomainBaseDetail>>> items = group.getData();
                for (ItemData item : items) {
                    GameDomainBaseDetail detail = (GameDomainBaseDetail) ((UIModule) item.getmData()).getData();
                    temps.add(detail.getDownUrl());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return temps;
    }

    @Override
    public String getUrlsFromModule(UIModule<GameDomainBaseDetail> module) {
        String url = null;
        try {
            if (module != null) {
                GameDomainBaseDetail detail = module.getData();
                url = detail.getDownUrl();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }
}