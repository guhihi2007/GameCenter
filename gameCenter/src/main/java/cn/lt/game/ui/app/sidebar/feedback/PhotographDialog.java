package cn.lt.game.ui.app.sidebar.feedback;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.ui.app.community.SendTools;

/**
 * Created by zhengweijian on 15/8/25.
 */
public class PhotographDialog extends Dialog implements View.OnClickListener {
    public static final int PHOTO_URL_RESULT = 10;
    public static final int TAKE_PICTURE = 11;
    public static Uri uri;
    private int height;
    private int width;
    private TextView gallery;
    private TextView photograph;
    private TextView cancel;

    public PhotographDialog(Context context) {
        super(context, R.style.updateInfoDialogStyle);
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        height = dm.heightPixels; // 高度设置为屏幕的0.6，根据实际情况调整
        width = dm.widthPixels;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pop_feedback);
        initView();
        initState(width, height);

    }

    private void initView() {
        gallery = (TextView) findViewById(R.id.feedback_pop_gallery);
        photograph = (TextView) findViewById(R.id.feedback_pop_photograph);
        cancel = (TextView) findViewById(R.id.feedback_pop_cancel);

        gallery.setOnClickListener(this);
        photograph.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    private void initState(int width, int height) {

        WindowManager.LayoutParams p = this.getWindow().getAttributes(); // 获取对话框当前的参数值
        p.width = width;
        p.height = height;
        this.getWindow().setAttributes(p);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.feedback_pop_gallery:
                Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                albumIntent.setDataAndType(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                scanForActivity(getContext()).startActivityForResult(albumIntent, PHOTO_URL_RESULT);
                dismiss();
                break;
            case R.id.feedback_pop_photograph:
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                uri = SendTools.getDateUri();
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                scanForActivity(getContext()).startActivityForResult(cameraIntent, TAKE_PICTURE);
                dismiss();
                break;
            case R.id.feedback_pop_cancel:
                dismiss();
                break;
            default:

        }
    }

    private static Activity scanForActivity(Context cont) {
        if (cont == null)
            return null;
        else if (cont instanceof Activity)
            return (Activity) cont;
        else if (cont instanceof ContextWrapper)
            return scanForActivity(((ContextWrapper) cont).getBaseContext());

        return null;
    }
}
