package cn.lt.game.lib.view.banner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.image.ImageloaderUtil;

@SuppressWarnings("deprecation")
public class ImgAdapter extends BaseAdapter {

    private Context _context;
    private List<String> imgList;

    public ImgAdapter(Context context, List<String> imgList) {
        _context = context;
        setList(imgList);
    }

    public void setList(List<String> imgList) {
        if (imgList == null) {
            this.imgList = new ArrayList<>();
        } else {
            this.imgList = imgList;
        }
        this.notifyDataSetChanged();
    }



    public int getCount() {
        if (imgList == null || imgList.size() <= 0) {
            return 0;
        } else if (imgList == null || imgList.size() == 1) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    public Object getItem(int position) {

        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(_context).inflate(R.layout.item_banner_view, null);
            viewHolder = new ViewHolder();
//            ImageView imageView = new ImageView(_context);
//            imageView.setScaleType(ScaleType.FIT_XY);
//            imageView.setLayoutParams(new Gallery.LayoutParams(
//                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            viewHolder.imageView = (ImageView) convertView.findViewById(R.id.iv_bannerView);
            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        String banner = imgList.get(position % imgList.size());
//        ImageLoader.getInstance().display(banner, viewHolder.imageView);
        ImageloaderUtil.loadBannerImage(_context,banner, viewHolder.imageView);
        return convertView;
    }

    private class ViewHolder {
        ImageView imageView;
    }
}
