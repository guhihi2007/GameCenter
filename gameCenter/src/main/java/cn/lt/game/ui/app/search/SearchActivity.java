package cn.lt.game.ui.app.search;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.baidu.mobstat.StatService;

import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseFragmentActivity;
import cn.lt.game.db.service.SearchService;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.statistics.NodeConstant;
import cn.lt.game.ui.app.adapter.data.ItemData;
import cn.lt.game.ui.app.search.AdvertisementFragment.TitlesListFragmentCallBack;

/***
 * 搜索主页面
 *
 * @author ltbl
 */
public class SearchActivity extends BaseFragmentActivity implements OnClickListener, TitlesListFragmentCallBack {
    public static final int SEARCHADV = 0;
    public static final int SEARCHNODATA = 1;
    public static final int SEARCHRESULT = 2;
    public static final int SEARCHAUTOMATCH = 3;
    private AdvertisementFragment advertisementFragment;
    private SearchResultFragment resultFragment;
    private SearchNoDataFragment nodataFragment;
    private SearchAutoMatchFragment automatchFragment;
    private ImageView ib_search, ib_delete, ib_back;
    private View v_divider;
    private EditText autoEditText;
    private String keyword = "";
    private int returnType = 0;
    private MyWatcher watcher;
    private List<ItemData<? extends BaseUIModule>> mNoDataLists;
    private FragmentManager fm;
    private FragmentTransaction transaction;

    public List<ItemData<? extends BaseUIModule>> getmNoDataLists() {
        return mNoDataLists;
    }

