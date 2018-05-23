package cn.lt.game.ui.app.gamedetail;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.lib.util.LogUtils;

public class GameImageGallery extends LinearLayout {

    Context context;
    ArrayList<Bitmap> arrayList = new ArrayList<Bitmap>();
    private int screenWidth, screenHeigth;
    private PictureFlow gallery;
    private GalleryAdapter adapter;
    private Gallery.LayoutParams lpItem;
    private int currentItem = 0;
    private int itemWidth = 0;//(int)(MAPPHONE.width);
    private int itemHeight = 0;//(int)(MAPPHONE.width/1.415) ;
    private ArrayList<Integer> finishPositions;
    private boolean noPic;
    private LayoutInflater mInflater;


    public GameImageGallery(Context context) {
        super(context);
        this.context = context;
        mInflater = LayoutInflater.from(context);
        noPic = false;// ((MyApplication)context.getApplicationContext()).getNoPic();

    }

    public GameImageGallery(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }


    public void setCurrentItem(int i) {
        currentItem = i;
    }

    public void setCurrentPosition(int pos) {
        if (gallery.getAdapter().getCount() > pos && gallery.getAdapter().getCount() > 2) {
            gallery.setSelection(pos);
        } else {
            gallery.setSelection(0);
        }
    }

    public void setScreenSize(int screenWidth, int screenHeigth) {
        this.screenWidth = screenWidth;
        this.screenHeigth = screenHeigth;
    }

    public void setItemSize(int w, int h) {
        itemWidth = w;
        itemHeight = h;
    }

    public void setOnItemSelectedListener(AdapterView.OnItemSelectedListener listener) {
        gallery.setOnItemSelectedListener(listener);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        gallery.setOnItemClickListener(listener);
    }

    public void init(Context context) {
        setWillNotDraw(false);
        this.context = context;
        gallery = new PictureFlow(context);
        gallery.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        gallery.setHorizontalFadingEdgeEnabled(false);

        LinearLayout.LayoutParams lpGallery = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lpGallery.setMargins(0, 0, 0, 0);
        gallery.setMaxRotationAngle(0);

        gallery.setLayoutParams(lpGallery);
        setItemSize(screenWidth, screenHeigth);

        adapter = new GalleryAdapter(context, null);
        gallery.setAdapter(adapter);
        gallery.setBackgroundColor(Color.TRANSPARENT);
        addView(gallery);

        radius = (int) (screenWidth * 0.008);
        padding = (int) (screenWidth * 0.018);
        rw = (int) (screenWidth * 0.02);
    }


    public void notifyDataSetChanged() {
        adapter.notifyDataSetChanged();
    }


    Paint paint = new Paint();
    int spacing = 0;
    int radius = 30;
    int rw = 30, padding = 18;
    Rect rect = new Rect();
    private ScreenshotsSwitchedListener gameScreenshotsSwitchedListener;


    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        LogUtils.i("shengce", "哇哈哈哈哈哈");
        int count = getScreenshotsCount();

        if (count < 1) {
            return;
        }

        gameScreenshotsSwitchedListener.onScreenshotsSwitched(currentItem);
    }

    private int getScreenshotsCount() {
        return adapter.getCount();
    }

    public void setData(Bitmap[] pics) {
        adapter.setData(pics);
        adapter.notifyDataSetChanged();
    }

    public void setFinishPositions(ArrayList<Integer> finishPositions) {
        this.finishPositions = finishPositions;
    }

    public void setScreenshotsSwitchedListener(ScreenshotsSwitchedListener listener) {
        this.gameScreenshotsSwitchedListener = listener;
    }

    public class GalleryAdapter extends BaseAdapter {
        private Context context;
        private Bitmap[] pics;

        public GalleryAdapter(Context context) {
            init(context);
        }

        public GalleryAdapter(Context context, Bitmap[] pics) {

            init(context);
            setData(pics);

        }

        private void init(Context context) {
            this.context = context;
            lpItem = new Gallery.LayoutParams(itemWidth, itemHeight);
        }


        public void setData(Bitmap[] pics) {
            if (pics == null) {
                pics = new Bitmap[1];
            }
            this.pics = pics;
        }

        @Override
        public int getCount() {
            return pics.length;
        }

        public Bitmap getItem(int position) {
            Bitmap item = pics[position];
            return item;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("NewApi")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.item_imagegallery,
                        null);
                holder.iv = (ImageView) convertView
                        .findViewById(R.id.item_imagegallery_Iv);
                holder.pb = (ProgressBar) convertView
                        .findViewById(R.id.item_imagegallery_Pb);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.iv.getLayoutParams().width = itemWidth;
            holder.iv.getLayoutParams().height = itemHeight;

            holder.iv.setScaleType(ScaleType.FIT_CENTER);
            if (noPic) {

                if (finishPositions.indexOf(position) >= 0) {
                    holder.iv.setAlpha(0xff);
                } else {
                    holder.iv.setAlpha(0x00);
                }

                Bitmap item = pics[position];
                if (item == null) {
                    holder.iv.setAlpha(0x00);
                } else {
                    if (item.getWidth() > item.getHeight()) {
                        holder.iv.setImageBitmap(getBitmap(item));
                    } else {
                        holder.iv.setImageBitmap(item);
                    }
                }

            } else {

                Bitmap item = pics[position];
                if (item == null) {
                } else {
                    holder.iv.setAlpha(0xff);
                    if (item.getWidth() > item.getHeight()) {
                        holder.iv.setImageBitmap(getBitmap(item));
                    } else {
                        holder.iv.setImageBitmap(item);
                    }
                }
            }

            return convertView;
        }

        Bitmap getBitmap(Bitmap bitmap) {
            int w = bitmap.getWidth();
            int h = bitmap.getHeight();
            Matrix matrix = new Matrix();
            // 保证图片不变形.
            matrix.postScale(1, 1);
            matrix.postRotate(90, w / 2, h / 2);//以坐标50，100 旋转30°
            // w,h是原图的属性.
            return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
        }
    }

    public final class ViewHolder {
        public ImageView iv;
        public ProgressBar pb;
    }

    public interface ScreenshotsSwitchedListener {
        void onScreenshotsSwitched(int position);
    }

}
