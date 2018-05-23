package cn.lt.game.ui.app.community;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.service.DraftService;
import cn.lt.game.lib.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.community.face.FaceView;
import cn.lt.game.ui.app.community.face.FaceView.Work;
import cn.lt.game.ui.app.community.model.DraftBean;

//回复Activity
public class SendReplyActivity extends BaseActivity implements OnClickListener,
		Work {
	private ImageView face, keyboard;
	private EditText ed;
	private LinearLayout faceGroup;
	FaceView fv_face;
	private TextView title;
	private ImageButton back;
	private Button send;
	private int topicId;
	private int commentId;
	private int acceptorId;
	private int groupId;
	private String topic_title;
	private String topic_content;
	private String acceptorNickname;
	private String group_title;
	private boolean compileType = false;// 是否是编辑状态
	private DraftBean draftBean;// 如果是编辑状态可获得到这个BEAN

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_sendreply);
		getResout();
		initView();
		initListener();
	}

	private void getResout() { //有两种启动方式，有可能是编辑，有可能是新创建
		Bundle bundle = getIntent().getExtras();
		if (bundle.getString("type").equals("1")) { //正常跳转，新创建
			topicId = bundle.getInt("topicId");
			commentId = bundle.getInt("commentId");
			acceptorId = bundle.getInt("acceptorId");
			topic_title = bundle.getString("topic_title");
			topic_content = bundle.getString("topic_content");
			group_title = bundle.getString("group_title");
			acceptorNickname = bundle.getString("acceptorNickname");
			groupId = bundle.getInt("groupId");
		} else {   //如果从草稿箱页面跳转过来（编辑）
			draftBean = (DraftBean) getIntent().getExtras().get("draftBean");
			compileType = true;
			setData();
		}

	}

	private void setData() {
		topicId = draftBean.getTopic_Id();
		commentId = draftBean.getComment_id();
		acceptorId = draftBean.getAcceptor_id();
		acceptorNickname = draftBean.getAcceptorNickname();
		topic_title = draftBean.getTopic_title();
		topic_content = draftBean.getTopic_content();
		groupId = draftBean.getGroup_id();
		group_title = draftBean.getGroupTitle();
	}

	private void initListener() {
		back.setOnClickListener(this);
		send.setOnClickListener(this);
		face.setOnClickListener(this);
		keyboard.setOnClickListener(this);
		ed.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				faceGroup.setVisibility(View.GONE);
				face.setVisibility(View.VISIBLE);
				keyboard.setVisibility(View.GONE);
				return false;
			}
		});
	}

	private void initView() {
		back = (ImageButton) findViewById(R.id.back);
		send = (Button) findViewById(R.id.send);
		title = (TextView) findViewById(R.id.title);
		title.setText(getResources().getText(R.string.send_reply));
		faceGroup = (LinearLayout) findViewById(R.id.facegroup);
		ed = (EditText) findViewById(R.id.ed);
		ed.setHint("@" + acceptorNickname);
		if (compileType) {
			ed.setText(draftBean.getReply_content());
		}
		face = (ImageView) findViewById(R.id.face);
		keyboard = (ImageView) findViewById(R.id.keyboard);
		initface();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.face:
			face();
			break;
		case R.id.keyboard:
			keyboard();
			break;
		case R.id.send:  //发送回复
			try {
				if (ed.getText().toString().getBytes("GBK").length > 40000) {
					ToastUtils.showToast(SendReplyActivity.this,
							"内容最大长度不可超过40000字哦");
					return;
				}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			if (!ed.getText().toString().trim().isEmpty()) {
				if (compileType) { // 如果是编辑状态的
					String tag = draftBean.getTag();
					if (tag != null) {
						// 删除老的-----------------------------------------
						DraftService.getSingleton(this).deleteByTag(tag);
					}
					EventTools.instance().sendDftsDeleteByEdit(
							draftBean.getTag());
				}
				DraftBean db = new DraftBean();
				db.setState(2); //
				setTag(db);
				db.setAcceptor_id(acceptorId);
				db.setAcceptorNickname(acceptorNickname);
				db.setComment_id(commentId);
				db.setReply_content(ed.getText().toString());
				db.setTopic_Id(topicId);
				db.setGroup_id(groupId);
				db.setTopic_content(topic_content);
				db.setTopic_title(topic_title);
				db.setGroupTitle(group_title);
				db.setType(2);
				// 执行Db存语句
				// 从数据库取出未发送的发送
				try {
					DraftBean cansavedb = LocalHelpTools.instance(
							SendReplyActivity.this).chageCanSave_Reply(db);
					if (DraftService.getSingleton(this).save(cansavedb)) {
						// 从数据库取出未发送的发送
						SqlQueueTools.instance()
								.sendToQueue(SqlQueueTools.REPLY, db,
										SendReplyActivity.this);
						finish();
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
					ToastUtils.showToast(SendReplyActivity.this, "数据异常发送失败");
					return;
				}

			} else {
				ToastUtils.showToast(SendReplyActivity.this, "请输入回复内容");
			}
			break;
		case R.id.back:
			finish();
			break;
		default:
			break;
		}

	}

	private void keyboard() {
		face.setVisibility(View.VISIBLE);
		keyboard.setVisibility(View.GONE);
		faceGroup.setVisibility(View.GONE);
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.showSoftInput(ed, 0);
	}

	private void face() {
		face.setVisibility(View.GONE);
		keyboard.setVisibility(View.VISIBLE);
		faceGroup.setVisibility(View.VISIBLE);
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(ed.getWindowToken(), 0);
	}

	private void initface() { // 初始化表情 以及键盘隐藏或者显示等等状态
		keyboard.setVisibility(View.GONE);
		face.setVisibility(View.VISIBLE);
		faceGroup.setVisibility(View.GONE);
		fv_face = new FaceView(this, null, this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		faceGroup.addView(fv_face, params);
	}

	@Override
	public void onClick(String item_str) {
		replace(item_str, ed);
	}

	private void replace(String item_str, EditText view) {
		SpannableString spannable = new SpannableString(item_str); //
		view.append(spannable);
	}

	@SuppressWarnings("unused")
	private void replace(int id, String item_str, EditText view) {
		Drawable drawable = getResources().getDrawable(id); // 要出入的图片
		drawable.setBounds(0, 0, (int) ed.getTextSize() * 4 / 3,
				(int) ed.getTextSize() * 4 / 3); // 需要处理的文本，[smile]是需要被替代的文本
		SpannableString spannable = new SpannableString(item_str); //
		ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
		spannable.setSpan(span, 0, spannable.length(),
				Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		view.append(spannable);

	}

	public static Intent getIntent(Context context, int groupId, int topicId,
			int commentId, int acceptorId, String acceptorNickname,
			String topic_title, String topic_content, String group_title,
			String type) {
		Intent in = new Intent(context, SendReplyActivity.class);
		Bundle bd = new Bundle();
		bd.putString("type", type);
		bd.putString("acceptorNickname", acceptorNickname);
		bd.putString("topic_title", topic_title);
		bd.putString("topic_content", topic_content);
		bd.putString("group_title", group_title);
		bd.putInt("commentId", commentId);
		bd.putInt("topicId", topicId);
		bd.putInt("groupId", groupId);
		bd.putInt("acceptorId", acceptorId);
		in.putExtras(bd);
		return in;
	}

	private void setTag(DraftBean sb) {
		if (sb.getTag().equals("0")) {
			sb.setTag( TimeUtils.getCurrentTimeInString());
		}
	}

	@Override
	public void setPageAlias() {
		// TODO Auto-generated method stub
		
	}
}
