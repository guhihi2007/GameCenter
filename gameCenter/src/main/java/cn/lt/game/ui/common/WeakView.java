package cn.lt.game.ui.common;

import android.view.View;

import java.lang.ref.WeakReference;

import cn.lt.game.event.DownloadUpdateEvent;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/3/18.
 */
public abstract class WeakView<T extends View> extends WeakReference<T> {

    public WeakView(T r) {
        super(r);
        EventBus.getDefault().register(this);
    }

    public abstract void onEventMainThread(DownloadUpdateEvent updateEvent);
}