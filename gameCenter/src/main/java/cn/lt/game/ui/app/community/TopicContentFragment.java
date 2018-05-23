package cn.lt.game.ui.app.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.community.face.FaceView;
import cn.lt.game.ui.app.community.face.FaceView.Work;
import cn.lt.game.ui.app.community.widget.RichEditText;
import cn.lt.game.ui.app.sidebar.LoadingDialog;

//话题内容Fragment
@SuppressLint("ValidFragment")
public class TopicContentFragment extends BaseFragment implements
        OnClickListener, Work {

    private View      view;
    private ImageView face, keyboard, camera, gallery;
    private SendTopicActivity activity;
    private LinearLayout      faceGroup;
    FaceView   fv_face;
    private String content = "";
    private RichEditText contentET;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (SendTopicActivity) activity;
    }

    public static TopicContentFragment newInstance(String id) {
        TopicContentFragment myFragment = new TopicContentFragment();
        Bundle               args       = new Bundle();
        args.putString("id", id);
        myFragment.setArguments(args);
        return myFragment;
    }


    public void setData(String content) {
        this.content = content;
    }

    public String getHtmlText() {
        return contentET.getHtml();
    }

    public String getText(){
        return contentET.getText().toString().trim();
    }

    public ArrayList<String> getImagePathList(){
        return contentET.getImagePathList();
    }

    @Override
    public void setPageAlias() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.topiccontent_fragment, container,
                    false);
            initView();
            initListener();
        }
        return view;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (keyboard.getVisibility() == View.VISIBLE) {
                keyboard();
            }
        }
    }

    private void initListener() {
        camera.setOnClickListener(this);
        gallery.setOnClickListener(this);
        face.setOnClickListener(this);
        keyboard.setOnClickListener(this);
        contentET.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.getParent().requestDisallowInterceptTouchEvent(true);
                faceGroup.setVisibility(View.GONE);
                face.setVisibility(View.VISIBLE);
                keyboard.setVisibility(View.GONE);
                return false;
            }
        });

        contentET.setOnImageListener(new RichEditText.OnImageListener() {
            @Override
            public void startInsertImage() {
                showProgressDialog();
            }

            @Override
            public void endInsertImage() {
                hideProgressDialog();
            }
        });


        //注入数据,如果是编辑草稿箱
        if (!TextUtils.isEmpty(content)) {
            contentET.setHtml(this.content);
            //添加图片监听
        }
    }

    LoadingDialog progressDialog;


    private void initView() {
        faceGroup = (LinearLayout) view.findViewById(R.id.facegroup);
        contentET = (RichEditText) view.findViewById(R.id.richEditText);


        camera = (ImageView) view.findViewById(R.id.camera);
        face = (ImageView) view.findViewById(R.id.face);
        keyboard = (ImageView) view.findViewById(R.id.keyboard);
        gallery = (ImageView) view.findViewById(R.id.gallery);
        initface();


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.camera:
                if (getImagePathList().size() < SendCommentActivity.MAX_UPLOAD_IMAGE_COUNT) {
                    activity.startForResult(1, 0);
                } else {
                    ToastUtils.showToast(getActivity(), R.string.image_count_limit_tips);
                }
                break;
            case R.id.face:
                face();
                break;
            case R.id.keyboard:
                keyboard();
                break;
            case R.id.gallery:
                if (getImagePathList().size() < SendCommentActivity.MAX_UPLOAD_IMAGE_COUNT) {
                    activity.startForResult(0, SendCommentActivity.MAX_UPLOAD_IMAGE_COUNT - getImagePathList().size());
                } else {
                    ToastUtils.showToast(getActivity(), R.string.image_count_limit_tips);
                }
                break;

            default:
                break;
        }

    }

    private void keyboard() {
        face.setVisibility(View.VISIBLE);
        keyboard.setVisibility(View.GONE);
        faceGroup.setVisibility(View.GONE);
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.showSoftInput(contentET, 1);
    }

    private void face() {
        face.setVisibility(View.GONE);
        keyboard.setVisibility(View.VISIBLE);
        faceGroup.setVisibility(View.VISIBLE);
        InputMethodManager imm = (InputMethodManager) getActivity()
                .getSystemService(getActivity().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(contentET.getWindowToken(), 0);
    }

    private void initface() { // 初始化表情 以及键盘隐藏或者显示等等状态
        keyboard.setVisibility(View.GONE);
        face.setVisibility(View.VISIBLE);
        faceGroup.setVisibility(View.GONE);
        fv_face = new FaceView(getActivity(), null, this);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        faceGroup.addView(fv_face, params);
    }

    @Override
    public void onClick(String item_str) {
        contentET.insert(item_str);
    }

    public void insertImage(ArrayList<String> imageList) {
        if (getImagePathList().size() + imageList.size() > SendCommentActivity.MAX_UPLOAD_IMAGE_COUNT) {
            ToastUtils.showToast(getActivity().getApplicationContext(), R.string.image_count_limit_tips);
            return;
        }

        contentET.insertImages(new ArrayList<String>(imageList));

    }

    private void showProgressDialog() {
        if (progressDialog == null || !progressDialog.isShowing()) {
            progressDialog = new LoadingDialog(getActivity(),"正在插入图片，请稍后...");
            progressDialog.show();
        }

    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


}