    public void setmNoDataLists(List<ItemData<? extends BaseUIModule>> mNoDataLists) {
        this.mNoDataLists = mNoDataLists;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_search_main);
        initView();
        advertisementFragment = new AdvertisementFragment();
        watcher = new MyWatcher();
        autoEditText.addTextChangedListener(watcher);
        keyword = getIntent().getStringExtra("keyWord");
        if (keyword == null) {
            setFragment(SEARCHADV, ""); // 默认进入搜索推荐页面
        } else {
            setEditText(keyword);
            ib_delete.setVisibility(View.INVISIBLE);
            v_divider.setVisibility(View.VISIBLE);
            setFragment(SEARCHRESULT, keyword);
        }
    }

    private void initView() {
        ib_search = (ImageView) findViewById(R.id.ib_search);
        ib_delete = (ImageView) findViewById(R.id.ib_deleteone);
        v_divider = findViewById(R.id.v_divider);
        ib_back = (ImageView) findViewById(R.id.ib_lt_back);
        autoEditText = (EditText) findViewById(R.id.et_search);
        setOnClickListener();
    }

    private void setOnClickListener() {
        ib_search.setOnClickListener(this);
        ib_back.setOnClickListener(this);
        autoEditText.setOnClickListener(this);
        ib_delete.setOnClickListener(this);
        autoEditText.setOnEditorActionListener(new OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                LogUtils.i("Erosion","actionId====" + actionId);
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    LogUtils.i("Erosion","return ture");
                    keyword = autoEditText.getText().toString().trim();
                    // 隐藏键盘
                    hideKeyboard();
                    if ("".equals(keyword) || keyword.length() == 0) {
                        ToastUtils.showToast(SearchActivity.this, "请输入关键字");
                    } else {
                        // 跳到搜索结果页面并保存用户输入的搜索关键字
                        SearchService.getInstance(SearchActivity.this).save(keyword);
                        setFragment(SEARCHRESULT, keyword);
                    }
                    return true;
                } else {
                    LogUtils.i("Erosion","return false");
                    return false;

                }
            }
        });

    }

    public void setFragment(int flag, String keyWord) {
        switch (flag) {
            case SEARCHADV:// 热门搜索页
                returnType = SEARCHADV;
                showKeyboard();
                addFragment(advertisementFragment);
                break;
            case SEARCHRESULT:// 搜索结果页
                returnType = SEARCHRESULT;
                hideKeyboard();
                ib_search.setFocusable(true);
                autoEditText.clearFocus();
                resultFragment = new SearchResultFragment();
                Bundle bundle = new Bundle();
                bundle.putString("keyWord", keyWord);
                resultFragment.setArguments(bundle);
                addFragment(resultFragment);
                break;
            case SEARCHNODATA:// 无搜索结果页
                LogUtils.i("zzz", "创建Nodataframent");
                returnType = SEARCHNODATA;
                hideKeyboard();
                if (nodataFragment == null) {
                    nodataFragment = new SearchNoDataFragment();
                }
                addFragment(nodataFragment);
                break;
            case SEARCHAUTOMATCH:// 搜索匹配页
                returnType = SEARCHAUTOMATCH;
                automatchFragment = new SearchAutoMatchFragment();
                addFragment(automatchFragment);
                break;
            default:
                break;
        }

    }

    private void addFragment(Fragment fragment) {
        fm = getSupportFragmentManager();
        transaction = fm.beginTransaction();
        transaction.replace(R.id.fl_content, fragment);
        transaction.commit();
    }

    // 隐藏键盘
    public boolean hideKeyboard() {
        ib_search.requestFocus();
        InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean active = imm.isActive(autoEditText);
        imm.hideSoftInputFromWindow(autoEditText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        autoEditText.clearFocus();
        return active;
    }

    public void showKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) autoEditText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.showSoftInput(autoEditText, 0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        StatService.onResume(this);
        super.onResume();
        if (autoEditText != null) {
            autoEditText.setFocusable(true);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        StatService.onPause(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_search:
                keyword = autoEditText.getText().toString().trim();
                // 隐藏键盘
                if ("".equals(keyword) || keyword.length() == 0) {
                    ToastUtils.showToast(this, "请输入关键字");
                } else {
                    hideKeyboard();
                    // 跳到搜索结果页面并保存用户输入的搜索关键字
                    SearchService.getInstance(SearchActivity.this).save(keyword);
                    setFragment(SEARCHRESULT, keyword);
                }
                break;
            case R.id.et_searchcontent:
                keyword = "";
                break;
            case R.id.ib_deleteone:
                setFragment(SEARCHADV, "");
                keyword = "";
                autoEditText.setText("");
                ib_delete.setVisibility(View.INVISIBLE);
                break;
            case R.id.ib_lt_back:
                finishActivityBehavior();
                break;
        }
    }

    @Override
    public void onHotTagSelected(FunctionEssence info) {
        // 点击本周热门标签跳转到标签列表页
        ActivityActionUtils.jumpToSearhTagActiviy(this, info.getUniqueIdentifier(), info.getTitle());
    }

    @Override
    public void hotWordOnclick(String hotword) {
        setFragment(SEARCHRESULT, hotword);
        setEditText(hotword);
    }

    private void setEditText(String keyword) {
        autoEditText.setText(keyword);
        autoEditText.setSelection(keyword.length());
    }

    @Override
    public void gotoNoDataFragment() {
        LogUtils.i("zzz", "进入到无结果页面");
        setFragment(SEARCHNODATA, "");
    }

    @Override
    public void setNodeName() {
        setmNodeName(NodeConstant.SearchRoot);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finishActivityBehavior();
        }
        return false;
    }

    // 统一 返回行为
    private void finishActivityBehavior() {
        switch (returnType) {
            case SEARCHADV:
                finish();
                break;
            case SEARCHNODATA:
            case SEARCHAUTOMATCH:
            case SEARCHRESULT:
                autoEditText.setText("");
                break;
            default:
                finish();
                break;

        }
    }

    @Override
    public void gotoAdverFragment() {
        autoEditText.setText("");
        setFragment(SEARCHADV, "");
    }

    @Override
    public void saveNoDataList(List<ItemData<? extends BaseUIModule>> list) {
        mNoDataLists = list;
    }

    public class MyWatcher implements TextWatcher {
        private CharSequence cs;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            keyword = s.toString();
            MyApplication.application.mCurrentWord=keyword;
            cs = s;
            if (!TextUtils.isEmpty(keyword) && keyword.length() > 0) {
                ib_delete.setVisibility(View.VISIBLE);
                v_divider.setVisibility(View.VISIBLE);
                if (returnType != SEARCHRESULT) {
                    setFragment(SEARCHAUTOMATCH, "");
                    LogUtils.i("zzz","是否允许请求网络=="+SearchAutoMatchFragment.isLoaded);
                    SearchAutoMatchFragment.isLoaded = true;
                    automatchFragment.requestData(keyword);
                }
            } else {
                ib_delete.setVisibility(View.INVISIBLE);
                    setFragment(SEARCHADV, "");
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            int editStart = autoEditText.getSelectionStart();
            int editEnd = autoEditText.getSelectionEnd();
            if (cs.length() >= 50) {
                ToastUtils.showToast(SearchActivity.this, "您输入的内容已超过限制！");
                s.delete(editStart - 1, editEnd);
            }
            if (TextUtils.isEmpty(keyword)) {
            }
        }
    }

}
