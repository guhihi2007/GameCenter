package cn.lt.game.ui.app.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.ui.app.ImageViewPagerActivity;
import cn.lt.game.ui.app.ImageViewPagerActivity.ImageUrl;
import cn.lt.game.ui.app.community.model.DraftBean;
import cn.lt.game.ui.app.community.model.Photo;
import cn.lt.game.ui.app.community.widget.DraftMenu;

//草稿箱ListView的Item（回复或者评论）
public class DraftsItemViewReplyOrComment extends LinearLayout implements
		OnClickListener {
	private Context context;
	private LayoutInflater inflater; 
	private TextView type, time, content, topic_title, topic_content;  //类型，时间，内容，话题标题,话题内容
	private View topic; //话题
	private DraftMenu menu;//右上角菜单点击图标
	private final int COMMENT = 1;
	private final int REPLY = 2;

	public DraftsItemViewReplyOrComment(Context context, LayoutInflater inflater) {
		super(context);
		this.context = context;
		this.inflater = inflater;
		initView();
	}

	private void initView() {
		inflater.inflate(R.layout.drafts_replyorcomment_item, this);
		topic = findViewById(R.id.topic);
		type = (TextView) findViewById(R.id.type);
		time = (TextView) findViewById(R.id.time);
		topic_title = (TextView) findViewById(R.id.topic_title);
		content = (TextView) findViewById(R.id.content);
		topic_content = (TextView) findViewById(R.id.topic_content);
		menu = (DraftMenu) findViewById(R.id.menu);
	}

	public void setData(DraftBean db) { // 有可能是评论，有可能是回复根据type区分
		switch (db.getType()) {
		case 1:
			type.setText("评论");
			time.setText(db.getTag());
			if (db.getLocal_commentContent().equals("-1")) {
				content.setText(getContent(db.getComment_content()));
			} else {
				content.setText(getContent(LocalHelpTools.instance(context)
						.getStringContent(db.getLocal_commentContent())));
				db.setComment_content(content.getText().toString());
			}
			if (db.getLocal_commentPaths().equals("-1")) {

			} else {
				db.setComment_paths(LocalHelpTools.instance(context).getPaths(
						db.getLocal_commentPaths()));
			}
			break;
		case 2:
			type.setText("回复");
			time.setText(db.getTag());
			content.setText(db.getReply_content());
			if (db.getLocal_replyContent().equals("-1")) {
				content.setText(db.getReply_content());
			} else {
				content.setText(LocalHelpTools.instance(context)
						.getStringContent(db.getLocal_replyContent()));
				db.setReply_content(content.getText().toString());
			}

			break;
		default:
			break;
		}

		topic_title.setText(db.getTopic_title());
		if (db.getLocal_topicContent().equals("-1")) {
			topic_content.setText(getContent(db.getTopic_content()));
		} else {
			topic_content.setText(getContent(LocalHelpTools.instance(context)
					.getStringContent(db.getLocal_topicContent())));
		}
		content.setOnClickListener(this);
		content.setTag(db);
		topic.setOnClickListener(this);
		topic.setTag(db);
		menu.setDb(db);// 菜单点击按钮设置
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.content:
			DraftBean db = (DraftBean) v.getTag();
			switch (db.getType()) {
			case COMMENT:
				Intent in2 = new Intent(context, SendCommentActivity.class);
				Bundle b2 = new Bundle();
				b2.putString("type", "2");// 草稿箱编辑页面启动的 所以传2
				b2.putSerializable("draftBean", db);
				in2.putExtras(b2);
				context.startActivity(in2);
				break;
			case REPLY:
				Intent in3 = new Intent(context, SendReplyActivity.class);
				Bundle b3 = new Bundle();
				b3.putString("type", "2");// 草稿箱编辑页面启动的 所以传2
				b3.putSerializable("draftBean", db);
				in3.putExtras(b3);
				context.startActivity(in3);
				break;
			default:
				break;
			}
			break;
		case R.id.topic:
			DraftBean dd = (DraftBean) v.getTag();
			ActivityActionUtils.jumpToTopicDetail(context, dd.getTopic_Id());
			break;

		default:
			MyImageView im = (MyImageView) v;
			ActivityActionUtils.jumpToImagEye((Activity) context,
					changeToImageUrl(im.getPaths()), im.getPosition());
			break;
		}

	}

	private ImageViewPagerActivity.ImageUrl changeToImageUrl(//本地路径转换成ImageViewPagerActivity可识别的路径
			ArrayList<String> al) {
		List<Photo> li = new ArrayList<Photo>();
		for (int i = 0; i < al.size(); i++) {
			Photo p = new Photo();
			p.original = "File:/" + al.get(i);
			li.add(p);
		}
		ImageViewPagerActivity.ImageUrl url = new ImageUrl(li);
		return url;
	}

	private String getContent(String html){
		String content = HtmlUtils.convertHtmlToString(html);
		if(content.trim().isEmpty()){
			ArrayList<String> imageList = HtmlUtils.getImagePathList(html);
			if(imageList.size()>0){
				content = "[图片]";
			}else{
				content="[空]";
			}
		}
		return content;
	}


}
