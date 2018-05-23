package cn.lt.game.ui.app.community;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.lt.game.R;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.community.model.PrivateMessage;

/**
 * Created by wenchao on 2015/11/26.
 */
public class MyPrivateMessageAdapter extends BaseAdapter {
    private Context context;
    private List<PrivateMessage> mList;

    public MyPrivateMessageAdapter(Context context) {
        this.context = context;
        this.mList = new ArrayList<>();
    }

    public void setList(List<PrivateMessage> list) {
        mList = list;
        this.notifyDataSetChanged();
    }

    public void appendToList(List<PrivateMessage> list) {
        mList.addAll(list);
        this.notifyDataSetChanged();
    }

    public List<PrivateMessage> getList() {
        return mList;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder h;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.item_my_private_msg, null);
            h = new ViewHolder();
            h.content = (TextView) convertView.findViewById(R.id.content);
            h.name = (TextView) convertView.findViewById(R.id.name);
            h.time = (TextView) convertView.findViewById(R.id.time);
            h.icon = (ImageView) convertView.findViewById(R.id.user_icon);
            h.level = (ImageView) convertView.findViewById(R.id.level);
            h.redPoint = convertView.findViewById(R.id.red_point);
            convertView.setTag(h);
        } else {
            h = (ViewHolder) convertView.getTag();
        }

        PrivateMessage item = mList.get(position);

        //头像
        ImageloaderUtil.loadUserHead(context, item.user_icon, h.icon);
//        名称
        if (TextUtils.isEmpty(item.user_nickname)) {
            item.user_nickname = "匿名";
        }
        h.name.setText(item.user_nickname);
//       等级
        h.level.setImageLevel(item.user_level);
//        最后发表内容
        if (TextUtils.isEmpty(item.last_statement)) {
            item.last_statement = "";
        }
        h.content.setText(item.last_statement);
//        最后发表时间
        if (TextUtils.isEmpty(item.published_at)) {
            item.user_nickname = "";
        }
        //时间改为MM-dd HH:mm
        Date date = TimeUtils.getDateToStringHaveHour(item.published_at);
        SimpleDateFormat formatter = new SimpleDateFormat("MM-dd HH:mm", Locale.US);
        String time = formatter.format(date);
        h.time.setText(time);
//  红点
        h.redPoint.setVisibility(item.is_read ? View.GONE : View.VISIBLE);

        return convertView;
    }


    public void deleteMsg(int position) {
        mList.remove(position);
        notifyDataSetChanged();
    }


    private static class ViewHolder {
        TextView name;
        TextView time;
        ImageView icon;
        ImageView level;
        TextView content;
        View redPoint;
    }
}
