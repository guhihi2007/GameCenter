package cn.lt.game.ui.app.community;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.db.service.DraftService;
import cn.lt.game.gallery.GalleryActivity;
import cn.lt.game.gallery.MyAdapter;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.TimeUtils;
import cn.lt.game.lib.util.FromPageManager;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsDataProductorImpl;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.ui.app.community.model.Category;
import cn.lt.game.ui.app.community.model.DraftBean;
import de.greenrobot.event.EventBus;

//发表话题Activity
public class SendTopicActivity extends BaseFragmentActivity implements OnPageChangeListener, OnClickListener {
    private TextView title;
    private ImageButton back;
    private Button send;
    private ViewPager viewPager;
    private SendTopicAdatper adapter;
    private Uri uri;
    private boolean compileType = false;// 是否是编辑状态
    private DraftBean draftBean;// 如果是编辑状态可获得到这个BEAN
    private ArrayList<Category> categoryList;
    private int groupId;

    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_sendtopic);
        Bundle bundle = getIntent().getExtras();
        if (bundle.get("type").equals("1")) { // 正常启动
            groupId = bundle.getInt("group_id");
            categoryList = (ArrayList<Category>) bundle.getSerializable("category");
            initView();
            initListener();
        } else {
            draftBean = (DraftBean) getIntent().getExtras().get("draftBean");
            categoryList = draftBean.getCategoryList();
            groupId = draftBean.getGroup_id();
            compileType = true;
            initView();
            initListener();
            if (draftBean != null) {
                initData(draftBean);
            }
        }
        StatisticsEventData event = StatisticsDataProductorImpl.produceStatisticsData(null, 0, 0, null, Constant.PAGE_TOPIC_PUBLISH, ReportEvent.ACTION_PAGEJUMP, null, null, null, "");
