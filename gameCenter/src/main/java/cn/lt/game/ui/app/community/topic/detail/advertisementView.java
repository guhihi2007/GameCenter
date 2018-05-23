package cn.lt.game.ui.app.community.topic.detail;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.ui.app.community.model.Comment;

/**
 * Created by zhengweijian on 15/8/30.
 */
public class advertisementView extends RelativeLayout  {
    private ImageView headimage;
    private TextView  name;
    private TextView time;
    private ImageView bigimage;
    private TextView content;

    public advertisementView(Context context) {
        this(context, null);
    }

    public advertisementView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public advertisementView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        LayoutInflater.from(context).inflate(R.layout.view_adv, this);
        this.setBackgroundResource(R.drawable.left_right_selector);
        int padding = (int) context.getResources().getDimension(
                R.dimen.inInterval);
        setPadding(padding, padding, padding, 0);

        setFocusable(false);
        setFocusableInTouchMode(false);
        setDescendantFocusability(FOCUS_BLOCK_DESCENDANTS);
        initView();
    }

    private void initView() {
        headimage = (ImageView)findViewById(R.id.adv_icon);
        name = (TextView)findViewById(R.id.adv_name);
        time = (TextView) findViewById(R.id.adv_time);
        bigimage = (ImageView)findViewById(R.id.adv_img);
        content = (TextView) findViewById(R.id.adv_content);


    }

    public void setComment(Comment comment){

        name.setText(comment.author_nickname);
        content.setText(comment.content);
        time.setText(comment.published_at);
        headimage.setImageDrawable(getResources().getDrawable(R.mipmap.ic_launcher));
        ImageloaderUtil.loadImage(getContext(),comment.image, bigimage, false);

    }


}
