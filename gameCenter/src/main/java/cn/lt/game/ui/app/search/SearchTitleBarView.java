package cn.lt.game.ui.app.search;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import cn.lt.game.R;
import cn.lt.game.db.service.SearchService;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;

/**
 * Created by Apple on 15/4/1.
 */
public class SearchTitleBarView extends LinearLayout implements OnClickListener {
    public Context context;
    private ImageView bt_back;
    private ImageView bt_search;
    private EditText content;
    private ImageView ib_deleteone;

    public SearchTitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        intView(context);
        intAction(context);
        this.context = context;
    }

    private void intAction(Context context) {
        bt_search.setOnClickListener(this);
    }

    private void intView(Context context) {
        View view = View.inflate(context, R.layout.lt_searchjp, this);
        bt_back = (ImageView) view.findViewById(R.id.ib_lt_back);
        bt_search = (ImageView) view.findViewById(R.id.ib_search);
        content = (EditText) view.findViewById(R.id.et_search);
        ib_deleteone = (ImageView) view.findViewById(R.id.ib_deleteone);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ib_lt_back:
                ((Activity) getContext()).finish();
                break;
            case R.id.ib_search:
                if (TextUtils.isEmpty(content.getText().toString().trim())) {
                    ActivityActionUtils.activity_jump(context, SearchActivity.class);
                } else {
                    SearchService service = SearchService.getInstance(context);
                    if (service.findOne(content.getText().toString().trim())) {
                        service.deleteOne(content.getText().toString().trim());
                        service.add(content.getText().toString().trim());
                    } else {
                        service.add(content.getText().toString().trim());
                    }
                    //临时传值
                    SharedPreferencesUtil sputils = new SharedPreferencesUtil(context);
                    sputils.add("keyWord_GameName", content.getText().toString());
                    ActivityActionUtils.activity_Jump_Value(context, SearchActivity.class,
                            "keyWord", content.getText().toString());
                }

                break;
            case R.id.et_searchcontent:
                break;
            case R.id.ib_deleteone:
                if (!TextUtils.isEmpty(content.getText().toString().trim())) {
                    content.setText("");
                }
                break;

        }
    }

    public void setTextViewText(String keyWord) {
        content.setText(keyWord);
    }


}
