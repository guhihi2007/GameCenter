package cn.lt.game.ui.app.community.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.db.service.DraftService;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.ui.app.community.DraftsJudgeTools;
import cn.lt.game.ui.app.community.EventTools;
import cn.lt.game.ui.app.community.SendCommentActivity;
import cn.lt.game.ui.app.community.SendReplyActivity;
import cn.lt.game.ui.app.community.SendTopicActivity;
import cn.lt.game.ui.app.community.SqlQueueTools;
import cn.lt.game.ui.app.community.TopicTitleFragment;
import cn.lt.game.ui.app.community.model.ClearEvent;
import cn.lt.game.ui.app.community.model.DraftBean;
import de.greenrobot.event.EventBus;

/***
 * 发表话题、评论、回复失败的弹出框
 * 
 * @author ltbl
 * 
 */
public class DraftsDialog extends Dialog implements OnClickListener {

	private TextView tv_retry, tv_edit, tv_delete; //重试，编辑，删除
	private Context context;
	private DraftBean db;//草稿箱Bean
	private final int TOPIC = 0;  
	private final int COMMENT = 1;
	private final int REPLY = 2;

	public DraftsDialog(Context context, DraftBean db) {
		super(context, R.style.updateInfoDialogStyle);
		this.context = context;
		this.db = db;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.failuredilog);
		initView();
	}

	private void initView() { //得到Dialog里面控件的引用
		tv_retry = (TextView) findViewById(R.id.tv_retry);
		tv_edit = (TextView) findViewById(R.id.tv_edit);
		tv_delete = (TextView) findViewById(R.id.tv_delete);
		tv_retry.setOnClickListener(this);
		tv_edit.setOnClickListener(this);
		tv_delete.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 删除/取消发送
		case R.id.tv_delete:
			this.dismiss();
			String tag = db.getTag();
			Boolean deleteOK = DraftService.getSingleton(context).deleteByTag(tag);
			if (!deleteOK) {
				ToastUtils.showToast(context, "删除失败");
				return;
			}
			ToastUtils.showToast(context, "删除成功");

			int count = DraftService.getSingleton(context).getDraftListCount();
			if (count <= 0) {
				Log.i("zzz", "草稿箱已空，取消所有草稿箱通知 。。。");
				EventBus.getDefault().post(new ClearEvent(true));
			}
			EventTools.instance().sendDftsDelete(db);
			break;
		// 编辑
		case R.id.tv_edit:
			edit();
			this.dismiss();
			break;
		// 重试
		case R.id.tv_retry:
			retry();
			this.dismiss();
			break;
		}
	}

	private void retry() {   //重试，点击重试的时候 直接在后台发送
		if(!NetUtils.isConnected(getContext())){
			ToastUtils.showToast(getContext(), R.string.download_no_network);
			return;
		}

		switch (db.getType()) {
		case TOPIC:
			if(db.getTopic_title().trim().length()<=0){
				ToastUtils.showToast(getContext(), R.string.title_notnull);
				return;
			}

			if (db.getTopic_title().length() > TopicTitleFragment.INPUT_LIMIT) {
				ToastUtils.showToast(getContext(), R.string.title_too_much);
				return;
			}
			if (db.getTopic_content().trim().isEmpty()) {
				ToastUtils.showToast(getContext(), "发表失败，话题内容不能为空");
				return;
			}
			if (null == db.getCategory_id()) {
				ToastUtils.showToast(getContext(), "话题分类数据有误");
				return;
			}
			if (db.getTopic_content().length() > 20000) {
				ToastUtils.showToast(getContext(),
						"发表失败，话题内容最最多20000字");
				return;
			}
			SqlQueueTools.instance().sendToQueue(SqlQueueTools.TOPIC, db,
					context);
			DraftsJudgeTools.instance().save(db.getTag());
			break;
		case COMMENT:
			if (db.getComment_content().trim().isEmpty()) {
				ToastUtils.showToast(getContext(), "评论内容不能为空");
				return;
			}
			if (db.getComment_content().length() > 20000) {
				ToastUtils.showToast(getContext(),
						"内容最大长度不可超过20000字哦");
				return;
			}
			SqlQueueTools.instance().sendToQueue(SqlQueueTools.COMMENT, db,
					context);
			DraftsJudgeTools.instance().save(db.getTag());
			break;
		case REPLY:
			SqlQueueTools.instance().sendToQueue(SqlQueueTools.REPLY, db,
					context);
			DraftsJudgeTools.instance().save(db.getTag());
			break;

		default:
			break;
		}

	}

	private void edit() {  //编辑，点击编辑的时候跳转到相应的编辑页面
		switch (db.getType()) {
		case TOPIC:
			Intent in = new Intent(context, SendTopicActivity.class);
			Bundle b = new Bundle();
			b.putString("type", "2");// 草稿箱编辑页面启动的 所以传2
			b.putSerializable("draftBean", db);
			in.putExtras(b);
			context.startActivity(in);
			break;
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
	}

}
