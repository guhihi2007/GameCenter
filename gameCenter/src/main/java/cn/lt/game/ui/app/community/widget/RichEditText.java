package cn.lt.game.ui.app.community.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.widget.EditText;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.lt.game.lib.FileUtils;
import cn.lt.game.lib.ImageUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.lib.util.image.BitmapUtil;
import cn.lt.game.lib.util.text.StringEscapeUtils;
import cn.lt.game.lib.util.threadpool.LTAsyncTask;

/**
 * 支持插入图片的EditText
 * Created by wenchao on 2015/11/4.
 */
public class RichEditText extends EditText {

    private OnImageListener onImageListener;

    public RichEditText(Context context) {
        super(context);
    }

    public RichEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void insertImages(final ArrayList<String> imagePathList) {

        new LTAsyncTask<Void, String, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                if (onImageListener != null)
                    onImageListener.startInsertImage();
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (onImageListener != null)
                    onImageListener.endInsertImage();
            }

            @Override
            protected Void doInBackground(Void... params) {
                for (int i = 0; i < imagePathList.size(); i++) {
                    String savePath = compressImage(imagePathList.get(i));
                    publishProgress(savePath);
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(String... values) {
                super.onProgressUpdate(values);
                Spanned imageHtml = Html.fromHtml("<img src='" + values[0] + "'/>", new ImageGetter(), null);
                insert(imageHtml);
            }

        }.execute();
    }


    public void setOnImageListener(OnImageListener onImageListener) {
        this.onImageListener = onImageListener;
    }

    /**
     * 根据光标所在位置插入字符串
     *
     * @param text
     */
    public void insert(CharSequence text) {
        int      start    = getSelectionStart();
        Editable editable = getText();
        editable.insert(start, text);
        setText(editable);
        setSelection(start + text.length());

    }

    /**
     * 获取html、
     *
     * @return
     */
    public String getHtml() {
        if(getText().toString().trim().isEmpty()){
            return "";
        }
        String unicodeHtml = Html.toHtml(this.getText());
        if(unicodeHtml.length()>0) {
            //删除最后一个回车
            if(unicodeHtml.charAt(unicodeHtml.length() - 1) == '\n'){
               unicodeHtml =unicodeHtml.substring(0,unicodeHtml.length()-1);
            }
        }
        return StringEscapeUtils.unescapeHtml4(unicodeHtml);
    }




    /**
     * 设置html
     *
     * @param html
     */
    public void setHtml(String html) {
        setText(Html.fromHtml(html, new ImageGetter(), null));
    }

    public ArrayList<String> getImagePathList() {
        return HtmlUtils.getImagePathList(getHtml());
    }

    private String compressImage(String imagePath) {
        int[] widthAndHeight = BitmapUtil.calculateBitmapWidthAndHeight(imagePath);
        int   bitmapWidth    = widthAndHeight[0];
        int   bitmapHeight   = widthAndHeight[1];
        int   screentWidth   = Utils.getScreenWidth(getContext());
        int   screenHeight   = Utils.getScreenHeight(getContext());

        String saveFilePath = imagePath;
        if (bitmapWidth > screentWidth || bitmapHeight > screenHeight) {
            //压缩图片
            try {
                Bitmap bitmap = BitmapUtil.decodeSampledBitmapFromFile(imagePath, 500, 500);
                //处理图片角度的问题
                int degress = BitmapUtil.getDegress(imagePath);
                if(degress!=0) {
                    bitmap = BitmapUtil.rotateBitmap(bitmap, BitmapUtil.getDegress(imagePath));
                }
                //============
                String fileName = FileUtils.getFileName(imagePath);
                saveFilePath = getContext().getExternalCacheDir().getAbsolutePath() + File.separator + "upload_images" + File.separator + fileName;
                BitmapUtil.saveBitmapToFile(bitmap, saveFilePath, Bitmap.CompressFormat.JPEG);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return saveFilePath;
    }

    public interface OnImageListener {
        void startInsertImage();

        void endInsertImage();


    }


    private class ImageGetter implements Html.ImageGetter {

        @Override
        public Drawable getDrawable(String source) {
            int screenWidth = Utils.getScreenWidth(getContext());

            Bitmap bitmap  = BitmapUtil.decodeSampledBitmapFromFile(source, screenWidth / 5, screenWidth / 5);

            Drawable d = ImageUtils.bitmapToDrawable(bitmap);
            d.setBounds(0, 0, bitmap.getWidth(), bitmap.getHeight());
            return d;
        }
    }


}
