package cn.lt.game.ui.app.jump;

import android.content.Context;
import android.content.Intent;

import cn.lt.game.event.JumpToContentTabEvent;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.model.EntryPages;
import cn.lt.game.model.PageDetail;
import de.greenrobot.event.EventBus;

/***
 * Created by Administrator on 2015/12/14.
 */
public class PageJumper implements IJumper {
    @Override
    public void jump(Object o, Context context) {
        try {
            PageDetail page = (PageDetail) o;

            if (EntryPages.hot_tab.equals(page.desc)) {
                EventBus.getDefault().post(new JumpToContentTabEvent(page.value));
                return;
            }

            Class<?> tempClass = page.activityClass;
            if (tempClass != null) {
                Intent intent = new Intent(context, tempClass);
                if (page.needParam) {
                    intent.putExtra(page.key, page.value);
                    intent.putExtra(page.key2, page.value2);
                }
                context.startActivity(intent);
            } else {
                ToastUtils.showToast(context, "要跳转的页面不存在！");
            }
        } catch (Exception e) {
            LogUtils.i("GOOD", "跳转异常");
            e.printStackTrace();
        }
    }
}
