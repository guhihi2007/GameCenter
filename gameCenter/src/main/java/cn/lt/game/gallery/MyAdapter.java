package cn.lt.game.gallery;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.ToastUtils;

public class MyAdapter extends CommonAdapter<String> {

    /**
     * 用户选择的图片，存储为图片的完整路径
     */
    public static ArrayList<String> mSelectedImage = new ArrayList<String>();
    private TextView num;
    private int confineNum;// 总共限制张数
    private Context cn;
    /**
     * 文件夹路径
     */
    private String mDirPath;

    public MyAdapter(Context context, List<String> mDatas, int itemLayoutId,
                     String dirPath, TextView num, int confineNum) {
        super(context, mDatas, itemLayoutId);
        this.num = num;
        this.mDirPath = dirPath;
        this.confineNum = confineNum;
        this.cn = context;
    }

    @Override
    public void convert(final cn.lt.game.gallery.ViewHolder helper,
                        final String item) {
        // 设置no_pic
        helper.setImageResource(R.id.gallery_item_image,
                R.mipmap.gallery_pictures_no);
        // 设置no_selected
        helper.setImageResource(R.id.gallery_item_select,
                R.mipmap.gallery_picture_unselected);
        // 设置图片
        helper.setImageByUrl(R.id.gallery_item_image, mDirPath + "/" + item);

        final ImageView mImageView = helper.getView(R.id.gallery_item_image);
        final ImageView mSelect = helper.getView(R.id.gallery_item_select);
        mImageView.setColorFilter(null);
        // 设置ImageView的点击事件
        mImageView.setOnClickListener(new OnClickListener() {
            // 选择，则将图片变暗，反之则反之
            @Override
            public void onClick(View v) {
                if (mSelectedImage.contains(mDirPath + "/" + item)) {
                    mSelectedImage.remove(mDirPath + "/" + item);
                    mSelect.setImageResource(R.mipmap.gallery_picture_unselected);
                    mImageView.setColorFilter(null);
                    num.setText(String.format(mContext.getResources().getString(R.string.finish_select_image), mSelectedImage.size(), confineNum));
                } else
                // 未选择该图片
                {
                    if ((mSelectedImage.size()) >= confineNum) {
                        ToastUtils.showToast(cn, String.format(mContext.getString(R.string.max_image_selected_tips), confineNum));
                    } else {
                        mSelectedImage.add(mDirPath + "/" + item);
                        mSelect.setImageResource(R.mipmap.gallery_pictures_selected);
                        mImageView.setColorFilter(Color
                                .parseColor("#77000000"));
                        num.setText(String.format(mContext.getResources().getString(R.string.finish_select_image), mSelectedImage.size(), confineNum));

                    }

                }

            }
        });
        /**
         * 已经选择过的图片，显示出选择过的效果
         */
        if (mSelectedImage.contains(mDirPath + "/" + item)) {
            mSelect.setImageResource(R.mipmap.gallery_pictures_selected);
            mImageView.setColorFilter(Color.parseColor("#77000000"));
        } else {
            mSelect.setImageResource(R.mipmap.gallery_picture_unselected);
            mImageView.setColorFilter(null);
        }
    }
}
