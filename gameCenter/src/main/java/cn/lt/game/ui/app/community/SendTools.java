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
import android.util.Log;
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
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.web.WebCallBackBase;
import cn.lt.game.net.Host.HostType;
import cn.lt.game.net.Net;
import cn.lt.game.ui.app.community.model.SendTopicBean;
import cn.lt.game.ui.notification.LTNotificationManager;

//发表话题工具类
public class SendTools {
    private volatile static SendTools mInstance = null;
    private final int UPLOAD = 1;
    private final int UPLOAD_FAILED = 2;
    private final int POST_SUCCESS = 3;
    private final int NON_SUCCESS = 7;//带有敏感词的发送标记
    private final int POST_FAILED = 4;
    private final int PARSE_FAILED = 5;
    private final int PATH_ERROR = 6;

    public static SendTools instance() {
        if (mInstance == null) {
            synchronized (SendTools.class) {
                if (mInstance == null) {
                    mInstance = new SendTools();
                }
            }
        }
        return mInstance;
    }

    public void sendtopic(final SendTopicBean sb, final Context cn) {
        initNotification(cn);// 初始化通知栏
        HandlerThread ht = new HandlerThread("ss");
        ht.start();
        Handler handler = new Handler(ht.getLooper()) {
            private ArrayList<UploadImage> uploadImageArrayList = new ArrayList<UploadImage>(); // 图片ids存放String
            private int photoNum; // 当前上传第几张图片

            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case UPLOAD: // 如果图片上传成功接着上传下一个图片
                        ArrayList<String> localpath = (ArrayList<String>) msg.obj;
                        if (photoNum != localpath.size()) {
                            Log.i("zzz", "开始上传图片");
                            uploadImage(localpath, photoNum, this, uploadImageArrayList, cn);// 上传第几张图片
                            photoNum = photoNum + 1;
                            int totalSize = localpath.size();
                            int rate = accuracy(photoNum, totalSize, 0);
                            if (rate <= 100) {
                                remoteView.setProgressBar(R.id.progressbar, 100, rate, false);
                                remoteView.setTextViewText(R.id.tv_percent, rate + "%");
                                builder.setTicker("正在提交...");
                                remoteView.setTextViewText(R.id.tv_tip, "正在提交");
                            }
                            if (rate == 100) {
                                destoryNotification();
                            }
                            manager.notify(PUBLISHTOPIC_ID, notification);
                        } else {
                            Log.i("zzz", "图片上传完成后继续发送");
                            sendPostTopicLast(sb.getTopic_title(), sb.getTopic_content(), "" + sb.getGroup_id(), sb.getCategory_id(), uploadImageArrayList, this);
                        }
                        break;
                    case UPLOAD_FAILED:  //上传图片失败（只要有一张失败就算是失败）
                        builder.setTicker("话题发表失败");
                        remoteView.setTextViewText(R.id.tv_tip, "话题发表失败");
                        destoryNotification();
                        DraftService.getSingleton(cn).update(sb.getTag());
                        EventTools.instance().send(EventTools.TOPIC_TAG, false, sb.getGroup_id(), 0);
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroup_id(), 0);
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        break;
                    case POST_SUCCESS: // 发表话题完全成功
                        Log.i("zzz", "完全成功");
                        manager.cancel(PUBLISHTOPIC_ID);
                        LTNotificationManager.getinstance().publishTopicMsg(cn, "话题发表成功", R.mipmap.ic_publish_success);
                        DraftService.getSingleton(cn).deleteByTag(sb.getTag());
                        EventTools.instance().send(EventTools.TOPIC_TAG, true, sb.getGroup_id(), 0);
                        EventTools.instance().send(EventTools.DRAFTS_TAG, true, sb.getGroup_id(), 0);
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        ToastUtils.showToast(cn, (String)msg.obj);
                        break;
                    case NON_SUCCESS://发表的话题包含敏感词
                        manager.cancel(PUBLISHTOPIC_ID);
                        ToastUtils.showToast(cn, (String) msg.obj);
                        EventTools.instance().send(EventTools.TOPIC_TAG, true, sb.getGroup_id(), 0);
                        EventTools.instance().send(EventTools.DRAFTS_TAG, true, sb.getGroup_id(), 0);
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        break;
                    case POST_FAILED: // 发送失败
                        Log.i("zzz", "发表失败");
                        ToastUtils.showToast(cn, (String) msg.obj);
                        manager.cancel(PUBLISHTOPIC_ID);
                        LTNotificationManager.getinstance().publishTopicMsg(cn, "话题发表失败", R.mipmap.ic_publish_failed);
                        destoryNotification();
                        DraftService.getSingleton(cn).update(sb.getTag());
                        EventTools.instance().send(EventTools.TOPIC_TAG, false, sb.getGroup_id(), 0);
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroup_id(), 0);
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        break;
                    case PARSE_FAILED: // 数据解析异常
                        destoryNotification();
                        DraftService.getSingleton(cn).update(sb.getTag());
                        EventTools.instance().send(EventTools.TOPIC_TAG, false, sb.getGroup_id(), 0);
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroup_id(), 0);
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        break;
                    case PATH_ERROR: // 图片路径有误
                        destoryNotification();
                        DraftService.getSingleton(cn).deleteByTag(sb.getTag());
                        EventTools.instance().send(EventTools.DRAFTS_TAG, false, sb.getGroup_id(), 0);
                        DraftsJudgeTools.instance().remove(sb.getTag());
                        break;
                    default:
                        destoryNotification();
                        break;
                }
            }

        };
        if (sb.getPaths().size() == 0) {   //如果没有图片  就直接发表话题
            sendPostTopicLast(sb.getTopic_title(), sb.getTopic_content(), "" + sb.getGroup_id(), sb.getCategory_id(), null, handler);
        } else {  //如果有图，就先上传图片
            if (!isExist(sb.getPaths())) {  //判断上传的图片在本地是否都存在（有可能被用户删除了）
                handler.sendEmptyMessage(PATH_ERROR);// 如果有图片不存在
                return;
            }
            Message msg = new Message();
            msg.what = UPLOAD;
            msg.obj = sb.getPaths();
            handler.sendMessage(msg); // 开始上传图片
        }
    }

    private void destoryNotification() {
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

    private boolean isExist(ArrayList<String> al) {  //判断本地图片是否都存在
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

    private void sendPostTopicLast(String topic_title, String topic_content,//没有图片 或者图片全都上传成功的情况下 最后集中发送post请求
                                   String group_id, String category_id, ArrayList<UploadImage> uploadImageArrayList, final Handler hand) {
        HashMap<String, String> ha = new HashMap<String, String>();
        ha.put("group_id", "" + group_id);
        ha.put("category_id", category_id);

        String uploadContent;
        if (uploadImageArrayList != null) {
            //替换图片路劲
            uploadContent = convertTopicContent(topic_content, uploadImageArrayList);
        } else {
            uploadContent = topic_content;
        }
        //Base64编码
        byte[] encode = Base64.encode(uploadContent.getBytes(), Base64.DEFAULT);
        uploadContent = new String(encode);

        ha.put("topic_content", uploadContent);
        ha.put("topic_title", topic_title);
        if (uploadImageArrayList == null) {
        } else {
            ha.put("resource_ids", getString(uploadImageArrayList));
        }
        Net.instance().executePost(HostType.FORUM_HOST, cn.lt.game.net.Uri.COM_TOPICS_CREATE, ha, new WebCallBackBase() {
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
                        hand.sendMessage(createMessage(POST_FAILED, jb.getString("message")));
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

    private String getString(ArrayList<UploadImage> aa) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < aa.size(); i++) {
            sb.append(aa.get(i).resourceId + ",");
        }
        return sb.toString().substring(0, sb.toString().length() - 1);
    }

    private void uploadImage(final ArrayList<String> al, int photoNum2,  //上传图片
                             final Handler handler, final ArrayList<UploadImage> uploadImageArrayList, Context cnn) {

        final String imagePath = al.get(photoNum2);

        Map<String, Object> ha = new HashMap<String, Object>();
//        ha.put("file", Utils.Bitmap2InputStream(PhotoCompress.getInstance(cnn)
//                .getCompressBitMap(imagePath)));
        ha.put("file", Utils.Bitmap2InputStream(BitmapFactory.decodeFile(imagePath)));
        ha.put("use", "topic");
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

    private Message createMessage(int response, String message) {
        Message msg = new Message();
        msg.what = response;
        msg.obj = message;
        if (null == message) {
            msg.obj = "数据库返回数据异常";
        }
        return msg;
    }

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
