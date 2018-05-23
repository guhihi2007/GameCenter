package cn.lt.game.ui.app.jump;

import android.content.Context;

import cn.lt.game.domain.essence.FunctionEssence;
import cn.lt.game.domain.essence.IdentifierType;
import cn.lt.game.event.JumpToContentTabEvent;
import de.greenrobot.event.EventBus;

/***
 * Created by JohnsonLin on 2017/6/14.
 */
public class HotTabJumper implements IJumper {

    @Override
    public void jump(Object o, Context context) {
        try {
            FunctionEssence data = (FunctionEssence) o;
            EventBus.getDefault().post(new JumpToContentTabEvent(data.getUniqueIdentifierBy(IdentifierType.URL)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
