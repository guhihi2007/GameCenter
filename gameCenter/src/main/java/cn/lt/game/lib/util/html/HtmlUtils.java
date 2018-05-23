package cn.lt.game.lib.util.html;

import android.graphics.Color;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;

/**
 * Created by wenchao on 2015/8/25.
 */
public class HtmlUtils {

    /**
     * 针对Base64字符串解码 后支持html,网络
     * 移动网络加载小图，
     * wifi网络加载大图
     *
     * @param textView
     */
    public static void supportHtmlWithNet(final TextView textView, String text,boolean isBase64Text) {
        String htmlText;
        if(isBase64Text) {
            //解码
            htmlText = new String(Base64.decode(text, Base64.DEFAULT));
        }else{
            htmlText = text;
        }

        htmlText = convertImageBeforeBr(htmlText);

        textView.setTag(R.id.tagTv, htmlText);
        textView.setLinkTextColor(Color.parseColor("#006699"));//统一设置超链接颜色
        //支持超链接
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence charSequence = Html.fromHtml(htmlText, new RemoteImageGetter(textView),
                new HtmlTagHandler(textView.getContext()));
        textView.setText(new UrlClickStringBuilder(charSequence, new UrlClickStringBuilder.OnClickListener(){
            @Override
            public void onClick(String url) {
                ActivityActionUtils.jumpToByUrl(textView.getContext(),"",url);
            }
        }));
    }

    /***
     * 只针对话题评论的Html文本处理，去掉各种标签换行
     * @param textView
     * @param text
     * @param isBase64Text
     */
    public static void supportCommentHtmlWithNet(final TextView textView, String text,boolean isBase64Text) {
        String htmlText;
        if(isBase64Text) {
            //解码
            htmlText = new String(Base64.decode(text, Base64.DEFAULT));
            htmlText =  htmlText.replace("<br>","\n").replace("<p>","").replace("</p>","").replace("<p dir=\"ltr\">","");
            Log.i("zzz","评论转以后的内容"+htmlText);
        }else{
            htmlText = text;
        }

        htmlText = convertImageBeforeBr(htmlText);

        textView.setTag(R.id.tagTv, htmlText);
        textView.setLinkTextColor(Color.parseColor("#006699"));//统一设置超链接颜色
        //支持超链接
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        CharSequence charSequence = Html.fromHtml(htmlText, new RemoteImageGetter(textView),
                new HtmlTagHandler(textView.getContext()));
        textView.setText(new UrlClickStringBuilder(charSequence, new UrlClickStringBuilder.OnClickListener(){
            @Override
            public void onClick(String url) {
                ActivityActionUtils.jumpToByUrl(textView.getContext(),"",url);
            }
        }));
    }
    /**
     * 所有img标签前面增加br
     * @param html
     * @return
     */
    public static String convertImageBeforeBr(String html){
        Document document = Jsoup.parse(html);
        Elements images = document.select("img");
        for(int i =0;i<images.size();i++){
            Element image = images.get(i);
            image.before("<br>");
        }
        return document.outerHtml();
    }


    /**
     * 根据html解析出所有img中src的值,依次解析
     * @param html
     * @return
     */
    public static ArrayList<String> getImagePathList(String html) {
        Document document   = Jsoup.parse(html);
        Elements images     = document.select("img");
        ArrayList<String> imagePaths = new ArrayList<String>();
        for (int i = 0; i < images.size(); i++) {
            Element image = images.get(i);
            imagePaths.add(image.attr("src"));
        }
        return imagePaths;
    }

    /**
     * 转换imgUrl为html文本
     * @param imageUrl
     * @return
     */
    public static String convertToHtmlWidthImg(String imageUrl){
        return "<img src='"+imageUrl+"'/>";
    }

    /**
     * 吧html转换成纯文本
     * @param htmlText
     * @return
     */
    public static String convertHtmlToString(String htmlText){
        if(TextUtils.isEmpty(htmlText)){
            return "";
        }
        return Jsoup.parse(htmlText).text();
    }

}
