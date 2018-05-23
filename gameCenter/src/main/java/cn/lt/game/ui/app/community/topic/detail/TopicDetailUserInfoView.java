package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.view.RoundImageView;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;
import cn.lt.game.ui.app.community.topic.my.TopicTitleView;
import cn.lt.game.ui.app.community.widget.MoreButton;

public class TopicDetailUserInfoView extends FrameLayout implements View.OnClickListener{
	private static final int ADMIN = 2;

	private Context mContext;

	private RoundImageView mUserIconIV;

	private TextView mUserNameTV;

	private TextView mCommentDateTV;

	private TextView mCommentContentTV;

	private TopicDetail mTopicDetail;

	private TopicTitleView mCommentTitle;

	private MoreButton mMoreButton;

	private ImageView mUserAdmin;

	private ImageView levleView;

	private LayoutInflater mInflater;



	private int userId;

	public void setmTopicDetail(TopicDetail mTopicDetail) {
		this.mTopicDetail = mTopicDetail;
	}

	public TopicDetailUserInfoView(Context context) {
		super(context);
		this.mContext = context;
		init();
	}

	public TopicDetailUserInfoView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.mContext = context;
		init();
	}


	private void init() {
		mInflater = LayoutInflater.from(mContext);
		mInflater.inflate(R.layout.topic_detail_layout, this);
		initView();
	}

	private void initView() {
		mUserAdmin = (ImageView) findViewById(R.id.iv_is_manger);
		mMoreButton = (MoreButton) findViewById(R.id.mb_topic_detail);
		mMoreButton.setVisibility(View.GONE);
		mUserNameTV = (TextView) findViewById(R.id.tv_user_name_topic_detail);
		mUserIconIV = (RoundImageView) findViewById(R.id.iv_user_head_pht_topic_detail);
		mCommentDateTV = (TextView) findViewById(R.id.tv_comment_date);
		mCommentContentTV = (TextView) findViewById(R.id.tv_comment_content_topic_detail);

		mCommentTitle = (TopicTitleView) findViewById(R.id.tv_comment_title_topic_detail);

		levleView = (ImageView) findViewById(R.id.iv_level_data);

		mUserIconIV.setOnClickListener(this);
		mUserNameTV.setOnClickListener(this);
	}

	public void fillLayout(TopicDetail detail) {
		userId = detail.author_id;
		mCommentTitle.setTopicTitle(detail);
		setmTopicDetail(detail);
		mMoreButton.setData(detail, MoreButton.TopicType.Default_Topic);
		mUserNameTV.setText(detail.author_nickname);
		mCommentDateTV.setText(TimeUtils.curtimeDifference(detail.published_at));


		ImageloaderUtil.loadImage(getContext(),detail.author_icon, mUserIconIV, false);
		levleView.setImageLevel(detail.user_level);

		HtmlUtils.supportHtmlWithNet(mCommentContentTV,detail.topic_content,true);


		if (detail.user_type == ADMIN) {
			mUserAdmin.setVisibility(View.VISIBLE);
		} else {
			mUserAdmin.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		ActivityActionUtils.activity_Jump_Value(v.getContext(), PersonalActivity.class, "userId", userId);
	}



}
