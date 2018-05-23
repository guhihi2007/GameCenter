package cn.lt.game.ui.app.community;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.lib.util.image.BitmapUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.community.personalpage.PersonalActivity;
import cn.lt.game.ui.app.community.widget.BubbleTextView;

/**
 * Created by wenchao on 2015/11/26.
 */
public class MyPrivateMessageDetailAdapter extends BaseAdapter {

    private List<MyPrivateMessageItem> mList;
    private Context mContext;

    private boolean isNeedBottom = true;

    private int friendUserId;

    public MyPrivateMessageDetailAdapter(Context context, int friendUserId) {
        mContext = context;
        mList = new ArrayList<>();
        this.friendUserId = friendUserId;
    }

    public void setList(List<MyPrivateMessageItem> list) {
        mList = list;
        handleTimeTag(mList);
        this.notifyDataSetChanged();
    }

    /**
     * 从最底部时间开始计算，
     * 间隔超过30分钟后的第一项显示时间
     *
     * @param list
     */
    void handleTimeTag(List<MyPrivateMessageItem> list) {
        String lastTime = null;
        for (int i = list.size() - 1; i >= 0; i--) {
            MyPrivateMessageItem item = list.get(i);
            if (i == 0) {//最顶上的一定要有时间显示
                item.needShowTime = true;
                break;
            }
            String time = item.time;
            if (lastTime == null) {
                lastTime = time;
                continue;
            }
            long interval = TimeUtils.getIntervalTime(lastTime, time);
            if (interval / 1000 / 60 > 30) {//间隔大于30分钟
                list.get(i + 1).needShowTime = true;
                lastTime = time;
            }


        }
    }

    public void setNeedBottom(boolean is) {
        this.isNeedBottom = is;
    }

    public void add(MyPrivateMessageItem item) {
        mList.add(item);
        handleTimeTag(mList);
        this.notifyDataSetChanged();
    }

    public void appendToList(List<MyPrivateMessageItem> list) {
        mList.addAll(0, list);
        handleTimeTag(mList);
        this.notifyDataSetChanged();
    }

    public ArrayList<String> getImageList() {
        ArrayList<String> imageList = new ArrayList<>();
        for (int i = mList.size() - 1; i >= 0; i--) {
            MyPrivateMessageItem item = mList.get(i);
            if (item.messageType == MyPrivateMessageDetailActivity.MSG_LEFT_IMAGE || item.messageType == MyPrivateMessageDetailActivity.MSG_RIGHT_IMAGE) {
                imageList.add(getImagePath(item));
            }
        }
        return imageList;
    }

    private String getImagePath(MyPrivateMessageItem item) {
        final String imagePath;
        if (!TextUtils.isEmpty(item.locaImage)) {
            imagePath = "file://" + item.locaImage;
        } else if (!TextUtils.isEmpty(item.remoteImage)) {
            imagePath = item.remoteImage;
        } else {
            imagePath = "";
        }
        return imagePath;
    }

