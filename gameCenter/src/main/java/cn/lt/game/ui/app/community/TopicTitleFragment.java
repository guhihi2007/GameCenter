package cn.lt.game.ui.app.community;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.lib.util.ToastUtils;

//话题标题Fragment
@SuppressLint("ValidFragment")
public class TopicTitleFragment extends BaseFragment {
    private String   id;
    private View     view;
    private EditText title;
    private TextView prompt;
    private String titlecontent = "";
    private SendTopicActivity activity;


    //限制60个字符
    public static final int INPUT_LIMIT = 30;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = (SendTopicActivity) activity;
    }

    public String getTitleContent() {
        if (title == null) {
            return titlecontent;
        } else {
            return title.getText().toString();
        }

    }

    public static TopicTitleFragment newInstance(String id) { // 防止横竖屏切换导致重构异常
        TopicTitleFragment myFragment = new TopicTitleFragment();
        Bundle             args       = new Bundle();
        args.putString("id", id);
        myFragment.setArguments(args);
        return myFragment;
    }

    public void setData(String content) {
        titlecontent = content;
    }

    @Override
    public void setPageAlias() {

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.id = getArguments().getString("id");
        if (null != view) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (null != parent) {
                parent.removeView(view);
            }
        } else {
            view = inflater.inflate(R.layout.topictitle_fragment, container, false);
            initView();
        }
        return view;
    }

    private void initView() {
        title = (EditText) view.findViewById(R.id.title);
        title.setText(titlecontent);
        prompt = (TextView) view.findViewById(R.id.prompt);

        setInputSurplusCountText(INPUT_LIMIT - titlecontent.length());
        setInputLimit();


        title.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        for(int i = start;i<end;i++){
                            char c = source.charAt(i);
                            if(c == ' '){
                                return "";
                            }
                        }
                        return null;
                    }
                }
        });

    }


    private void setInputLimit() {
//        title.setFilters(new InputFilter[]{new InputFilter.LengthFilter(INPUT_LIMIT)});
        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                int surplus = INPUT_LIMIT - s.length();
                setInputSurplusCountText(surplus);
            }
        });
        //处理回车按键
        title.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    if (title.getText().toString().trim().length() > 0) {
                        activity.setCurrentItem(1);
                    } else {
                        ToastUtils.showToast(activity, R.string.title_notnull);
                    }
                    return true;
                }
                return false;
            }
        });


    }

    /**
     * 设置剩余可输入字符文本
     *
     * @param surplusCount
     */
    private void setInputSurplusCountText(int surplusCount) {
        if(surplusCount>=0) {
            prompt.setText(String.format(getResources().getString(R.string.input_count_tips_v1), surplusCount));
            prompt.setTextColor(getResources().getColor(R.color.point_grey));
        }else{
            int count = Math.abs(surplusCount);
            prompt.setText(String.format(getResources().getString(R.string.input_count_tips_v2), count));
            prompt.setTextColor(getResources().getColor(R.color.light_yellow));
        }
    }

}
