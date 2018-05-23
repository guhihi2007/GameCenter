package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri;
import cn.lt.game.ui.app.community.model.Comment;
import cn.lt.game.ui.app.community.model.Comments;
import cn.lt.game.ui.app.community.model.Likes;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.model.User;
import cn.lt.game.ui.app.community.topic.detail.HeaderViewInstance.HeaderView;
import cn.lt.game.ui.app.community.widget.LikeTextViewOfTopicDerail;

public class GroupListActionbar extends FrameLayout implements OnClickListener {
    public static final String NAME = "TopicOrderBy";
    private static final int COMMENT_TYPE = 1;
    private static final int LIKELIST_TYPE = 2;
    private SelectButton comment;
    private SelectButton like;
    private GroupListIndicator indicator;
    private int page = 0;
    private Context context;
    private int topicId = -1;
    private ArrayList<ICommentList> commentList = new ArrayList<ICommentList>();
    private ArrayList<ICommentList> likeList = new ArrayList<ICommentList>();
    private ArrayList<ICommentList> emptyList = new ArrayList<ICommentList>();
    private ICallBack dataCallBack;
    private CommentLoadingItem commentLoadingItem;
    private CommentHintItem commentHintItem;
    private CommentHeaderItem headerItem;// 评论相关信息view(用户信息，评论内容)
    private CommentEmptyItem emptyActionItem;
    private GradientRightView gradienRighttView;
    private GradientLeftView gradientLeftView;
    private ActionbarOnClickListener actionbarOnClickListener;
    private HeaderView headerView = null;//用户组view+标题view
    private MeasureHeaderHeight measureHeaderHeight;
    private Orderby orderbyType;
    private boolean isDisabled = false;

    private LikeTextViewOfTopicDerail likeTextView;// 点赞按钮的实例（这里使用是防止频繁点击）

    public GroupListActionbar(Context context) {
        super(context, null);

    }

