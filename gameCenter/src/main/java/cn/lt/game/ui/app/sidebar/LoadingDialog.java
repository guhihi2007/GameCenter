package cn.lt.game.ui.app.sidebar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import cn.lt.game.R;

public class LoadingDialog extends Dialog {
    private TextView tv;
    private String msg;
  
    public LoadingDialog(Context context,String msg) {
        super(context, R.style.loadingDialogStyle);
        this.msg = msg;
    }
    public LoadingDialog(Context context) {
        super(context, R.style.loadingDialogStyle);
        this.msg = "正在提交...";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);
        tv = (TextView)this.findViewById(R.id.tv);
        tv.setText(msg);
        setCanceledOnTouchOutside(false);
    }

	public TextView getTv() {
		return tv;
	}
}
