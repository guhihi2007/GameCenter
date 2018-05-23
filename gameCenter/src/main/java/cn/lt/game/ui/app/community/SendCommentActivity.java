package cn.lt.game.ui.app.community;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.db.service.DraftService;
import cn.lt.game.gallery.GalleryActivity;
import cn.lt.game.gallery.MyAdapter;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.ui.app.community.face.FaceView;
import cn.lt.game.ui.app.community.face.FaceView.Work;
import cn.lt.game.ui.app.community.model.DraftBean;
import cn.lt.game.ui.app.community.widget.RichEditText;
import cn.lt.game.ui.app.sidebar.LoadingDialog;

//评论Activity
public class SendCommentActivity extends BaseActivity implements
        OnClickListener, Work {
    private static final int INPUT_LIMIT            = 20000;
    public static final  int MAX_UPLOAD_IMAGE_COUNT = 9;
    private ImageView face, keyboard, camera, gallery;
    private LinearLayout faceGroup;
    FaceView fv_face;
    private Uri            uri;
    private TextView       title;
    private ImageButton    back;
    private Button         send;
    private int            topicId;
    private int            groupId;
    private String         topicTitle;
    private String         topicContent;
    private String         groupTitle;
    private boolean        isAutoJump;
    private ProgressDialog pd;
    private Context context     = SendCommentActivity.this;
    private boolean compileType = false;// 是否是编辑状态
    private DraftBean draftBean;// 如果是编辑状态可获得到这个BEAN

    private RichEditText richEditor;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.topic_sendcomment);
        Bundle bundle = getIntent().getExtras();
        if (bundle.getString("type").equals("1")) { // 正常启动
            topicId = bundle.getInt("topicId");
            groupId = bundle.getInt("groupId");
            topicTitle = bundle.getString("topic_title");
            topicContent = bundle.getString("topic_content");
            isAutoJump = bundle.getBoolean("isAutoJump");
            groupTitle = bundle.getString("group_title");
        } else {
            draftBean = (DraftBean) getIntent().getExtras().get("draftBean");
            compileType = true;
            setData();
        }
        initView();
        initListener();
    }

    private void setData() {
        topicId = draftBean.getTopic_Id();
        groupId = draftBean.getGroup_id();
        topicTitle = draftBean.getTopic_title();
        topicContent = draftBean.getTopic_content();
        groupTitle = draftBean.getGroupTitle();
        isAutoJump = draftBean.isAutoJump();

    }

    private void initListener() {
        back.setOnClickListener(this);
        send.setOnClickListener(this);
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        face.setOnClickListener(this);
        keyboard.setOnClickListener(this);
        richEditor.setOnTouchListener(new OnTouchListener() { // 评论的文本框，一旦触摸代表用户要输入内容，作出相应的处理

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                faceGroup.setVisibility(View.GONE);
                face.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.GONE);
                return false;
            }
        });

        richEditor.setOnImageListener(new RichEditText.OnImageListener() {
            @Override
            public void startInsertImage() {
                if (progressDialog == null || !progressDialog.isShowing()) {
                    progressDialog = new LoadingDialog(SendCommentActivity.this, "正在插入图片，请稍后...");
                    progressDialog.show();
                }
            }

            @Override
            public void endInsertImage() {
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
        });


        //编辑草稿箱的数据传递进来
        if (compileType) {
            richEditor.setHtml(draftBean.getComment_content());
        }
    }

    /**
     * 插入图片进度
     */
    LoadingDialog progressDialog;

    private void initView() {
        pd = new ProgressDialog(SendCommentActivity.this);
        back = (ImageButton) findViewById(R.id.back);
        send = (Button) findViewById(R.id.send);
        title = (TextView) findViewById(R.id.title);
        title.setText(getResources().getText(R.string.send_comment));
        faceGroup = (LinearLayout) findViewById(R.id.facegroup);
        camera = (ImageView) findViewById(R.id.camera);
        face = (ImageView) findViewById(R.id.face);
        keyboard = (ImageView) findViewById(R.id.keyboard);
        gallery = (ImageView) findViewById(R.id.gallery);
        richEditor = (RichEditText) findViewById(R.id.richEditText);


        initface();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera: // 调用系统照相机功能
                if (getImgCount() <= 9) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    uri = SendTools.getDateUri();
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                    startActivityForResult(intent, 1);
                } else {
                    ToastUtils.showToast(SendCommentActivity.this, "最多只可评论9张图片哦");
                }
                break;
            case R.id.face:
                face();
                break;
            case R.id.keyboard:
                keyboard();
                break;
            case R.id.gallery: // 调用相册
                if (getImgCount() < MAX_UPLOAD_IMAGE_COUNT) {
                    Intent in = new Intent(context, GalleryActivity.class);
                    in.putExtra("max_image_count", MAX_UPLOAD_IMAGE_COUNT - richEditor.getImagePathList().size());
                    startActivityForResult(in, 0); // 相册
                } else {
                    ToastUtils.showToast(SendCommentActivity.this, "最多只可评论9张图片哦");
                }
                break;
            case R.id.send: // 发送评论
                if (richEditor.getText().toString().trim().isEmpty() || richEditor.getHtml().trim().length() == 0) {
                    ToastUtils.showToast(SendCommentActivity.this, "评论内容不能为空");
                    return;
                }
                if (richEditor.getHtml().length() > INPUT_LIMIT) {
                    ToastUtils.showToast(SendCommentActivity.this,
                            "内容最大长度不可超过20000字哦");
                    return;
                }

                /**
                 * 内容为空并且无图片
                 */
                if (!richEditor.getHtml().trim().isEmpty() || getImgCount() != 0) {
                    if (compileType) { // 如果是编辑状态的
                        String tag = draftBean.getTag();
                        if (tag != null) {
                            DraftService.getSingleton(this).deleteByTag(tag);
                        }
                        EventTools.instance().sendDftsDeleteByEdit(
                                draftBean.getTag());
                    }
                    DraftBean db = new DraftBean();
                    db.setState(2); //
                    setTag(db);

                    db.setComment_content(richEditor.getHtml());
                    db.setComment_paths(richEditor.getImagePathList());
                    db.setGroup_id(groupId);
                    db.setTopic_content(topicContent);
                    db.setTopic_title(topicTitle);
                    db.setGroupTitle(groupTitle);
                    db.setAutoJump(isAutoJump);
                    db.setTopic_Id(topicId);
                    db.setType(1);
                    try {
                        DraftBean cansavedb = LocalHelpTools.instance(context)
                                .chageCanSave_Comment(db);
                        if (DraftService.getSingleton(this).save(cansavedb)) {
                            // 从数据库取出未发送的发送
                            SqlQueueTools.instance().sendToQueue(
                                    SqlQueueTools.COMMENT, db,
                                    SendCommentActivity.this);
                            finish();
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                        ToastUtils.showToast(SendCommentActivity.this, "数据异常发送失败");
                        return;
                    }
                    // 执行Db存语句

                }

                break;
            case R.id.back:
                //发表评论处理
                if (!compileType && hasContent()) {
                    showSaveTips();
                } else if (compileType) {
                    //草稿箱编辑处理
                    editBackHandle();
                } else {
                    finish();
                }

                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pd.dismiss();
    }

    private void keyboard() { // 点击键盘图标的时候业务处理
        face.setVisibility(View.VISIBLE);
        keyboard.setVisibility(View.GONE);
        faceGroup.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.showSoftInput(richEditor, 0);
    }

    private void face() { // 点击表情图标的时候的业务处理
        face.setVisibility(View.GONE);
        keyboard.setVisibility(View.VISIBLE);
        faceGroup.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(richEditor.getWindowToken(), 0);
    }

    private void initface() { // 初始化表情 以及键盘隐藏或者显示等等状态
        keyboard.setVisibility(View.GONE);
        face.setVisibility(View.VISIBLE);
        faceGroup.setVisibility(View.GONE);
        fv_face = new FaceView(this, null, this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        faceGroup.addView(fv_face, params);
    }

    @Override
    public void onClick(String item_str) {
        richEditor.insert(item_str);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) { // 如果是从相机返回的，将路径加载到要发送的集合里面
            if (data != null) {
            } else {
                ArrayList<String> al = new ArrayList<String>();
                al.add(uri.getPath());

                insertImage(al);
            }

        } // 读取相册缩放图片
        if (requestCode == 0 && resultCode == 5) { // 如果是从相册返回的，将相册选择的所有图片路径加载到要发送的集合里面
            if (MyAdapter.mSelectedImage.size() == 0) {
            } else {
                insertImage(MyAdapter.mSelectedImage);
                MyAdapter.mSelectedImage.clear();
            }
        }

    }

    public static Intent getIntent(Context context, int groupId,
                                   String topic_title, String topic_content, String group_title,
                                   Boolean isAutoJump, int topicId, String type) {
        Intent in = new Intent(context, SendCommentActivity.class);
        Bundle bd = new Bundle();
        bd.putString("type", type);
        bd.putInt("groupId", groupId);
        bd.putInt("topicId", topicId);
        bd.putString("topic_title", topic_title);
        bd.putString("topic_content", topic_content);
        bd.putString("group_title", group_title);
        bd.putBoolean("isAutoJump", isAutoJump);
        in.putExtras(bd);
        return in;
    }

    private void setTag(DraftBean sb) { // 设置唯一标示

        if (sb.getTag().equals("0")) {
            sb.setTag(TimeUtils.getCurrentTimeInString());
        }
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_COMMENT_PUBLISH);

    }

    private int getImgCount() {
        return richEditor.getImagePathList().size();
    }


    private void insertImage(ArrayList<String> imageList) {

        if (getImgCount() + imageList.size() > 9) {
            ToastUtils.showToast(this, R.string.image_count_limit_tips);
            return;
        }


        richEditor.insertImages(new ArrayList<String>(imageList));
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (!compileType && hasContent()) {
                showSaveTips();
                return true;
            }
            if (compileType) {
                editBackHandle();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean hasContent() {
        return (!richEditor.getText().toString().trim().isEmpty() && !richEditor.getHtml().trim().isEmpty()) || getImgCount() > 0;
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
        DraftBean db = new DraftBean();
        db.setState(0); //
        setTag(db);
        db.setComment_content(richEditor.getHtml());
        db.setComment_paths(richEditor.getImagePathList());
        db.setGroup_id(groupId);
        db.setTopic_content(topicContent);
        db.setTopic_title(topicTitle);
        db.setGroupTitle(groupTitle);
        db.setAutoJump(isAutoJump);
        db.setTopic_Id(topicId);
        db.setType(1);
        try {
            DraftBean cansavedb = LocalHelpTools.instance(context)
                    .chageCanSave_Comment(db);
            DraftService.getSingleton(this).save(cansavedb);
            ToastUtils.showToast(this, R.string.draf_saved_tips);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void editBackHandle() {
        DraftService.getSingleton(this).deleteByTag(draftBean.getTag());
        if (hasContent()) {
            draftBean.setComment_content(richEditor.getHtml());
            draftBean.setComment_paths(richEditor.getImagePathList());
            draftBean.setGroup_id(groupId);
            draftBean.setTopic_content(topicContent);
            draftBean.setTopic_title(topicTitle);
            draftBean.setGroupTitle(groupTitle);
            draftBean.setAutoJump(isAutoJump);
            draftBean.setTag(TimeUtils.getCurrentTimeInString());
            draftBean.setTopic_Id(topicId);
            DraftService.getSingleton(this).save(draftBean);
        }
        finish();
    }
}
