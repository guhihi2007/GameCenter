package cn.lt.game.lib.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.net.NetIniCallBack;
import cn.lt.game.ui.app.community.CheckUserRightsTool;
import cn.lt.game.ui.app.community.group.GroupMemberActivity;
import cn.lt.game.ui.app.community.group.GroupMemberActivity.EventBean;
import cn.lt.game.ui.app.community.model.Category;
import cn.lt.game.ui.app.community.model.Group;
import cn.lt.game.ui.app.community.model.ShareBean;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.group.SpinerPopAdapter.IOnMenuSelectListener;
import cn.lt.game.ui.app.community.widget.ShareDialog;
import cn.lt.game.ui.app.community.widget.SpinerPopWindow;
import cn.lt.game.ui.app.search.SearchActivity;
import de.greenrobot.event.EventBus;

/**
 * Created by zhengweijian on 15/8/18.
 */
public class TitleMoreButton extends ImageButton implements View.OnClickListener, IOnMenuSelectListener {
	private MoreButtonType type;
	private ShareDialog sharedialog;
	private Activity mActivity;
	private Group groupInfo;

	public enum MoreButtonType {
		Default(), GameDetail(), TopicDetail(), TopicGroup(), BackHome(), Special(), GroupTopic();
		MoreButtonType(){
			this.isJoin = false;
		}
		boolean isJoin;
		public MoreButtonType setJoin(boolean flag) {
			this.isJoin = flag;
			return this;
		}

	}

	public TitleMoreButton(Context context) {
		this(context, null);
	}

	public TitleMoreButton(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public TitleMoreButton(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		setOnClickListener(this);
		type = MoreButtonType.Default;

	}

	public void setType(MoreButtonType type) {
		this.type = type;

	}

	public void setActivity(Activity act) {
		this.mActivity = act;
	}

	/***
	 * 设置小组话题列表跳转到小组成员列表需要的数据
	 * 
	 * @param groupInfo
	 */
	public void setGroupInfo(Group groupInfo) {
		this.groupInfo = groupInfo;
	}

	public void setShareBean(ShareBean sb, ShareDialog.ShareDialogType type) {
		if (sharedialog == null) {
			sharedialog = new ShareDialog(getContext(), type);
		}
		sharedialog.setShareBean(sb);
	}

	public void setSortListener(ShareDialog.ItopdetailSortCallback sortCallback) {
		if (sharedialog == null) {
			sharedialog = new ShareDialog(getContext(), ShareDialog.ShareDialogType.TopicDetail);
		}
		sharedialog.setSortCallback(sortCallback);
	}

	public void setTopicDetail(TopicDetail topicdetail) {
		sharedialog.setTopicDetail(topicdetail);
	}
	private SpinerPopWindow pop;
	@Override
	public void onClick(View v) {
		switch (type) {
		case GameDetail:
		case TopicDetail:
			if (sharedialog != null && !sharedialog.isShowing()&& NetUtils.isConnected(v.getContext())) {
				sharedialog.show();
			}else{
				ToastUtils.showToast(v.getContext(),"请检查您的网络！");
			}
			break;
		case TopicGroup:
			System.out.println("TopicGroup");
			break;
		case BackHome:
			ActivityActionUtils.jumpToHomeActivityIndex(getContext());
			break;
		case Special:
			ActivityActionUtils.activity_jump(getContext(), SearchActivity.class);
			break;
		/***
		 * 注意：用这个跳转的时候必须调用setGroupInfo()和setActivity方法，否则无法跳转到小组成员列表
		 */
		case GroupTopic:
			List<Category> list = new ArrayList<Category>();
			if(type.isJoin==true){
				list.add(new Category(0, "已加入"));
			}else{
				list.add(new Category(0, "加入小组"));
			}
			list.add(new Category(1, "小组成员"));
			pop = new SpinerPopWindow(mActivity, list);
			pop.showAtLocation(this, Gravity.CENTER, 0, 0);
			pop.setmMenuSelectListener(this);
			break;
		case Default:
			break;
		default:
			System.out.println("没设置类型");
			break;

		}
	}

	public void release() {
		sharedialog = null;
	}

	@Override
	public void onMenuClick(int pos) {
		if (pos == 0) {
			CheckUserRightsTool.instance().checkIsUserLoginAndGoinGroup(getContext(), groupInfo.group_id, new NetIniCallBack() {
				@Override
				public void callback(int code) {
					Log.i("zzz", "加入小组返回码===" + code);
					if (0 == code) {
						ToastUtils.showToast(mActivity, "已加入小组");
						List<Category> list = new ArrayList<Category>();
						list.add(new Category(0, "已经加入"));
						list.add(new Category(1, "小组成员"));
						pop.adapter.notifyDataSetChanged();
						EventBus.getDefault().post(new EventBean("refreshData"));
					}else if(-2==code){
						ToastUtils.showToast(mActivity, "已加入该小组，不能重复加入！");
					}
				}
			});
		} else if (pos == 1) {
			// 跳转到小组成员列表
			ActivityActionUtils.activity_Jump_Values(getContext(), GroupMemberActivity.class, "GroupMember", groupInfo);
		}
	}

}
