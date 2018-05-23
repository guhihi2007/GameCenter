package cn.lt.game.ui.app.community;

import cn.lt.game.ui.app.community.model.CommentEvent;
import cn.lt.game.ui.app.community.model.DraftBean;
import cn.lt.game.ui.app.community.model.DraftsDeletEvent;
import cn.lt.game.ui.app.community.model.DraftsDeleteByEditeEvent;
import cn.lt.game.ui.app.community.model.DraftsEvent;
import cn.lt.game.ui.app.community.model.JumpEvent;
import cn.lt.game.ui.app.community.model.ReplyEvent;
import cn.lt.game.ui.app.community.model.TopicEvent;
import de.greenrobot.event.EventBus;

//EventBus发送工具类
public class EventTools {
	private volatile static EventTools mInstance = null;
	public static final int DRAFTS_TAG = 1;// 草稿箱类型
	public static final int TOPIC_TAG = 2;// 话题类型
	public static final int REPLY_TAG = 3;// 回复类型
	public static final int COMMENT_TAG = 4;// 评论类型
	public static final int JUMP_TAG = 5;// 跳转通知
	public static final int CLEAR_TAG = 6;// 清空event
	public static final int DRAFTSDELET_TAG = 7;// 草稿箱删除
	public static final int DRAFTSDELETBYEDIT_TAG = 8;// 草稿箱删除

	public static EventTools instance() {
		if (mInstance == null) {
			synchronized (EventTools.class) {
				if (mInstance == null) {
					mInstance = new EventTools();
				}
			}
		}
		return mInstance;
	}

	public void send(int type, Boolean result, int grouId, int topicId) {
		switch (type) {
		case DRAFTS_TAG:
			EventBus.getDefault().post(
					createDraftsEvnen(result, grouId, topicId));
			break;
		case TOPIC_TAG:
			EventBus.getDefault().post(
					createTopicEvnen(result, grouId, topicId));
			break;
		case REPLY_TAG:
			EventBus.getDefault().post(
					createReplyEvnen(result, grouId, topicId));
			break;
		case COMMENT_TAG:
			EventBus.getDefault().post(
					createCommentEvnen(result, grouId, topicId));
			break;
		case JUMP_TAG:
			EventBus.getDefault()
					.post(createJumpEvnen(result, grouId, topicId));
			break;
		default:
			break;
		}

	}

	public void sendDftsDelete(DraftBean db) { // 发送草稿箱删除Evnent
		EventBus.getDefault().post(new DraftsDeletEvent(db));
	}

	public void sendDftsDeleteByEdit(String time) { // 发送草稿箱删除由于编辑造成的
		EventBus.getDefault().post(new DraftsDeleteByEditeEvent(time));
	}

	private JumpEvent createJumpEvnen(Boolean result, int grouId, int topicId) {// 创建跳转Event
		// TODO Auto-generated method stub
		return new JumpEvent(result, grouId, topicId);
	}

	private DraftsEvent createDraftsEvnen(Boolean result, int grouId,// 创建草稿箱Event
			int topicId) {
		return new DraftsEvent(result, grouId, topicId);
	}

	private TopicEvent createTopicEvnen(Boolean result, int grouId, int topicId) {// 创建话题Event
		return new TopicEvent(result, grouId, topicId);
	}

	private CommentEvent createCommentEvnen(Boolean result, int grouId,// 创建评论Event
			int topicId) {
		return new CommentEvent(result, grouId, topicId);
	}

	private ReplyEvent createReplyEvnen(Boolean result, int grouId, int topicId) {// 创建回复Event
		return new ReplyEvent(result, grouId, topicId);
	}

}
