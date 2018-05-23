package cn.lt.game.lib.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class ActivityManager {

    private List<Activity> mActivities;

    private boolean isUP = false;

    public boolean isUP() {
        return isUP;
    }

    public void setUP(boolean isUP) {
        this.isUP = isUP;
    }

    private static final class ActivityManagerHolder {
        private static final ActivityManager sInstance = new ActivityManager();
    }

    private ActivityManager() {
        mActivities = new ArrayList<Activity>();
    }

    public static ActivityManager self() {
        return ActivityManagerHolder.sInstance;
    }

    public void add(Activity activity) {
        final List<Activity> activities = mActivities;
        if (activity == null || activities.contains(activity)) {
            return;
        }

        activities.add(activity);
    }

    /**
     * 顶部Activity
     *
     * @return
     */
    public Activity topActivity() {
        if (mActivities != null && mActivities.size() > 0) {
            return mActivities.get(mActivities.size() - 1);
        }
        return null;
    }

    public void remove(Activity activity) {
        mActivities.remove(activity);
    }

    public void exitApp() {
        exitApp(null, true);
    }

    public void exitAppWithoutShutdown() {
        exitApp(null, false);
    }

    public void exitApp(Context context, boolean flag) {

        setUP(false);
        for (Activity activity : mActivities) {
            activity.finish();
        }
        if (flag) {
            System.exit(0);
        }
    }


    /**
     * 获取activity的跟布局
     *
     * @param context
     * @return
     */
    public static View getRootView(Activity context) {
        return ((ViewGroup) context.findViewById(android.R.id.content)).getChildAt(0);
    }

    /**
     * @param activity
     * @return > 0 success; <= 0 fail
     */
    public static int getStatusHeight(Activity activity) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass.getField("status_bar_height").get(localObject).toString());
                statusHeight = activity.getResources().getDimensionPixelSize(i5);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }
}
