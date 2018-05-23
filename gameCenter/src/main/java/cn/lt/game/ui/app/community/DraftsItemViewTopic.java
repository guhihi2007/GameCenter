package cn.lt.game.ui.app.community;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.html.HtmlUtils;
import cn.lt.game.ui.app.ImageViewPagerActivity;
import cn.lt.game.ui.app.ImageViewPagerActivity.ImageUrl;
import cn.lt.game.ui.app.community.model.DraftBean;
import cn.lt.game.ui.app.community.model.Photo;
import cn.lt.game.ui.app.community.widget.DraftMenu;

//草稿箱话题Item
public class DraftsItemViewTopic extends LinearLayout implements
        OnClickListener {
    private Context        context;
    private LayoutInflater inflater;
    private TextView       type, time, title, content;//item内部的控件
    private DraftMenu menu;

    public DraftsItemViewTopic(Context context, LayoutInflater inflater) {
        super(context);
        this.context = context;
        this.inflater = inflater;
        initView();
    }

    private void initView() {
        inflater.inflate(R.layout.drafts_topic_item, this);
        type = (TextView) findViewById(R.id.type);
        time = (TextView) findViewById(R.id.time);
        title = (TextView) findViewById(R.id.title);
        content = (TextView) findViewById(R.id.content);
        menu = (DraftMenu) findViewById(R.id.menu);
    }

    public void setData(DraftBean db) {
        type.setText("话题");
        time.setText(db.getTag());
        title.setText(getTitle(db.getTopic_title()));
        if (db.getLocal_topicContent().equals("-1")) {
//            HtmlUtils.supportHtml(content, db.getTopic_content());
            content.setText(getContent(db.getTopic_content()));
        } else {
            String contentStr = LocalHelpTools.instance(context).getStringContent(
                    db.getLocal_topicContent());
//            HtmlUtils.supportHtml(content, contentStr);
            content.setText(getContent(contentStr));
        }
        if (db.getLocal_topicPaths().equals("-1")) {
        } else {
            db.setTopic_paths(LocalHelpTools.instance(context).getPaths(
                    db.getLocal_topicPaths()));
        }
        content.setTag(db);
        content.setOnClickListener(this);
        menu.setDb(db);
    }

    private String getContent(String html){
        String content = HtmlUtils.convertHtmlToString(html);
        if(content.trim().isEmpty()){
            ArrayList<String> imageList = HtmlUtils.getImagePathList(html);
            if(imageList.size()>0){
                content = "[图片]";
            }else{
                content="[空]";
            }
        }
        return content;
    }

    private String getTitle(String title){
        if(title.trim().isEmpty()){
            title = "[空]";
        }
        return title;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.content) {
            DraftBean db = (DraftBean) v.getTag();
            Intent in = new Intent(context, SendTopicActivity.class);
            Bundle b = new Bundle();
            b.putString("type", "2");// 草稿箱编辑页面启动的 所以传2
            b.putSerializable("draftBean", db);
            in.putExtras(b);
            context.startActivity(in);

        } else {
            MyImageView im = (MyImageView) v;
            ArrayList<String> al = im.getPaths();
            ActivityActionUtils.jumpToImagEye((Activity) context,
                    changeToImageUrl(al), im.getPosition());
        }
    }

    private ImageViewPagerActivity.ImageUrl changeToImageUrl(
            ArrayList<String> al) {
        List<Photo> li = new ArrayList<Photo>();
        for (int i = 0; i < al.size(); i++) {
            Photo p = new Photo();
            p.original = "File:/" + al.get(i);
            li.add(p);
        }
        ImageViewPagerActivity.ImageUrl url = new ImageUrl(li);
        return url;
    }
}
