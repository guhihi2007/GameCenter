package cn.lt.game.ui.app.community;

import cn.lt.game.ui.app.community.model.DraftBean;
import cn.lt.game.ui.app.community.model.SendCommentBean;
import cn.lt.game.ui.app.community.model.SendReplyBean;
import cn.lt.game.ui.app.community.model.SendTopicBean;

//草稿箱里面的内容转换工具类
public class DraftsBeanTools {
	// 转换成话题Bean
	public static SendTopicBean toTopic(DraftBean db) {
		SendTopicBean sb = new SendTopicBean();
		sb.setCategoryList(db.getCategoryList());
		sb.setCategory_id(db.getCategory_id());
		sb.setGroup_id(db.getGroup_id());
		sb.setPaths(db.getTopic_paths());
		sb.setTag(db.getTag());
		sb.setTopic_content(db.getTopic_content());
		sb.setTopic_title(db.getTopic_title());
		return sb;
	}

	// 转换成评论Bean
	public static SendCommentBean toComment(DraftBean db) {
		SendCommentBean sb = new SendCommentBean();
		sb.setComment_content(db.getComment_content());
		sb.setGroupId(db.getGroup_id());
		sb.setAutoJump(db.isAutoJump());
		sb.setPaths(db.getComment_paths());
		sb.setTag(db.getTag());
		sb.setGroup_title(db.getGroupTitle());
		sb.setTopicId(db.getTopic_Id());
		return sb;
	}

	// 转换成回复Bean
	public static SendReplyBean toReply(DraftBean db) {
		SendReplyBean sb = new SendReplyBean();
		sb.setAcceptorId(db.getAcceptor_id());
		sb.setAcceptorNickname(db.getAcceptorNickname());
		sb.setCommentId(db.getComment_id());
		sb.setContent(db.getReply_content());
		sb.setTag(db.getTag());
		sb.setTopicId(db.getTopic_Id());
		sb.setGroupId(db.getGroup_id());
		return sb;
	}
}
