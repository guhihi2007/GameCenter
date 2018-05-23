package cn.lt.game.ui.app.community;

import android.app.Notification;
import android.app.Notification.Builder;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.format.Time;
import android.util.Base64;
import android.widget.RemoteViews;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.db.service.DraftService;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.web.WebCallBackBase;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.ui.app.community.model.SendCommentBean;
import cn.lt.game.ui.notification.LTNotificationManager;

//发送评论工具类，发送评论图片张数有限制 ，9张
public class SendCommentTools {
    private volatile static SendCommentTools mInstance = null;
    private final int UPLOAD = 1;
    private final int UPLOAD_FAILED = 2;
    private final int POST_SUCCESS = 3;
    private final int POST_FAILED = 4;
    private final int PARSE_FAILED = 5;
    private final int PATH_ERROR = 6;
    private final int NON_SUCCESS = 7;//待敏感词的评论状态

    public static SendCommentTools instance() {
        if (mInstance == null) {
            synchronized (SendCommentTools.class) {
                if (mInstance == null) {
                    mInstance = new SendCommentTools();
                }
            }
        }
        return mInstance;
    }

    public void sendComment(final SendCommentBean sb, final Context cn) { // 发送评论
        initNotification(cn);// 初始化通知栏
        HandlerThread ht = new HandlerThread("sss");
        ht.start();
        Handler handler = new Handler(ht.getLooper()) {
            private ArrayList<UploadImage> uploadImageArrayList = new ArrayList<UploadImage>(); // 图片ids存放String
            private int photoNum; // 当前上传第几张图片

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case UPLOAD: // 如果图片上传成功接着上传下一个图片
                        ArrayList<String> localpath = (ArrayList<String>) msg.obj;
                        if (photoNum != localpath.size()) {
                            uploadImage(localpath, photoNum, this, uploadImageArrayList, cn);// 上传第几张图片
                            photoNum = photoNum + 1;
                            int totalSize = localpath.size();
                            int rate = accuracy(photoNum, totalSize, 0);
                            if (rate <= 100) {
                                remoteView.setProgressBar(R.id.progressbar, 100, rate, false);
                                remoteView.setTextViewText(R.id.tv_percent, rate + "%");
                                builder.setTicker("正在提交...");
                                remoteView.setTextViewText(R.id.tv_tip, "正在提交...");

                            }
                            if (rate == 100) {
                                destoryNotification();
                            }
                            manager.notify(PUBLISHTOPIC_ID, notification);
                        } else {
                            sendCommentLast(sb.getComment_content(), uploadImageArrayList, this, sb.getTopicId());
                        }
                        break;
                    case UPLOAD_FAILED: // 上传图片失败
                        LogUtils.i("zzz", "upload failed...");
                        manager.cancel(PUBLISHTOPIC_ID);
                        LTNotificationManager.getinstance().publishTopicMsg(cn, "评论发表失败", R.mipmap.ic_publish_failed);
                        destoryNotification();
                        DraftService.getSingleton(cn).update(sb.getTag());
                        EventTools.instance().send(EventTools.COMMENT_TAG, false, sb.getGroupId(), sb.getTopicId());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroupId(), sb.getTopicId());
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        break;
                    case POST_SUCCESS: // 评论完全发送成功
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        LogUtils.i("zzz", "评论完全成功");
                        manager.cancel(PUBLISHTOPIC_ID);
                        ToastUtils.showToast(cn, (String) msg.obj);
                        LTNotificationManager.getinstance().publishTopicMsg(cn, "评论发表成功", R.mipmap.ic_publish_success);
                        DraftService.getSingleton(cn).deleteByTag(sb.getTag());
                        EventTools.instance().send(EventTools.COMMENT_TAG, true, sb.getGroupId(), sb.getTopicId());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, true, sb.getGroupId(), sb.getTopicId());
                        break;
                    case NON_SUCCESS:
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        LogUtils.i("zzz", "评论内容包含敏感词也算发表成功");
                        ToastUtils.showToast(cn, (String)msg.obj);
                        manager.cancel(PUBLISHTOPIC_ID);
                        DraftService.getSingleton(cn).deleteByTag(sb.getTag());
                        EventTools.instance().send(EventTools.COMMENT_TAG, true, sb.getGroupId(), sb.getTopicId());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, true, sb.getGroupId(), sb.getTopicId());
                        break;
                    case POST_FAILED: // 发送失败
                        ToastUtils.showToast(cn, (String) msg.obj);
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        LogUtils.i("zzz", "发表失败");
                        manager.cancel(PUBLISHTOPIC_ID);
                        LTNotificationManager.getinstance().publishTopicMsg(cn, "评论发表失败", R.mipmap.ic_publish_failed);
                        destoryNotification();
                        DraftService.getSingleton(cn).update(sb.getTag());
                        EventTools.instance().send(EventTools.COMMENT_TAG, false, sb.getGroupId(), sb.getTopicId());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroupId(), sb.getTopicId());
                        break;

                    case PARSE_FAILED: // 数据解析异常
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        destoryNotification();
                        DraftService.getSingleton(cn).update(sb.getTag());
                        EventTools.instance().send(EventTools.COMMENT_TAG, false, sb.getGroupId(), sb.getTopicId());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroupId(), sb.getTopicId());
                        break;
                    case PATH_ERROR: // 图片路径有误
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        destoryNotification();
                        DraftService.getSingleton(cn).deleteByTag(sb.getTag());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroupId(), sb.getTopicId());
                        break;
                    default:
                        destoryNotification();
                        break;
                }
            }

        };
        if (sb.getPaths().size() == 0) {
            sendCommentLast(sb.getComment_content(), null, handler, sb.getTopicId());
        } else {
            if (!isExist(sb.getPaths())) {
                handler.sendEmptyMessage(PATH_ERROR);// 如果有图片不存在
                return;
            }
            Message msg = new Message();
            msg.what = UPLOAD;
            msg.obj = sb.getPaths();
            handler.sendMessage(msg); // 开始上传图片
        }
        if (sb.isAutoJump()) {
            jump(sb, cn);
        }
    }

    private void jump(SendCommentBean sb, Context cn) { // 跳转到详情页面
        EventTools.instance().send(EventTools.JUMP_TAG, true, sb.getGroupId(), sb.getTopicId());
        ActivityActionUtils.jumpToTopicDetail(cn, sb.getTopicId());
    }

    private void destoryNotification() { // 销毁通知
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                    manager.cancel(PUBLISHTOPIC_ID);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }).start();
    }

    private boolean isExist(ArrayList<String> al) { // 判断本地图片是否还存在
        for (int i = 0; i < al.size(); i++) {
            File file = new File(al.get(i));
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    public int accuracy(double num, double total, int scale) {
        DecimalFormat df = (DecimalFormat) NumberFormat.getInstance();
        // 可以设置精确几位小数
        df.setMaximumFractionDigits(scale);
        // 模式 例如四舍五入
        df.setRoundingMode(RoundingMode.HALF_UP);
        double accuracy_num = num / total * 100;
        return Integer.parseInt(df.format(accuracy_num));
    }

    /**
     * 把html文本中的本地图片地址替换为网络图片地址
     *
     * @param localContent
     * @param uploadImageArrayList
     * @return
     */
    private String convertTopicContent(String localContent, ArrayList<UploadImage> uploadImageArrayList) {
//        return HtmlUtils.replaceImgSrcValue(localContent, uploadImageArrayList);
		for (UploadImage uploadImage : uploadImageArrayList) {
			if (localContent.contains(uploadImage.path)) {
//				如果包含则替换
				localContent = localContent.replace(uploadImage.path, uploadImage.downUrl);
			}
		}
		return localContent;
    }

    private void sendCommentLast(String comment_content, ArrayList<UploadImage> uploadImageArrayList, final Handler hand, int topicId) { // 如果图片全都上传成功了，就执行最后的post发送任务请求。
        HashMap<String, String> ha = new HashMap<String, String>();
        String uploadContent;
        if (uploadImageArrayList != null) {
            //替换图片路劲
            uploadContent = convertTopicContent(comment_content, uploadImageArrayList);
        } else {
            uploadContent = comment_content;
        }
        //Base64编码
        byte[] encode = Base64.encode(uploadContent.getBytes(), Base64.DEFAULT);
        uploadContent = new String(encode);

        ha.put("comment_content", uploadContent);
        if (uploadImageArrayList == null) {
        } else {
            ha.put("resource_ids", getString(uploadImageArrayList));
        }
        Net.instance().executePost(HostType.FORUM_HOST, cn.lt.game.net.Uri.getCommentCreateUri(topicId), ha, new WebCallBackBase() {
                    @Override
                    public void route(String result) {
                        try {
                            JSONObject jb = new JSONObject(result);
                            int status = jb.getInt("status");
                            String msg = jb.getString("message");
                            if (1 == status) {
                                hand.sendMessage(createMessage(POST_SUCCESS, msg));
                            } else if (800 == status) {
                                hand.sendMessage(createMessage(NON_SUCCESS, msg));
                            } else {
                                hand.sendMessage(createMessage(POST_FAILED, msg));
                            }
                        } catch (JSONException e) {
                            hand.sendMessage(createMessage(PARSE_FAILED, "数据解析异常"));
                            e.printStackTrace();

                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        hand.sendMessage(createMessage(POST_FAILED, error.getMessage()));
                    }
                });

    }

    private String getString(ArrayList<UploadImage> uploadImageArrayList) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < uploadImageArrayList.size(); i++) {
            sb.append(uploadImageArrayList.get(i).resourceId + ",");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    private void uploadImage(final ArrayList<String> al, int photoNum2,  // 上传图片
                             final Handler handler, final ArrayList<UploadImage> uploadImageArrayList, Context cnn) {
        final String imagePath = al.get(photoNum2);
        Map<String, Object> ha = new HashMap<String, Object>();
//		ha.put("file", Utils.Bitmap2InputStream(PhotoCompress.getInstance(cnn)
//				.getCompressBitMap(imagePath)));
        ha.put("file", Utils.Bitmap2InputStream(BitmapFactory.decodeFile(imagePath)));
        ha.put("use", "comment");
        Net.instance().executePost(HostType.FORUM_HOST, cn.lt.game.net.Uri.COM_MULTIMEDIAS_PHOTOS_UPLOAD, ha, new WebCallBackBase() {
                    @Override
                    public void route(String result) {
                        try {
                            JSONObject jb = new JSONObject(result);
                            if (jb.getInt("status") == 1) {

                                String resourceId = jb.getJSONObject("data").getString("resource_id");
                                String url = jb.getJSONObject("data").getString("url");

                                UploadImage uploadImage = new UploadImage();
                                uploadImage.path = imagePath;
                                uploadImage.resourceId = resourceId;
                                uploadImage.downUrl = url;
                                uploadImageArrayList.add(uploadImage);


                                Message msg = new Message();
                                msg.what = UPLOAD;
                                msg.obj = al;
                                handler.sendMessage(msg);
                            } else {
                                handler.sendMessage(createMessage(UPLOAD_FAILED, jb.getString("message")));
                            }
                        } catch (JSONException e) {
                            handler.sendMessage(createMessage(PARSE_FAILED, "数据解析异常"));
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Throwable error) {
                        handler.sendMessage(createMessage(UPLOAD_FAILED, error.getMessage()));
                    }
                });
    }

    private Message createMessage(int response, String message) { // handler要发送的message封装类
        Message msg = new Message();
        msg.what = response;
        msg.obj = message;
        if (null == message) {
            msg.obj = "数据库返回数据异常";
        }
        return msg;
    }

    // 得到当前日期Uri
    public static Uri getDateUri() {
        Time t = new Time();
        t.setToNow();
        int year = t.year;
        int month = t.month;
        int day = t.monthDay;
        int hour = t.hour;
        int minute = t.minute;
        int second = t.second;
        String filename = "" + year + month + day + hour + minute + second;
        // 创建文件
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "sendyouxizhongxin";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }
        File filee = new File(path + "/" + filename + ".jpg");
        // 格式化为Uri
        Uri fileImageFilePath = Uri.fromFile(filee);
        return fileImageFilePath;
    }

    /***
     * 通知栏相关 add by ztl
     */
    private NotificationManager manager;
    private RemoteViews remoteView;
    private Notification notification;
    private Builder builder;
    private static final int PUBLISHTOPIC_ID = 20150527;

    private void initNotification(Context context) {
        manager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
        remoteView = new RemoteViews(context.getPackageName(), R.layout.topic_notification);
        notification = new Notification();
        notification.flags = Notification.FLAG_AUTO_CANCEL;// 点击后自动消失
        builder = new Notification.Builder(context);
        builder.setTicker("发送中...");
        builder.setSmallIcon(R.mipmap.ic_publishing);
        builder.setAutoCancel(true);
        // builder.setDefaults(Notification.DEFAULT_SOUND);
        // builder.setContentIntent(PendingIntent.getActivity(this, 0, new
        // Intent(Intent.ACTION_DELETE), 0));
        notification = builder.build();
        remoteView.setImageViewResource(R.id.iv_publishing, R.mipmap.ic_launcher);
        remoteView.setTextViewText(R.id.tv_tip, "发表中...");
        remoteView.setTextViewText(R.id.tv_time, formatTime(Calendar.getInstance().getTime()));
        notification.contentView = remoteView;
        manager.notify(PUBLISHTOPIC_ID, notification);
    }

    private static String formatTime(Date date) {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat myFmt = new SimpleDateFormat("hh : mm", Locale.US);
        GregorianCalendar gc = new GregorianCalendar();
        int m = gc.get(GregorianCalendar.AM_PM);
        if (m == 0) {
            sb.append("上午" + myFmt.format(date));
        } else {
            sb.append("下午" + myFmt.format(date));
        }
        return sb.toString();
    }
}
