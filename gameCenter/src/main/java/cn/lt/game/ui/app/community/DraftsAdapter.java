package cn.lt.game.ui.app.community;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.ui.app.community.model.DraftBean;

public class DraftsAdapter extends BaseAdapter {
    private List<DraftBean> al = new ArrayList<DraftBean>();
    private Context        context;
    private LayoutInflater inflater;
    private final int TYPE_1 = 0; // 话题
    private final int TYPE_2 = 1; // 评论或者回复

    @Override
    public int getCount() {
        return al == null ? 0 : al.size();
    }

    public DraftsAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    public void setData(List<DraftBean> al) {
        if (this.al.size() > 0) {
            this.al.clear();
        }
        this.al.addAll(al);
        notifyDataSetChanged();
    }

    public void remove(DraftBean db) {
        al.remove(db);
        notifyDataSetChanged();
    }

    public void removeByTag(String tag) {
        for (int i = 0; i < al.size(); i++) {
            if (al.get(i).getTag().equals(tag)) {
                al.remove(al.get(i));
                notifyDataSetChanged();
                return;
            }
        }

    }

    public void addData(List<DraftBean> al) {
        this.al.addAll(al);
        notifyDataSetChanged();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public int getItemViewType(int position) {
        DraftBean db = al.get(position);
        if (db.getType() == 0) {
            return TYPE_1;
        } else {
            return TYPE_2;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        switch (getItemViewType(position)) {
            case TYPE_1:
//			if (convertView == null) {
                convertView = new DraftsItemViewTopic(context, inflater);
//			}
                ((DraftsItemViewTopic) convertView).setData(al.get(position));
                break;
            case TYPE_2:
//			if (convertView == null) {
                convertView = new DraftsItemViewReplyOrComment(context,
                        inflater);
//			}
                ((DraftsItemViewReplyOrComment) convertView).setData(al
                        .get(position));
                break;

            default:
                break;
        }

        return convertView;
    }
}