    @Override
    public int getItemViewType(int position) {
        return mList.get(position).messageType;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = getItemViewType(position);
        final ViewHolder h;
        if (convertView == null) {
            h = new ViewHolder();
            switch (viewType) {
                case MyPrivateMessageDetailActivity.MSG_LEFT_TEXT:
                    convertView = View.inflate(mContext, R.layout.view_computer, null);
                    h.head = (ImageView) convertView.findViewById(R.id.feedback_listitem_computerIcon);
                    h.text = (TextView) convertView.findViewById(R.id.feedback_listitem_computerChat);
                    h.time = (TextView) convertView.findViewById(R.id.time);
                    break;
                case MyPrivateMessageDetailActivity.MSG_LEFT_IMAGE:
                    convertView = View.inflate(mContext, R.layout.item_left_image, null);
                    h.head = (ImageView) convertView.findViewById(R.id.feedback_listitem_icon);
                    h.image = (ImageView) convertView.findViewById(R.id.feedback_listitem_image);
                    h.time = (TextView) convertView.findViewById(R.id.time);
                    break;
                case MyPrivateMessageDetailActivity.MSG_RIGHT_TEXT:
                    convertView = View.inflate(mContext, R.layout.view_user_chat, null);
                    h.head = (ImageView) convertView.findViewById(R.id.feedback_listitem_userIcon);
                    h.text = (TextView) convertView.findViewById(R.id.feedback_listitem_userChat);
                    h.progressLayout = convertView.findViewById(R.id.feedback_listitem_userProgressBar);
                    h.sendFaildIcon = (ImageView) convertView.findViewById(R.id.feedback_listItem_userFailure);
                    h.time = (TextView) convertView.findViewById(R.id.time);
                    break;
                case MyPrivateMessageDetailActivity.MSG_RIGHT_IMAGE:
                    convertView = View.inflate(mContext, R.layout.item_right_image, null);
                    h.head = (ImageView) convertView.findViewById(R.id.feedback_listitem_userIcon);
                    h.image = (ImageView) convertView.findViewById(R.id.feedback_listitem_image);
                    h.progressLayout = convertView.findViewById(R.id.feedback_listitem_progressLayout);
                    h.sendFaildIcon = (ImageView) convertView.findViewById(R.id.feedback_listItem_failure);
                    h.progressText = (TextView) convertView.findViewById(R.id.feedback_listItem_progressText);
                    h.time = (TextView) convertView.findViewById(R.id.time);
                    break;
            }

            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }


        final MyPrivateMessageItem item = mList.get(position);
        //头像
        ImageloaderUtil.loadUserHead(mContext, item.headIcon, h.head);

        if (item.needShowTime) {
            h.time.setVisibility(View.VISIBLE);
            h.time.setText(item.time);
        } else {
            h.time.setVisibility(View.GONE);
            h.time.setText("");
        }

        switch (viewType) {
            case MyPrivateMessageDetailActivity.MSG_LEFT_TEXT:
                if (TextUtils.isEmpty(item.content)) {
                    item.content = "";
                }
                h.text.setText(HtmlUtils.convertHtmlToString(item.content));
                h.text.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showCopyPop(v, item.content);
                        return true;
                    }
                });
                h.head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Value(mContext, PersonalActivity.class, "userId", friendUserId);
                    }
                });

                break;
            case MyPrivateMessageDetailActivity.MSG_LEFT_IMAGE:
                if (TextUtils.isEmpty(item.remoteImage)) {
                    item.remoteImage = "";
                }
//                ImageLoader.getInstance().display(item.remoteImage, h.image, R.mipmap.icon_feedback_failed, new ImageLoadListener(h.image, item.remoteImage, (ListView) parent));
                ImageloaderUtil.loadImage(mContext, item.remoteImage, h.image, false);
                h.head.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityActionUtils.activity_Jump_Value(mContext, PersonalActivity.class, "userId", friendUserId);
                    }
                });
                break;
            case MyPrivateMessageDetailActivity.MSG_RIGHT_TEXT:
                if (TextUtils.isEmpty(item.content)) {
                    item.content = "";
                }
                h.sendFaildIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //重新发送文字
                        ((MyPrivateMessageDetailActivity) mContext).sendTextMsgRetry(item);
                    }
                });
                h.text.setText(HtmlUtils.convertHtmlToString(item.content));
                switch (item.sendStatus) {
                    case MyPrivateMessageDetailActivity.SEND_SUCCESS:
                        h.progressLayout.setVisibility(View.GONE);
                        h.sendFaildIcon.setVisibility(View.GONE);
                        break;
                    case MyPrivateMessageDetailActivity.SEND_FAILED:
                        h.progressLayout.setVisibility(View.GONE);
                        h.sendFaildIcon.setVisibility(View.VISIBLE);
                        break;
                    case MyPrivateMessageDetailActivity.SEND_ING:
                        h.progressLayout.setVisibility(View.VISIBLE);
                        h.sendFaildIcon.setVisibility(View.GONE);
                        break;
                }
                h.text.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showCopyPop(v, item.content);
                        return true;
                    }
                });
                h.head.setOnClickListener(null);

                break;
            case MyPrivateMessageDetailActivity.MSG_RIGHT_IMAGE:
                h.head.setOnClickListener(null);
                final String imagePath = getImagePath(item);

                h.sendFaildIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //重新发送图片
                        ((MyPrivateMessageDetailActivity) mContext).sendImageMsgRetry(item);
                    }
                });

