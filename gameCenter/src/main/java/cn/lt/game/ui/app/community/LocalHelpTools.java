package cn.lt.game.ui.app.community;

import android.content.Context;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cn.lt.game.lib.util.file.CacheFileUtil;
import cn.lt.game.ui.app.community.model.DraftBean;
//要保存到本地数据库的时候 需要转换一下再保存，因为内容有可能过长，图片数量有可能过多，本地数据库存储不了的直接存储为文件。
public class LocalHelpTools {
	private volatile static LocalHelpTools mInstance = null;
	private Context cn;

	public static LocalHelpTools instance(Context cn) {
		if (mInstance == null) {
			synchronized (LocalHelpTools.class) {
				if (mInstance == null) {
					mInstance = new LocalHelpTools(cn);
				}
			}
		}
		return mInstance;
	}

	private LocalHelpTools(Context cn) {
		this.cn = cn;
	}
    //将草稿箱里面的转换成可以存储的话题
	public DraftBean chageCanSave_Topic(DraftBean db)
			throws UnsupportedEncodingException {
		DraftBean dd = new DraftBean();
		dd.setState(db.getState()); //
		dd.setCategoryList(db.getCategoryList());
		dd.setTag(db.getTag());
		dd.setGroup_id(db.getGroup_id());
		dd.setTopic_title(db.getTopic_title());
		dd.setCategory_id(db.getCategory_id());
		dd.setType(db.getType());
		String content = db.getTopic_content();
		ArrayList<String> paths = db.getTopic_paths();
		if (content.getBytes("GBK").length > 4000) {//如果字数超过4000就采用本地文件存储
			dd.setTopic_content("");
			dd.setLocal_topicContent(dd.getTag() + "topic_content");
			saveString(dd.getLocal_topicContent(), content);//本地文件存储，传入2个参数一个是文件名字，一个是上下文对象
		} else {
			dd.setTopic_content(content);
		}
		if (paths.size() > 60) {//如果图片超过60张就做本地文件存储
			dd.setTopic_paths(new ArrayList<String>());
			dd.setLocal_topicPaths(dd.getTag() + "topic_paths");
			saveObject(dd.getLocal_topicPaths(), paths);//本地文件存储，传入2个参数一个是文件名字，一个是上下文对象
		} else {
			dd.setTopic_paths(paths);
		}
		return dd;

	}

	public DraftBean chageCanSave_Comment(DraftBean db)   //转换成可存储的评论
			throws UnsupportedEncodingException {
		DraftBean dd = new DraftBean();
		dd.setState(dd.getState()); //
		dd.setTag(db.getTag());
		dd.setGroup_id(db.getGroup_id());
		dd.setTopic_title(db.getTopic_title());
		dd.setAutoJump(db.isAutoJump());
		dd.setTopic_Id(db.getTopic_Id());
		dd.setGroupTitle(db.getGroupTitle());
		dd.setType(1);

		String content = db.getComment_content();
		ArrayList<String> paths = db.getComment_paths();
		String topic_content = db.getTopic_content();
		if (content.getBytes("GBK").length > 4000) {
			dd.setComment_content("");
			dd.setLocal_commentContent(dd.getTag() + "comment_content");
			saveString(dd.getLocal_commentContent(), content);
		} else {
			dd.setComment_content(content);
		}
		if (topic_content.getBytes("GBK").length > 4000) {
			dd.setTopic_content("");
			dd.setLocal_topicContent(dd.getTag() + "topic_content");
			saveString(dd.getLocal_topicContent(), topic_content);
		} else {
			dd.setTopic_content(topic_content);
		}
		if (paths.size() > 60) {
			dd.setComment_paths(new ArrayList<String>());
			dd.setLocal_commentPaths(dd.getTag() + "comment_paths");
			saveObject(dd.getLocal_commentPaths(), paths);
		} else {
			dd.setComment_paths(paths);
		}
		return dd;

	}

	public DraftBean chageCanSave_Reply(DraftBean db)//转换成可存储的回复
			throws UnsupportedEncodingException {
		DraftBean dd = new DraftBean();
		dd.setState(2); //
		dd.setTag(db.getTag());
		dd.setAcceptor_id(db.getAcceptor_id());
		dd.setAcceptorNickname(db.getAcceptorNickname());
		dd.setComment_id(db.getComment_id());
		dd.setTopic_Id(db.getTopic_Id());
		dd.setGroup_id(db.getGroup_id());
		dd.setTopic_title(db.getTopic_title());
		dd.setGroupTitle(db.getGroupTitle());
		dd.setType(2);

		String content = db.getReply_content();
		String topic_content = db.getTopic_content();
		if (content.getBytes("GBK").length > 4000) {
			dd.setReply_content("");
			dd.setLocal_replyContent(dd.getTag() + "reply_content");
			saveString(dd.getLocal_replyContent(), content);
		} else {
			dd.setReply_content(content);
		}
		if (topic_content.getBytes("GBK").length > 4000) {
			dd.setTopic_content("");
			dd.setLocal_topicContent(dd.getTag() + "topic_content");
			saveString(dd.getLocal_topicContent(), topic_content);
		} else {
			dd.setTopic_content(topic_content);
		}
		return dd;

	}

	private boolean saveString(String filename, String content) {   //本地存储字符换
		return CacheFileUtil.caheData(filename, content, cn);
	}

	private boolean saveObject(String filename, Object obj) {    //本地存储对象
		return CacheFileUtil.caheObject(filename, obj, cn);
	}

	public String getStringContent(String filename) {
		return CacheFileUtil.getCacheFromFile(filename, cn);
	}

	public ArrayList<String> getPaths(String filename) {
		return CacheFileUtil.getObjectFromFile(filename, cn);
	}
}
