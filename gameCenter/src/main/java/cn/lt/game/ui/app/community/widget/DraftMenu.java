package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.community.DraftsJudgeTools;
import cn.lt.game.ui.app.community.model.DraftBean;
//草稿箱Item上面的菜单选项，点击弹出重发，删除，编辑的Dialog
public class DraftMenu extends ImageView implements OnClickListener {
	private Context context;
	private DraftBean db;

	public DraftMenu(Context context, AttributeSet attrs) {   //创建的时候设置好监听器
		super(context, attrs);
		this.context = context;
		setOnClickListener(this);
	}

	public DraftBean getDb() {
		return db;
	}

	public void setDb(DraftBean db) { //绑定数据源，数据源为从数据库取出的话题Bean或者回复Bean或者评论Bean。
		this.db = db;
	}
    
	@Override
	public void onClick(View v) {  //点击的时候判断数据是否正在发送，如果正在发送，就提示用户正在发送
		DraftsDialog fd = new DraftsDialog(context, db);
		if (DraftsJudgeTools.instance().showDialog(db.getTag())) {
			fd.show();
		} else {
			ToastUtils.showToast(context, "数据正在发送中，请勿打扰");
		}

	}

}