//                ImageLoader.getInstance().display(imagePath, h.image, R.mipmap.icon_feedback_failed, new ImageLoadListener(h.image, imagePath, (ListView) parent));
                ImageloaderUtil.loadImage(mContext, imagePath, h.image, false);
                switch (item.sendStatus) {
                    case MyPrivateMessageDetailActivity.SEND_SUCCESS:
                        h.progressLayout.setVisibility(View.GONE);
                        h.sendFaildIcon.setVisibility(View.GONE);
                        break;
                    case MyPrivateMessageDetailActivity.SEND_FAILED:
                        h.progressLayout.setVisibility(View.GONE);
                        h.sendFaildIcon.setVisibility(View.VISIBLE);
                        break;
                    case MyPrivateMessageDetailActivity.SEND_ING:
                        h.progressLayout.setVisibility(View.VISIBLE);
                        h.sendFaildIcon.setVisibility(View.GONE);
                        h.progressText.setText(item.progress + "%");
                        break;
                }

                break;
        }
        return convertView;
    }

    private static class ViewHolder {
        public ImageView head;
        public TextView text;
        public ImageView image;
        public View progressLayout;
        public ImageView sendFaildIcon;
        public TextView progressText;
        public TextView time;
    }

    private PopupWindow copyWindow;

    private void showCopyPop(View anchor, String copyText) {
        if (copyWindow == null) {
            copyWindow = createPopupWindow("复制", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String text = (String) v.getTag();
                    Utils.copy(v.getContext(), text);
                    copyWindow.dismiss();
                }
            });
        }
        if (copyWindow.isShowing()) {
            copyWindow.dismiss();
        }
        int[] widthAndHeight = Utils.getWidthAndHeight(copyWindow.getContentView());

        copyWindow.getContentView().setTag(copyText);
        copyWindow.showAsDropDown(anchor, anchor.getMeasuredWidth() / 2 - widthAndHeight[0] / 2, -anchor.getMeasuredHeight() - widthAndHeight[1]);
    }


    private PopupWindow saveWindow;

    private void showSaveWindow(View anchor, final Bitmap bitmap) {
        if (saveWindow == null) {
            saveWindow = createPopupWindow("保存到手机", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Bitmap image = (Bitmap) v.getTag();
                    try {
                        String fileName = TTGC_DirMgr.getCachePicDirectory() + File.separator + "PIC_" + System.currentTimeMillis() + ".jpg";
                        BitmapUtil.saveBitmapToFile(image, fileName);
                        ToastUtils.showToast(v.getContext(), "照片已保存到:" + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    saveWindow.dismiss();
                }
            });
        }
        if (saveWindow.isShowing()) {
            saveWindow.dismiss();
        }
        int[] widthAndHeight = Utils.getWidthAndHeight(saveWindow.getContentView());
        saveWindow.getContentView().setTag(bitmap);
        saveWindow.showAsDropDown(anchor, anchor.getMeasuredWidth() / 2 - widthAndHeight[0] / 2, -anchor.getMeasuredHeight() - widthAndHeight[1]);
    }


    private PopupWindow createPopupWindow(String text, View.OnClickListener onClickListener) {
        View contentView = View.inflate(mContext, R.layout.popup_msg_item_click, null);
        PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());

        BubbleTextView textView = (BubbleTextView) contentView.findViewById(R.id.bubbleTextView);
        textView.setText(text);
        contentView.setOnClickListener(onClickListener);
        return popupWindow;
    }


}
