package cn.lt.game.ui.app.community.topic;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.ImageViewPagerActivity.ImageUrl;
import cn.lt.game.ui.app.community.model.TextType;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.my.TopicTitleView;
import cn.lt.game.ui.app.community.widget.CommentTextView;
import cn.lt.game.ui.app.community.widget.LikeTextView;
import cn.lt.game.ui.app.community.widget.ReadingNumberTextView;

/**
 * 话题项，包括：用户信息、话题内容、阅读数、评论数、点赞数 按键
 */
public class TopicItemWidget extends RelativeLayout {

    private UserInfoWidget userInfo;
    private TopicTitleView title;
    private TextView content;
    private ImageView img[] = new ImageView[3];
    private TextView tv_imgCount[] = new TextView[3];
    private RelativeLayout rl_img[] = new RelativeLayout[3];
    private ReadingNumberTextView readingNumber;
    private LinearLayout comment;
    private LinearLayout like;
    private CommentTextView tv_comment;
    private LikeTextView tv_like;

    private TextView tv_allContent;
    private LinearLayout ll_topImgPreview;

    private LinearLayout ll_topicButtomBtn;
    private View line;
    private LinearLayout ll_topicContent;

    public TopicItemWidget(Context context) {
        super(context);
        initView(context);
        init_findView();
    }

    public TopicItemWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
        init_findView();
    }

    public TopicItemWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
        init_findView();
    }

    private void initView(Context context) {
        LayoutInflater.from(context).inflate(R.layout.topic_item, this);
        setBackgroundResource(R.color.white);
        setPadding(DensityUtil.dip2px(context, 12), 0, DensityUtil.dip2px(context, 12), 0);
    }

    private void init_findView() {
        userInfo = (UserInfoWidget) findViewById(R.id.user_info);
        title = (TopicTitleView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.tv_topic_content);
        tv_allContent = (TextView) findViewById(R.id.tv_allContent);
        ll_topImgPreview = (LinearLayout) findViewById(R.id.ll_topImgPreview);
        img[0] = (ImageView) findViewById(R.id.img1);
        img[1] = (ImageView) findViewById(R.id.img2);
        img[2] = (ImageView) findViewById(R.id.img3);
        rl_img[0] = (RelativeLayout) findViewById(R.id.rl_img1);
        rl_img[1] = (RelativeLayout) findViewById(R.id.rl_img2);
        rl_img[2] = (RelativeLayout) findViewById(R.id.rl_img3);
        tv_imgCount[0] = (TextView) findViewById(R.id.tv_imgCount1);
        tv_imgCount[1] = (TextView) findViewById(R.id.tv_imgCount2);
        tv_imgCount[2] = (TextView) findViewById(R.id.tv_imgCount3);
        readingNumber = (ReadingNumberTextView) findViewById(R.id.reading_number);
        comment = (LinearLayout) findViewById(R.id.comment);
        tv_comment = (CommentTextView) findViewById(R.id.tv_comment);
        like = (LinearLayout) findViewById(R.id.like);
        tv_like = (LikeTextView) findViewById(R.id.tv_like);
        ll_topicButtomBtn = (LinearLayout) findViewById(R.id.ll_topicButtomBtn);
        ll_topicContent = (LinearLayout) findViewById(R.id.ll_topicContent);
        line = findViewById(R.id.line);

    }

    public void setData(final TopicDetail topic) {
        userInfo.setUserInfo(topic.author_id);

        title.setTopicTitle(topic);
        userInfo.loadUserIcon(topic.author_icon);
        userInfo.setUserType(topic.user_type);
        userInfo.setUser_name(topic.author_nickname);
        userInfo.setTime(topic.published_at);
        userInfo.setUserLevel(topic.user_level);
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.jumpToTopicDetail(v.getContext(), topic.topic_id);

            }
        });

        setPreviewImage(topic);

        displayTopicContent(topic.topic_summary);

        readingNumber.setData(topic);
        tv_comment.setTextType(TextType.NUM);
        tv_comment.setData(topic, CommentTextView.FROM_HOTTOPICLIST);
        tv_comment.setAutoJumpToTopicDetail(true);
        comment.setOnClickListener(tv_comment.getOnClickListener());
        tv_like.setTextType(TextType.NUM);
        tv_like.setData(topic);
        like.setOnClickListener(tv_like.getOnClickListener());
    }

    private void setPreviewImage(final TopicDetail topic) {
        if (imgCount(topic) <= 0) {
            ll_topImgPreview.setVisibility(View.GONE);
        } else {
            displayPreviewImage(topic);
        }
    }

    private void displayPreviewImage(final TopicDetail topic) {
        ll_topImgPreview.setVisibility(View.VISIBLE);
        int imgSize = 0;
        while (imgSize < 3) {
            rl_img[imgSize++].setVisibility(View.GONE);
        }

        if (topic.appendix != null) {
            imgSize = 0;
            while (imgSize < imgCount(topic) && imgSize < 3) {
//                ImageLoader.getInstance().display(topic.appendix.photos.get(imgSize).thumbnail, img[imgSize]);
                ImageloaderUtil.loadImage(getContext(),topic.appendix.photos.get(imgSize).thumbnail, img[imgSize], false);
                final int position = imgSize;
                rl_img[imgSize].setVisibility(View.VISIBLE);
                img[imgSize].setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.jumpToImagEye((Activity) v.getContext(), new ImageUrl(topic.appendix.photos), position);
                    }
                });
                imgSize++;
            }
        }

        showImgCount(topic);
    }

    private int imgCount(TopicDetail topicDetail) {
        return topicDetail.appendix.photos.size();
    }

    private void showImgCount(TopicDetail topic) {
        for (int i = 0; i < tv_imgCount.length; i++) {
            tv_imgCount[i].setVisibility(View.GONE);
        }
        int imgCount = topic.appendix.photos.size();

        /*4.0版本后这里修改为只有话题图片大于三张才予以显示总图片张数  modify by tiantian  at 2015/11/16 */
        if (imgCount > 3) {
            setImgCount(2, imgCount);
        }
    }
     private void setImgCount(int whichTv, int imgCount) {
        tv_imgCount[whichTv].setVisibility(View.VISIBLE);
        tv_imgCount[whichTv].setText("共" + imgCount + "张");
    }
    private void displayTopicContent(String topic_summary) {
        if (topic_summary.length()>0) {
            content.setVisibility(View.VISIBLE);
            content.setText(topic_summary);
        }else{
            content.setVisibility(View.GONE);
        }
    }

    /***
     * 设置我的收藏布局
     */
    public void setTheViewIsMyCollect() {
        ll_topicButtomBtn.setVisibility(View.GONE);
        line.setVisibility(View.GONE);
        ll_topImgPreview.setVisibility(View.GONE);
        ll_topicContent.setVisibility(View.GONE);
        tv_allContent.setVisibility(View.GONE);
        title.setMaxLines(2);
        title.setPadding(0, 10, 0, 10);
    }


}
