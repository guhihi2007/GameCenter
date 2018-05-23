package cn.lt.game.ui.app.community.topic.detail.reply;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/**
 * Created by zhengweijian on 15/8/31.
 */
public class ReplyAdapter extends BaseAdapter {
    public enum ReplyItemType{

        HEADER_ITEM_TYPE(1),REPLY_ITEM_TYPE(2),FOOT_ITEM_TYPE(3),COUNT_ITEM_TYPE(4);
        int type;

        ReplyItemType(int type){
            this.type = type;
        }
    }
    private Context context;
    private ArrayList<IReplyView> list = new ArrayList<IReplyView>();
    private int user_id;

   public  ReplyAdapter(Context context,ArrayList<IReplyView> list) {
       this.context = context;
       this.list =list;
      user_id = UserInfoManager.instance().getUserInfo().getId();
   }

    @Override
    public boolean isEnabled(int position) {

        return list.get(position).isClickable(user_id); //判断是不是自己回复自己

//        return  true;
    }

    @Override
    public int getCount()
    {

        return list == null ? 0: list.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getType();
    }

    @Override
    public int getViewTypeCount() {
        return ReplyItemType.COUNT_ITEM_TYPE.type;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return list.get(i).getView(context,view,i);
    }
}
