package cn.lt.game.lib.util.html;

import android.text.SpannableStringBuilder;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

/**
 * Created by wenchao on 2015/8/28.
 */
public class UrlClickStringBuilder extends SpannableStringBuilder{

    private CharSequence charSequence;
    private OnClickListener onClickListener;


    public UrlClickStringBuilder(CharSequence charSequence,OnClickListener onClickListener){
        super(charSequence);
        this.charSequence = charSequence;
        this.onClickListener = onClickListener;
        build();
    }

    private void build(){
        URLSpan[] urlSpans = this.getSpans(0, charSequence.length(), URLSpan.class);
        for(URLSpan span:urlSpans){
            makeLinkClickable(span);
        }
    }


    private void makeLinkClickable(final URLSpan span){
        int start = getSpanStart(span);
        int end = getSpanEnd(span);
        int flags =getSpanFlags(span);


        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                if(onClickListener!=null){
                    onClickListener.onClick(span.getURL());
                }
            }
        };
        setSpan(clickableSpan, start, end, flags);
        removeSpan(span);
    }

    public interface OnClickListener{
        void onClick(String url);
    }
}
