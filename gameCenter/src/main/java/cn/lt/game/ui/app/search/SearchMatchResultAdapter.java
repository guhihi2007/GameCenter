package cn.lt.game.ui.app.search;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.db.service.SearchService;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.gamedetail.OperationSign;
import cn.lt.game.ui.common.listener.InstallButtonClickListener;
import cn.lt.game.ui.installbutton.IndexUpdateButtonState;


/***
 * 搜索匹配结果适配器
 */
public class SearchMatchResultAdapter extends BaseAdapter {
    public static final int TYPE01 = 0; // 精确匹配类型
    public static final int TYPE02 = 1; // 搜索历史类型
    public static final int TYPE03 = 2;// 模糊搜索类型
    public Context context;
    private List<Object> mList = new ArrayList<Object>();
    /***
     * 填充精确搜索数据
     *
     * @param accurateHolder
     * @param position
     */
    private GameBaseDetail gamedetail = new GameBaseDetail();
    private String mPageName;

    public SearchMatchResultAdapter(Context context, String pageName) {
        this.context = context;
        this.mPageName = pageName;
    }

    @Override
    public int getItemViewType(int position) {
        Object o = mList.get(position);
        if (o instanceof String) {
            return TYPE02;
        } else if (o instanceof GameDomainBaseDetail) {
            return TYPE01;
        } else return TYPE03;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    public synchronized void setmList(List<? extends Object> list) {
        if (list == null) {
            mList = new ArrayList<Object>();
        } else {
            this.mList.addAll(list);
        }
        this.notifyDataSetChanged();
    }

    public synchronized void addData(List<? extends Object> list) {
        this.mList.clear();
        this.mList.addAll(list);
        notifyDataSetChanged();
    }

    public synchronized void clearList() {
        this.mList.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    public List<Object> getmList() {
        return mList;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SearchResultHolder searchResultHolder = null;
        SearchResultHolder searchHistoryHolder = null;
        SearchAccurateHolder accurateHolder = null;
        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE01:
                    accurateHolder = new SearchAccurateHolder();
                    convertView = View.inflate(context, R.layout.index_item_game_v4, null);
                    accurateHolder.icon = (ImageView) convertView.findViewById(R.id.logoIv);
                    accurateHolder.name = (TextView) convertView.findViewById(R.id.nameTv);
                    accurateHolder.label = (TextView) convertView.findViewById(R.id.label);
                    accurateHolder.size = (TextView) convertView.findViewById(R.id.game_size);
                    accurateHolder.downCnt = (TextView) convertView.findViewById(R.id.down_count);
                    accurateHolder.btn_stall = (Button) convertView.findViewById(R.id.grid_item_button);
                    accurateHolder.progress = (ProgressBar) convertView.findViewById(R.id.download_progress_bar);
                    accurateHolder.review = (TextView) convertView.findViewById(R.id.tagTv);
                    convertView.setTag(accurateHolder);
                    break;
                case TYPE02:
                    searchHistoryHolder = new SearchResultHolder();
                    convertView = View.inflate(context, R.layout.lt_searchhistory, null);
                    searchHistoryHolder.iv_sign = (ImageView) convertView.findViewById(R.id.iv_sign);
                    searchHistoryHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_serachcontent);
                    searchHistoryHolder.ivDetele = (ImageView) convertView.findViewById(R.id.delete_search_history);
                    convertView.setTag(searchHistoryHolder);
                    break;
                case TYPE03:
                    searchResultHolder = new SearchResultHolder();
                    convertView = View.inflate(context, R.layout.lt_searchhistory, null);
                    searchResultHolder.iv_sign = (ImageView) convertView.findViewById(R.id.iv_sign);
                    searchResultHolder.tvNumber = (TextView) convertView.findViewById(R.id.tv_serachcontent);
                    convertView.setTag(searchResultHolder);
                    break;
                default:
                    break;
            }
        } else {
            switch (type) {
                case TYPE01:
                    accurateHolder = (SearchAccurateHolder) convertView.getTag();
                    break;
                case TYPE02:
                    searchHistoryHolder = (SearchResultHolder) convertView.getTag();
                    break;
                case TYPE03:
                    searchResultHolder = (SearchResultHolder) convertView.getTag();
                    break;
                default:
                    break;
            }
        }
        try {
            switch (type) {
                case TYPE01:
                    initAccurateView(convertView, accurateHolder, position);
                    break;
                case TYPE02:
                    fillSearchResultItem(true, convertView, searchHistoryHolder, position);
                    break;
                case TYPE03:
                    fillSearchResultItem(false, convertView, searchResultHolder, position);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public void updateView(View view, int position) {
        if (view == null) return;
        Object tag = view.getTag();
        if (tag == null) return;
        if (tag instanceof SearchAccurateHolder) {
            initAccurateView(view, (SearchAccurateHolder) tag, position);
        }
    }
    /***
     * 填充历史搜索记录/填充模糊搜索数据
     *
     * @param resultHolder
     * @param pos
     */
    public void fillSearchResultItem(boolean isHistory, View convertView, SearchResultHolder resultHolder, final int pos) {
        if (isHistory) {
            final String historyWord = (String) mList.get(pos);
            resultHolder.iv_sign.setBackgroundResource(R.mipmap.ic_search_history);
            resultHolder.tvNumber.setText(historyWord);
            resultHolder.ivDetele.setVisibility(View.VISIBLE);
            resultHolder.ivDetele.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SearchService.getInstance(context).deleteOne(historyWord);
                    mList.remove(pos);
                    notifyDataSetChanged();
                }
            });
            convertView.setTag(R.id.search_item_click_type, "history");
            convertView.setTag(R.id.search_item_click_data, historyWord);
        } else {
            final FunctionEssence historyWord = (FunctionEssence) mList.get(pos);
            resultHolder.iv_sign.setBackgroundResource(R.mipmap.icon_search);
            resultHolder.tvNumber.setText(historyWord.getTitle());
            convertView.setTag(R.id.search_item_click_type, "fuzzy");
            convertView.setTag(R.id.search_item_click_data, historyWord);
        }

    }

    public void initAccurateView(View convertView, SearchAccurateHolder accurateHolder, int position) {
        GameDomainBaseDetail game = (GameDomainBaseDetail) mList.get(position);
        accurateHolder.name.setText(game.getName());

        List<String> flags = game.getFlags();
        if (flags != null && flags.size() > 0) {
            OperationSign sign;
            try {
                sign = OperationSign.valueOf(flags.get(0));
                accurateHolder.label.setVisibility(View.VISIBLE);
                accurateHolder.label.setText(sign.getSign());
                accurateHolder.label.setBackgroundResource(sign.getBackgroundRes());
                accurateHolder.label.setTextColor(context.getResources().getColor(sign.getColorRes()));
            } catch (IllegalArgumentException e) {
                accurateHolder.label.setVisibility(View.GONE);
            }

        } else {
            accurateHolder.label.setVisibility(View.GONE);
        }

        accurateHolder.review.setText(game.getReviews());
        accurateHolder.size.setText(Utils.getGameMBSize(game.getPkgSize()));
        accurateHolder.downCnt.setText(Utils.getGameDwncnt(game.getDownCnt() + ""));
        ImageloaderUtil.loadLTLogo(context, game.getIconUrl(), accurateHolder.icon);
        LogUtils.i("SearchMatchResultAdapter","id==========" + game.getPkgName());
//        gamedetail = DownFileService.getInstance(context).getDownFileById(Integer.parseInt(game.getUniqueIdentifier()));
        gamedetail = FileDownloaders.getDownFileInfoById(Integer.parseInt(game.getUniqueIdentifier()));
        if (gamedetail == null) {
            LogUtils.i("SearchMatchResultAdapter", "=====================");
            gamedetail = new GameBaseDetail();
            gamedetail.setGameBaseInfo(game);
        }
        IndexUpdateButtonState installButtonGroup = new IndexUpdateButtonState(gamedetail, accurateHolder.btn_stall, accurateHolder.progress);
        InstallButtonClickListener installButtonClickListener = new InstallButtonClickListener(context, gamedetail, installButtonGroup, mPageName);
        accurateHolder.btn_stall.setOnClickListener(installButtonClickListener);

        StatisticsEventData mStatisticsData = new StatisticsEventData();
        mStatisticsData.setActionType(ReportEvent.ACTION_CLICK);
        mStatisticsData.setPos(position + 1);
        mStatisticsData.setSrc_id(Integer.valueOf(game.getUniqueIdentifier()) + "");
        mStatisticsData.setSubPos(0);
        mStatisticsData.setPackage_name(game.getPkgName());
        accurateHolder.btn_stall.setTag(R.id.statistics_data, mStatisticsData);

        int state = gamedetail.getState();
        LogUtils.i("SearchMatchResultAdapter", "state======" + state);
        int percent = gamedetail.getDownPercent();
        installButtonGroup.setViewBy(state, percent);
        convertView.setTag(R.id.search_item_click_type, "accurate");
        convertView.setTag(R.id.search_item_click_data, game);
    }
    /***
     * 模糊搜索对象
     *
     * @author tiantian
     * @des
     */
    static class SearchResultHolder {
        ImageView iv_sign;
        TextView tvNumber;
        ImageView ivDetele;
    }

    /***
     * 精确搜索对象
     *
     * @author tiantian
     * @des
     */
    static class SearchAccurateHolder {
        public TextView name;
        public TextView label;
        public TextView size;
        public TextView downCnt;
        public ImageView icon;
        public Button btn_stall;
        public ProgressBar progress;
        public TextView review;
    }
}
