package cn.lt.game.lib.util.html;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.View;

import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.ui.app.ImageViewPagerActivity;
import cn.lt.game.ui.app.community.model.Photo;

/**
 * Created by wenchao on 2015/8/25.
 */
public class HtmlTagHandler implements Html.TagHandler {

    private Context context;
    private List<String> imageUrls;

    public HtmlTagHandler(Context context) {
        this.context = context;
        imageUrls = new ArrayList<String>();
    }


    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.toLowerCase().equals("img")) {
            int len = output.length();
            ImageSpan[] imageSpans = output.getSpans(len - 1, len, ImageSpan.class);
            String imgUrl = imageSpans[0].getSource();
            if(TextUtils.isEmpty(imgUrl)){
                //不存在Url
                return;
            }
            if(!imgUrl.contains("http://")) {
                //本地路劲需要加前缀
                imgUrl = "file://" + imgUrl;
            }
            output.setSpan(new ImageClick(context, imgUrl), len - 1, len, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            imageUrls.add(imgUrl);
        }
    }

    private class ImageClick extends ClickableSpan {
        private String  url;
        private Context context;

        public ImageClick(Context context, String url) {
            this.context = context;
            this.url = url;
        }

        @Override
        public void onClick(View widget) {
            if(imageUrls.contains(url)){
                int pos = imageUrls.indexOf(url);
                ActivityActionUtils.jumpToImagEye((Activity) context,getImageUrl(imageUrls),pos);
            }

        }

        private ImageViewPagerActivity.ImageUrl getImageUrl(
                List<String> imageUrls) {
            List<Photo> li = new ArrayList<Photo>();
            for(String url : imageUrls) {
                Photo p = new Photo();
                p.original = url;
                li.add(p);
            }
            ImageViewPagerActivity.ImageUrl imageUrl = new ImageViewPagerActivity.ImageUrl(li);
            return imageUrl;
        }
    }
}