    public GroupListActionbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater.from(context)
                .inflate(R.layout.group_listactionbar, this);
        initView();
    }

    private void initView() {
        SharedPreferencesUtil SharedPreferences = new SharedPreferencesUtil(getContext());
        int type = SharedPreferences.getInteger(NAME);
        initOrderBy(type);

        comment = (SelectButton) findViewById(R.id.group_listactionbar_comment);
        like = (SelectButton) findViewById(R.id.group_listactionbar_like);
        indicator = (GroupListIndicator) findViewById(R.id.group_listactionbar_Indicator);
        gradienRighttView = (GradientRightView) findViewById(R.id.group_listactionbar_emptyRightLayout);
        gradientLeftView = (GradientLeftView) findViewById(R.id.group_listactionbar_emptyLeftLayout);

        comment.setOnClickListener(this);
        like.setOnClickListener(this);

        int actionBarHeight = (int) getResources().getDimension(
                R.dimen.group_list_actionbar_height);
        emptyActionItem = new CommentEmptyItem(getEmptyView(0, actionBarHeight));
        commentHintItem = new CommentHintItem(context);
        commentLoadingItem = new CommentLoadingItem(context);

        initButtonX();

    }

    private void initOrderBy(int type) {
        switch (type) {
            case 0:// 默认
                orderbyType = Orderby.ASC;
                break;
            case 1:
                orderbyType = Orderby.ASC;
                break;
            case 2:
                orderbyType = Orderby.DESC;
                break;

            default:
                break;
        }

    }


    // 获取comment 按钮 leftX轴坐标，和RigthX轴坐标。然后初始化指示器；

    private void initButtonX() {
        comment.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        // 根据提供的坐标画角
                        indicator.setButtonOffset(comment.getLeft(),
                                comment.getRight());

                        comment.getViewTreeObserver()
                                .removeOnGlobalLayoutListener(this);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.group_listactionbar_comment:
                if (actionbarOnClickListener != null) {
                    commentHintItem.setHint("还没有人评论");
                    actionbarOnClickListener.commentOnClick(commentLoadingItem,
                            commentHintItem);
                }
                like.setEnabled(true);
                comment.setEnabled(false); // 不能按
                commentList.clear();
                page = 0;
                checkNetWork(COMMENT_TYPE, true, orderbyType);
                indicator.setButtonOffset(v.getLeft(), v.getRight());
                break;
            case R.id.group_listactionbar_like:
                if (actionbarOnClickListener != null) {
                    commentHintItem.setHint("还没有人点赞");
                    actionbarOnClickListener.likeOnClick(commentLoadingItem,
                            commentHintItem);
                }
                like.setEnabled(false);
                comment.setEnabled(true);
                likeList.clear();
                page = 0;
                checkNetWork(LIKELIST_TYPE, true, null);
                indicator.setButtonOffset(v.getLeft(), v.getRight());
                break;

            default:
                break;
        }

    }

    private void checkNetWork(int type, Boolean isScrollTop, Orderby orderby) {
        if (NetUtils.isConnected(context)) {
            page++;
            switch (type) {
                case COMMENT_TYPE:
                    loadCommentData(page, orderby, isScrollTop);
                    break;
                case LIKELIST_TYPE:
                    loadLikeListData(page, isScrollTop);
                default:
                    break;
            }

        } else {
            if (dataCallBack != null) {
                dataCallBack.dataCallBackOnFail();
            }
        }
    }

    // 获取评论数据
    private void loadCommentData(final int page, Orderby orderby,
                                 final Boolean isScrollTop) {

        Map<String, String> params = new HashMap<String, String>();
        params.put("page", page + "");
        params.put("order_by", orderby.getEnglish());
//
        Net.instance().executeGet(HostType.FORUM_HOST,
                Uri.getTopicCommentsUri(topicId), params,
                new WebCallBackToObj<Comments>() {

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        Logger.w(error.getMessage());
                        if (dataCallBack != null) {
                            dataCallBack.dataCallBackOnFail();
                        }
                    }

                    @Override
                    protected void handle(Comments info) {

                        isDisabled = page == info.getTotal_page() || info.getTotal_page() == 0;

                        if (page == 1) {
                            commentList.clear();
                            commentList.addAll(emptyList);
                        }
                        if (isScrollTop && page != 1) {
                            commentList.addAll(emptyList);
                        }

                        for (Comment comment : info.getDetail()) {

                            if (!TextUtils.isEmpty(comment.type) && comment.type.equalsIgnoreCase("advertisement")) {

                                commentList.add(new AdvItem(comment));
                            } else {

                                CommentItem item = new CommentItem(comment);
                                commentList.add(item);
                            }

                        }

                        if (dataCallBack != null) {
                            dataCallBack.dataCallBack(commentList, isScrollTop,
                                    emptyList,isDisabled);

                        }
                    }

                });



    }

    // 获取点赞列表数据
    private void loadLikeListData(final int page, final Boolean isScrollTop) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("page", page + "");
        Net.instance().executeGet(HostType.FORUM_HOST,
                new Uri().getTopicLikesUri(topicId), params,
                new WebCallBackToObj<Likes>() {

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        // TODO Auto-generated method stub
                        Logger.w(error.getMessage());

                        if (dataCallBack != null) {
                            dataCallBack.dataCallBackOnFail();
                        }

                        // 让话题详情页上的点赞按钮可以点击
                        likeTextView.setIsSendingRequest(false);
                    }

                    @Override
                    protected void handle(Likes info) {
                        // TODO Auto-generated method stub

                        isDisabled = page == info.total_page || info.total_page == 0;

                        if (isScrollTop) {
                            likeList.addAll(emptyList);
                        }

                        for (User user : info.detail) {

                            LikeListItem item = new LikeListItem(user);
                            if (!likeList.contains(item)) {
                                likeList.add(item);
                            }

                        }

                        if (dataCallBack != null) {
                            dataCallBack.dataCallBack(likeList, isScrollTop,
                                    emptyList,isDisabled);
                        }

                        // 让话题详情页上的点赞按钮可以点击
                        likeTextView.setIsSendingRequest(false);

                    }
                });

    }

    public void getData() {
        int type = 0;
        if (!like.isEnabled()) {
            type = LIKELIST_TYPE;

        } else {
            type = COMMENT_TYPE;
        }
        checkNetWork(type, false, orderbyType);
    }

    public void refreshData() {
        page = 0;
        commentList.clear();
        likeList.clear();

        commentList.addAll(emptyList);
        likeList.addAll(emptyList);
        getData();
    }

    public void getLikeData() {
        likeList.clear();
        likeList.addAll(emptyList);
        page = 0;

        if (actionbarOnClickListener != null) {
            actionbarOnClickListener.likeOnClick(commentLoadingItem,
                    commentHintItem);
        }
        like.setEnabled(false);
        comment.setEnabled(true);
        indicator.setButtonOffset(like.getLeft(), like.getRight());
        getData();
    }

    public void addListViewHead(TopicDetail info, int groupId, String groupTitle) {
        commentList.clear();
        likeList.clear();
        emptyList.clear();
        page = 0;

        headerView = HeaderViewInstance.getInstance(context);
        headerView.setDetail(info);
        headerView.getViewTreeObserver().addOnGlobalLayoutListener(
                new OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {
                        if (measureHeaderHeight != null && headerView != null) {
                            measureHeaderHeight.headerHeight(
                                    headerView.getRight(),
                                    headerView.getBottom());

                            headerView.getViewTreeObserver()
                                    .removeOnGlobalLayoutListener(this);
                        }

                    }
                });

        headerItem = new CommentHeaderItem(headerView, groupId, groupTitle);
        emptyList.add(headerItem);
        emptyList.add(emptyActionItem);
        emptyList.add(commentHintItem);
        emptyList.add(commentLoadingItem);

        commentList.addAll(emptyList);
        likeList.addAll(emptyList);

        getData();

    }

    private View getEmptyView(int w, int h) {
        View headerView = new View(context);
        headerView.setMinimumWidth(w);
        headerView.setMinimumHeight(h);
        headerView.setClickable(false);
        headerView.setFocusable(false);
        headerView.setFocusableInTouchMode(false);
        headerView.setBackgroundColor(getResources().getColor(
                R.color.background_grey));

        return headerView;
    }

    public int getTopicId() {
        return topicId;
    }

    public void setTopicId(int topicId) {
        this.topicId = topicId;
    }

    public void setCommentNum(int num) {
        // comment.set
        comment.setText("评论 " + num);
    }

    public void setLikeNum(int num) {
        like.setText("赞 " + num);
    }

    public void initActionBar() {
        like.setEnabled(true);
        comment.setEnabled(false); // 不能按
        indicator.setButtonOffset(comment.getLeft(), comment.getRight());
    }

    public ICallBack getDataCallBack() {
        return dataCallBack;
    }

    public void setDataCallBack(ICallBack dataCallBack) {
        this.dataCallBack = dataCallBack;
    }

    public void setGradientViewAlpha(float alpha) {
        gradienRighttView.setAlpha(alpha);
        gradientLeftView.setAlpha(alpha);

    }

    public ActionbarOnClickListener getActionbarOnClickListener() {
        return actionbarOnClickListener;
    }

    public void setActionbarOnClickListener(
            ActionbarOnClickListener actionbarOnClickListener) {
        this.actionbarOnClickListener = actionbarOnClickListener;
    }

    // 测量头部高度回调
    public MeasureHeaderHeight getMeasureHeaderHeight() {
        return measureHeaderHeight;
    }

    public void setMeasureHeaderHeight(MeasureHeaderHeight measureHeaderHeight) {
        this.measureHeaderHeight = measureHeaderHeight;
    }

    // 数据回调接口
    public interface ICallBack {
         void dataCallBack(ArrayList<ICommentList> list,
                                 Boolean isScroll, ArrayList<ICommentList> emptyList,Boolean isDisabled);

         void dataCallBackOnFail();

    }

    public interface ActionbarOnClickListener {
        void commentOnClick(CommentLoadingItem commentLoadingItem,
                            CommentHintItem commentHintItem);

        void sortOnClick(CommentLoadingItem commentLoadingItem,
                         CommentHintItem commentHintItem);

        void likeOnClick(CommentLoadingItem commentLoadingItem,
                         CommentHintItem commentHintItem);
    }

    public interface MeasureHeaderHeight {

        void headerHeight(int w, int h);
    }

    public void HeaderDestroy() {
        if (headerView != null) {
            HeaderViewInstance.onDestroy();
            headerView = null;
        }
    }

    public void setSort(Orderby orderbyType) {
        this.orderbyType = orderbyType;

        indicator.setButtonOffset(comment.getLeft(), comment.getRight());

        like.setEnabled(true);
        comment.setEnabled(true);

        if (actionbarOnClickListener != null) {
            commentHintItem.setHint("还没有人评论");
            actionbarOnClickListener.sortOnClick(commentLoadingItem,
                    commentHintItem);
        }

        commentList.clear();
        page = 0;
        checkNetWork(COMMENT_TYPE, true, orderbyType);
    }

    public void setLikeTextViewInstance(LikeTextViewOfTopicDerail likeTextView) {
        this.likeTextView = likeTextView;
    }

}
