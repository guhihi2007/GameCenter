package cn.lt.game.ui.app.gamedetail;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.sidebar.LoadingDialog;

/***
 * 游戏详情-发表评论
 *
 * @author ltbl
 */
public class SendGameDetailCommentActivity extends BaseActivity implements OnClickListener {
    private TextView tv_title;
    private Button btn_send;
    private TextView send;
    private ImageButton ib_back;
    private int gameId;
    private EditText et_content;
    protected LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_game_detial_comment);
        gameId = this.getIntent().getExtras().getInt("gameId");
        initView();
    }

    private void initView() {
        loadingDialog = new LoadingDialog(this);
        btn_send = (Button) findViewById(R.id.send);
        send = (TextView) findViewById(R.id.btn_finish);
        et_content = (EditText) findViewById(R.id.et_content);
        btn_send.setVisibility(View.GONE);
        tv_title = (TextView) findViewById(R.id.title);
        tv_title.setText("发表评论");
        ib_back = (ImageButton) findViewById(R.id.back);
//        btn_send.setOnClickListener(this);
        send.setOnClickListener(this);
        ib_back.setOnClickListener(this);
    }

    /***
     * 验证输入的内容是否符合规范
     *
     * @return
     */
    public boolean containsEmoji(String source) {
        int len = source.length();
        for (int i = 0; i < len; i++) {
            char codePoint = source.charAt(i);
            if (!isEmojiCharacter(codePoint)) {
                return true;
            }
        }
        return false;
    }

    /***
     * 检查输入内容是否包含Emoji表情符号
     *
     * @param codePoint
     * @return
     */
    private boolean isEmojiCharacter(char codePoint) {
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF));
    }

    /***
     * 发送成功刷新评论列表回调
     *
     * @author ltbl
     */
    public interface RefreshCommentCallBack {
        void refreshComment();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_finish:
                String str = et_content.getText().toString();
                hasIlleStr(str);
                break;
            case R.id.back:
                finish();
                break;
        }
    }

    public boolean hasIlleStr(String str) {
        try {
            if (str.getBytes("GBK").length > 280) {
                ToastUtils.showToast(this, "内容最多不可超过140字哦！");
                loadingDialog.hide();
                return false;
            }
            if (str.getBytes("GBK").length < 1) {
                ToastUtils.showToast(this, "请输入评论内容！");
                loadingDialog.hide();
                return false;
            }
            if (!str.trim().isEmpty()) {
                if (containsEmoji(str)) {
                    ToastUtils.showToast(this, "内容不符合规范！");
                    loadingDialog.hide();
                } else {
                    ToastUtils.showToast(SendGameDetailCommentActivity.this, "评论已提交审核！");
                    finish();
                }
            } else {
                ToastUtils.showToast(SendGameDetailCommentActivity.this, "内容不能少于一个字！");
                loadingDialog.hide();
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_GAME_SEND_COMMENT);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