//        DCStat.pageJumpEvent(event);
        FromPageManager.pageJumpReport(event);
    }

    private void initData(DraftBean draftBean) {
        TopicTitleFragment tc = (TopicTitleFragment) adapter.getItem(0);
        if (tc != null) {
            tc.setData(draftBean.getTopic_title());
        }
        TopicContentFragment tt = (TopicContentFragment) adapter.getItem(1);
        if (tt != null) {
            tt.setData(draftBean.getTopic_content());
        }
        TopicSortFragment ts = (TopicSortFragment) adapter.getItem(2);
        if (ts != null) {
            ts.setData(draftBean.getCategory_id());
        }

    }

    private void initListener() {
        back.setOnClickListener(this);
        send.setOnClickListener(this);
    }

    private void initView() {
        back = (ImageButton) findViewById(R.id.back);
        send = (Button) findViewById(R.id.send);
        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getText(R.string.send_topic));
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new SendTopicAdatper(getSupportFragmentManager(), this, categoryList);
        viewPager.setAdapter(adapter);
        SlidingTabLayout indicator = (SlidingTabLayout) findViewById(R.id.tab);
        indicator.setViewPager(viewPager);
        viewPager.addOnPageChangeListener(this);
    }

    public void setCurrentItem(int index) {
        viewPager.setCurrentItem(index);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back:
                //发表主题处理
                if (!compileType && hasContent()) {
                    showSaveTips();
                } else if (compileType) {
                    //草稿箱编辑处理
                    editBackHandle();
                } else {
                    finish();
                }
                break;
            case R.id.send:
                TopicTitleFragment tt = (TopicTitleFragment) adapter.getItem(0);
                TopicContentFragment tc = (TopicContentFragment) adapter.getItem(1);
                TopicSortFragment ts = (TopicSortFragment) adapter.getItem(2);
                if (tt.getTitleContent().trim().isEmpty()) {
                    ToastUtils.showToast(SendTopicActivity.this, R.string.title_notnull);
                    return;
                }
                if (tt.getTitleContent().length() > TopicTitleFragment.INPUT_LIMIT) {
                    ToastUtils.showToast(this, R.string.title_too_much);
                    return;
                }

                if (tc.getText().trim().isEmpty() || tc.getHtmlText().trim().isEmpty()) {
                    ToastUtils.showToast(SendTopicActivity.this, "发表失败，话题内容不能为空");
                    return;
                }
                if (null == ts.getSortId()) {
                    ToastUtils.showToast(SendTopicActivity.this, "话题分类数据有误");
                    return;
                }
                if (tc.getHtmlText().length() > 20000) {
                    ToastUtils.showToast(SendTopicActivity.this, "发表失败，话题内容最最多20000字");
                    return;
                }
                if (compileType) { // 如果是编辑状态的
                    String tag = draftBean.getTag();
                    if (tag != null) {
                        // 删除老的-----------------------------------------
                        DraftService.getSingleton(this).deleteByTag(tag);
                    }
                    EventTools.instance().sendDftsDeleteByEdit(draftBean.getTag());

                }
                DraftBean db = new DraftBean();
                db.setState(2); //
                db.setCategoryList(categoryList);
                setTag(db);
                db.setGroup_id(groupId);
                db.setTopic_paths(tc.getImagePathList());
                db.setTopic_content(tc.getHtmlText());
                db.setTopic_title(tt.getTitleContent());
                db.setCategory_id(ts.getSortId());
                db.setType(0);
                // 执行Db存语句
                try {
                    DraftBean cansavedb = LocalHelpTools.instance(   //有可能图片过多  或者文字过多 在发送之前 需要保存到本地数据库，过多的作为本地文件的形式存储起来
                            SendTopicActivity.this).chageCanSave_Topic(db);
                    if (DraftService.getSingleton(this).save(cansavedb)) {
                        // 从数据库取出未发送的发送
                        SqlQueueTools.instance().sendToQueue(SqlQueueTools.TOPIC, db, SendTopicActivity.this);
                        finish();
                    }
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    ToastUtils.showToast(SendTopicActivity.this, "数据异常发送失败");
                    return;
                }

                break;
            default:
                break;
        }
    }

    private void setTag(DraftBean db) {
        if (db.getTag().equals("0")) {
            db.setTag(TimeUtils.getCurrentTimeInString());
        }
    }

    public void startForResult(int i, int imageSize) {
        switch (i) {
            case 1:
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = SendTools.getDateUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(intent, 1);
                break;
            case 0:
                Intent in = new Intent(SendTopicActivity.this, GalleryActivity.class);
                in.putExtra("max_image_count", imageSize);
                startActivityForResult(in, 0); // 相册
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data != null) {
            } else {
                ArrayList<String> al = new ArrayList<String>();
                al.add(uri.getPath());
                TopicContentFragment tc = (TopicContentFragment) adapter.getItem(1);
                tc.insertImage(al);
            }
        } // 读取相册缩放图片
        if (requestCode == 0 && resultCode == 5) {
            if (MyAdapter.mSelectedImage.size() == 0) {
            } else {
                TopicContentFragment tc = (TopicContentFragment) adapter.getItem(1);
//				tc.getImageUrl().addAll(MyAdapter.mSelectedImage);
                tc.insertImage(MyAdapter.mSelectedImage);
                MyAdapter.mSelectedImage.clear();
            }
        }

    }

    @Override
    public void setNodeName() {
        // TODO Auto-generated method stub
        setmNodeName("");
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();
        if (arg0 == 2) {
            if (isOpen) {
                imm.hideSoftInputFromWindow(viewPager.getWindowToken(), 0); //强制隐藏键盘
            }
        }
//		else{
//			imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);   //强制显示键盘
//		}
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            //发表主题
            if (!compileType && hasContent()) {
                showSaveTips();
                return true;
            }
            //草稿箱进来的编辑后
            if (compileType) {
                editBackHandle();
                return true;
            }

        }
        return super.onKeyDown(keyCode, event);

    }

    private boolean hasContent() {


        TopicTitleFragment tt = (TopicTitleFragment) adapter.getItem(0);
        TopicContentFragment tc = (TopicContentFragment) adapter.getItem(1);
//		TopicSortFragment ts = (TopicSortFragment) adapter.getItem(2);
        boolean hasTitle = !tt.getTitleContent().trim().isEmpty();
        boolean hasContent = !tc.getHtmlText().trim().isEmpty() && !tc.getText().isEmpty();
        return hasTitle || hasContent;
    }

    private void showSaveTips() {
        MessageDialog messageDialog = new MessageDialog(this, "提示", "是否保存到草稿箱?", "不保存", "保存");
        messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
            @Override
            public void OnClick(View view) {
                saveDraft();
                finish();
            }
        });
        messageDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {
            @Override
            public void OnClick(View view) {
                finish();
            }
        });
        messageDialog.show();
    }

    private void saveDraft() {
        TopicTitleFragment tt = (TopicTitleFragment) adapter.getItem(0);
        TopicContentFragment tc = (TopicContentFragment) adapter.getItem(1);
        TopicSortFragment ts = (TopicSortFragment) adapter.getItem(2);
        boolean hasTitle = !tt.getTitleContent().trim().isEmpty();
        boolean hasContent = !tc.getHtmlText().trim().isEmpty();
        if (hasTitle || hasContent) {
            //保存到草稿箱
            DraftBean db = new DraftBean();
            db.setState(0);
            db.setCategoryList(categoryList);
            setTag(db);
            db.setGroup_id(groupId);
            db.setTopic_paths(tc.getImagePathList());
            db.setTopic_content(tc.getHtmlText());
            db.setTopic_title(tt.getTitleContent());
            db.setCategory_id(ts.getSortId());
            db.setType(0);
            try {
                DraftBean cansavedb = LocalHelpTools.instance(   //有可能图片过多  或者文字过多 在发送之前 需要保存到本地数据库，过多的作为本地文件的形式存储起来
                        SendTopicActivity.this).chageCanSave_Topic(db);
                DraftService.getSingleton(this).save(cansavedb);
                ToastUtils.showToast(this, R.string.draf_saved_tips);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    private void editBackHandle() {
        DraftService.getSingleton(this).deleteByTag(draftBean.getTag());
        //更新编辑后的数据
        if (hasContent()) {
            TopicTitleFragment tt = (TopicTitleFragment) adapter.getItem(0);
            TopicContentFragment tc = (TopicContentFragment) adapter.getItem(1);
            TopicSortFragment ts = (TopicSortFragment) adapter.getItem(2);
            draftBean.setTopic_title(tt.getTitleContent());
            draftBean.setTopic_content(tc.getHtmlText());
            draftBean.setTopic_paths(tc.getImagePathList());
            draftBean.setCategory_id(ts.getSortId());
            draftBean.setTag(TimeUtils.getCurrentTimeInString());
            DraftService.getSingleton(this).save(draftBean);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
