package cn.lt.game.ui.app.community.topic;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.ui.app.community.model.TopicDetail;
import cn.lt.game.ui.app.community.topic.group.GroupTopicActivity;
import cn.lt.game.ui.app.community.topic.my.TopicItemByMyPublish;
import cn.lt.game.ui.app.community.widget.MoreButton;
import de.greenrobot.event.EventBus;

/**
 * 最新话题列表适配器
 * 分为普通话题TYPE_NORAML  和 推广TYPE_AD 2块
 */
public class TopicListAdapter extends BaseAdapter {

    //普通视图
    private static final int TYPE_NORMAL = 0;
    //广告/推广位置
    private static final int TYPE_AD = 1;

    private Context context;
    private ArrayList<TopicDetail> list = new ArrayList<TopicDetail>();
    private static Handler handler = null;
    public static final int CANCEL_COLLECT_TOPIC = 1;

    private boolean isUserself = false;
    private boolean theViewIsMyCollected = false;


    public TopicListAdapter(Context context, int titleResId) {
        this.context = context;
        if (R.string.collected_topic == titleResId) {
            handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    switch (msg.what) {
                        // 取消收藏时，需通知收藏列表删除
                        case CANCEL_COLLECT_TOPIC:
                            list.remove(msg.obj);
                            if (list.size() <= 0) {
                                EventBus.getDefault().post("取消收藏");
                            }
                            notifyDataSetChanged();
                            break;

                        default:
                            break;
                    }
                }
            };
        }
        if (titleResId == R.string.published_topic) {
            this.isUserself = true;
        }
        if (titleResId == R.string.collected_topic) {
            this.theViewIsMyCollected = true;
        }
    }

    public synchronized void setData(List<TopicDetail> list) {
        this.list.clear();
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public synchronized void addData(List<TopicDetail> list) {
        this.list.addAll(list);
        notifyDataSetChanged();
    }

    public ArrayList<TopicDetail> getList(){
        return list;
    }

    public synchronized void addReData(List<TopicDetail> list) {
        this.list.addAll(list);
    }

    public synchronized void clearList() {
        this.list.clear();
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list == null ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }


    @Override
    public int getItemViewType(int position) {
//        return position % 2 == 1 ? TYPE_NORMAL : TYPE_AD;
        return TYPE_NORMAL;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder1Normal holder = null;
        ViewHolderAd holderAd = null;
        ViewHolderMyTopic vh = null;
        int viewType = getItemViewType(position);
        if (convertView == null) {
            switch (viewType) {
                case TYPE_NORMAL:
                    if (isUserself) {
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_topic_item_mine, null, false);
                        vh = new ViewHolderMyTopic(convertView);
                        Log.i("zzz", "我的话题页面");
                        convertView.setTag(vh);
                    } else {
                        convertView = LayoutInflater.from(context).inflate(R.layout.layout_topic_item_with_group_name, null, false);
                        holder = new ViewHolder1Normal(convertView);
                        Log.i("zzz", "热门话题页面");
                        convertView.setTag(holder);
                    }

                    break;
                case TYPE_AD:
                    convertView = LayoutInflater.from(context).inflate(R.layout.adapter_hot_topic_ad, null, false);
                    holderAd = new ViewHolderAd(convertView);
                    convertView.setTag(holderAd);
                    break;
                default:
                    break;
            }


        } else {
            switch (viewType) {
                case TYPE_NORMAL:
                    if (isUserself) {
                        vh = (ViewHolderMyTopic) convertView.getTag();
                    } else {
                        holder = (ViewHolder1Normal) convertView.getTag();
                    }
                    break;
                case TYPE_AD:
                    holderAd = (ViewHolderAd) convertView.getTag();
                    break;
                default:
                    break;
            }
        }
        TopicDetail topicDetail = list.get(position);


        switch (viewType) {
            //话题item
            case TYPE_NORMAL:
                if (isUserself) {
                    setViewMyTopic(vh, topicDetail);
                } else {
                    setViewNormal(holder, topicDetail);
                }
                break;
            //推广item
            case TYPE_AD:
                setViewAd(convertView, holderAd, topicDetail);
                break;
            default:
                break;
        }


        return convertView;
    }

    /**
     * 设置话题item
     *
     * @param holder
     * @param topicDetail
     */
    private void setViewNormal(ViewHolder1Normal holder, TopicDetail topicDetail) {
        holder.topic.setData(topicDetail);
        holder.more.setData(topicDetail, MoreButton.TopicType.Default_Topic);
        holder.groupname.setText(topicDetail.group_title);
        final int group_id = topicDetail.group_id;
        holder.groupname.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityActionUtils.activity_Jump_Value(v.getContext(), GroupTopicActivity.class, "group_id", group_id);
            }
        });
        if (theViewIsMyCollected) {
            Log.i("zzz", "是我的收藏页面");
            holder.topic.setTheViewIsMyCollect();

        }
    }


    /**
     * 设置广告item
     *
     * @param holder
     * @param topicDetail
     */
    private void setViewAd(View rootView, ViewHolderAd holder, TopicDetail topicDetail) {
        //推广点击事件
        rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ActivityActionUtils.jumpToWebView(context,"H5网页","http://www.baidu.com");
            }
        });
    }

    /**
     * 设置我的话题item
     *
     * @param holder
     * @param topicDetail
     */
    private void setViewMyTopic(ViewHolderMyTopic holder, TopicDetail topicDetail) {
        holder.topic.setData(topicDetail);
        if ("verifying".equals(topicDetail.status)) {
            holder.more.setVisibility(View.GONE);
        } else {
            holder.more.setVisibility(View.VISIBLE);
        }
        holder.more.setData(topicDetail, MoreButton.TopicType.My_Topic);

        holder.groupname.setText(topicDetail.group_title);
        final int group_id = topicDetail.group_id;
        holder.groupname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityActionUtils.activity_Jump_Value(v.getContext(), GroupTopicActivity.class, "group_id", group_id);
            }
        });
    }

    /***
     * 热门话题
     */
    private static class ViewHolder1Normal {
        public final TextView groupname;
        public final TopicItemWidget topic;
        public final MoreButton more;

        public ViewHolder1Normal(View root) {
            groupname = (TextView) root.findViewById(R.id.group_name);
            topic = (TopicItemWidget) root.findViewById(R.id.topic);
            more = (MoreButton) root.findViewById(R.id.more);
        }
    }

    /***
     * 我的话题
     */
    private static class ViewHolderMyTopic {
        public final TextView groupname;
        public final TopicItemByMyPublish topic;
        public final MoreButton more;

        public ViewHolderMyTopic(View root) {
            groupname = (TextView) root.findViewById(R.id.group_name);
            topic = (TopicItemByMyPublish) root.findViewById(R.id.topic);
            more = (MoreButton) root.findViewById(R.id.more);
        }
    }

    /***
     * 广告话题
     */
    private static class ViewHolderAd {
        public final ImageView headimage;
        public final TextView name;
        public final TextView time;
        public final TextView tagpopularize;

        public ViewHolderAd(View root) {
            headimage = (ImageView) root.findViewById(R.id.head_image);
            name = (TextView) root.findViewById(R.id.name);
            time = (TextView) root.findViewById(R.id.time);
            tagpopularize = (TextView) root.findViewById(R.id.tag_popularize);
        }
    }
}