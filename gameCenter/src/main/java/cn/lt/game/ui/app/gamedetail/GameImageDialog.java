package cn.lt.game.ui.app.gamedetail;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;

import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.image.BitmapUtil;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.gamedetail.GameImageGallery.ScreenshotsSwitchedListener;

public class GameImageDialog extends Dialog implements ScreenshotsSwitchedListener {
    private GameImageGallery gallery;
    private Context context;
    public Bitmap[] pics;
    private ArrayList<Integer> finishposition;
    private ArrayList<String> urls;
    private boolean noPic;
    private int screenWidth, screenHeigth;
    private ScreenshotsSubView screenshotsSubView;

    public GameImageDialog(Context context, Bitmap[] list, int screenWidth, int screenHeigth) {
        super(context, R.style.screenshotDialogStyle);
        this.context = context;
        noPic = false;//((MyApplication)context.getApplicationContext()).getNoPic();
        pics = list;
        this.screenWidth = screenWidth;
        this.screenHeigth = screenHeigth;

    }

    public void notifyDataSetChanged() {
        gallery.notifyDataSetChanged();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_gameimage);
        initGallery();
        initScreenshotsSubView();
        setScreenshotsSwitchedListener();
    }


    private void initGallery() {
        gallery = (GameImageGallery) findViewById(R.id.gameImage_gallery);
        gallery.setScreenSize(screenWidth, screenHeigth);
        gallery.init(context);
        gallery.setData(pics);
        gallery.setFinishPositions(finishposition);
        gallery.setCurrentPosition(0);
        gallery.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, final int arg2, long arg3) {
                // TODO Auto-generated method stub
                gallery.setCurrentItem(arg2);
                gallery.invalidate();
                if (finishposition.indexOf(arg2) < 0) {
                    if (arg1 == null) return;
                    final ImageView iv = (ImageView) arg1.findViewById(R.id.item_imagegallery_Iv);
                    //异步加载
                    ImageloaderUtil.loadImageCallBack(getContext(), urls.get(arg2), new SimpleTarget<GlideDrawable>() {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                            List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());
                            if (resource != null) {
                                if (resource.getIntrinsicWidth() > resource.getIntrinsicHeight()) {
                                    iv.setImageBitmap(BitmapUtil.getBitmap(BitmapUtil.drawable2Bitmap(resource)));
                                }
                                iv.setAlpha(0xff);
                                boolean firstDisplay = !displayedImages.contains(urls.get(arg2));
                                if (firstDisplay) {
//                                    FadeInBitmapDisplayer.animate(iv, 500); // 设置image隐藏动画500ms
                                    displayedImages.add(urls.get(arg2)); // 将图片uri添加到集合中
                                }
                                finishposition.add(urls.indexOf(urls.get(arg2)));
                                pics[urls.indexOf(urls.get(arg2))] = BitmapUtil.drawable2Bitmap(resource);
                            }
                        }
                    });
                }
//				}


            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        gallery.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameImageDialog.this.cancel();
            }
        });
    }

    private void initScreenshotsSubView() {
        screenshotsSubView = (ScreenshotsSubView) findViewById(R.id.gameImage_screenshotsSubView);
        screenshotsSubView.setImageCount(pics.length);
    }

    private void setScreenshotsSwitchedListener() {
        gallery.setScreenshotsSwitchedListener(this);
    }

    @Override
    public void onScreenshotsSwitched(int position) {
        screenshotsSubView.showCurrentScreenshotsPosition(position);
    }

    public void setCurrentPosition(int curr) {
        gallery.setCurrentPosition(curr);
    }

    public void setFinishposition(ArrayList<Integer> finishposition) {
        this.finishposition = finishposition;
    }

    public void setUrls(ArrayList<String> urls) {
        this.urls = urls;
    }

    public void recycleBitmaps(){
        for (int i = 0; i < pics.length; i++) {
            Bitmap bitmap = pics[i];
            if(bitmap != null) {
                bitmap.recycle();
                bitmap = null;
            }
        }
    }
}
