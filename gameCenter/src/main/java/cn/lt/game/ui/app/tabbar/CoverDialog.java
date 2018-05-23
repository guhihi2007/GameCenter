package cn.lt.game.ui.app.tabbar;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.jakewharton.rxbinding.view.RxView;

import java.util.concurrent.TimeUnit;

import cn.lt.game.R;
import rx.functions.Action1;


/**
 * @author chengyong
 * @time 2017/11/2 14:03
 * @des ${TODO}
 */

public class CoverDialog extends Dialog {
    public CoverDialog(Context context) {
        super(context, R.style.CoverDialogStyle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_cover_layout);
        this.setCanceledOnTouchOutside(true);
        RxView.clicks(findViewById(R.id.cancel_btn))
                .throttleFirst(500, TimeUnit.SECONDS)
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        dismiss();
                    }
                });
    }
}
