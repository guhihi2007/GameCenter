package cn.lt.game.ui.app.personalcenter.login;

import android.content.Context;
import android.os.Handler.Callback;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class UserNameAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater mInflater;
    private List<UserBaseInfo> filterDataList;
    private List<UserBaseInfo> dataList;
    private ArrayFilter mFilter;
    private Context context;
    private OnItemClickListener listener;
    //只是为了处理数据被完全删除时的情况.
    private Callback callback;

    public UserNameAdapter(Context context, List<UserBaseInfo> dataList) {
        mInflater = LayoutInflater.from(context);
        this.filterDataList = dataList;
        this.dataList = dataList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return filterDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return filterDataList.get(position).getUserName();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    final class ViewHolder {
        TextView userName;
        TextView userDel;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.login_history_item, parent,false);
            holder.userName = (TextView) convertView.findViewById(R.id.user_name);
            holder.userDel = (TextView) convertView.findViewById(R.id.user_del);
            convertView.setTag(holder);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    if (listener != null) {
                        listener.onItemClick((AdapterView<UserNameAdapter>) parent, arg0, position, 0);
                    }
                }
            });
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (filterDataList.size() > position) {
            holder.userName.setText(filterDataList.get(position).getUserName());
            holder.userDel.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View arg0) {
                    UserBaseInfo user = filterDataList.remove(position);
                    UserInfoManager.instance().delHistoryUserInfo(user);
                    dataList.remove(user);
                    notifyDataSetChanged();
                    callback.handleMessage(null);
                }
            });
        }
        return convertView;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }

    private class ArrayFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (prefix == null || prefix.length() == 0) {
                results.values = dataList;
                results.count = dataList.size();
            } else {
                String prefixString = prefix.toString().toLowerCase();

                int count = dataList.size();

                List<UserBaseInfo> newValues = new ArrayList<UserBaseInfo>(
                        count);

                for (int i = 0; i < count; i++) {
                    UserBaseInfo user = dataList.get(i);
                    if (user != null) {
                        if (user.getUserName() != null
                                && user.getUserName().startsWith(prefixString)) {
                            newValues.add(user);
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {
            filterDataList = (List<UserBaseInfo>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }

    public void setListener(OnItemClickListener listener, Callback callback) {
        this.listener = listener;
        this.callback = callback;
    }

}
