package cn.lt.game.ui.app.gamestrategy;

import android.content.Context;

import com.huanju.data.HjDataClient;
import com.huanju.data.content.raw.info.HjBatchInfoItem;
import com.huanju.data.content.raw.listener.HjRequestBatchListListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.NotifyHasStrategyEvent;
import cn.lt.game.lib.util.log.Logger;
import cn.lt.game.model.GameBaseDetail;
import de.greenrobot.event.EventBus;

/**
 * Created by wenchao on 2015/11/23.
 */
public class HjSdk {

    private static HjSdk        instance;
    private        HjDataClient mClient;


    /**
     * MainActivity中初始化
     *
     * @param context
     */
    public void initOnCreateMainActivity(Context context) {
        mClient = HjDataClient.getInstance(context);
        mClient.onMainActivityCreate();
    }

    /**
     * 根据游戏列表请求攻略列表
     */
    public void requestBatchStrategyList(List<GameBaseDetail> gameBaseDetails) {
        if (gameBaseDetails == null || gameBaseDetails.size() == 0) {
            return;
        }
        int requestNum = gameBaseDetails.size() / 15;
        for (int i = 0; i < requestNum; i++) {
            List<GameBaseDetail> gameBaseDetailList = gameBaseDetails.subList(i * 15, (i + 1) * 15);
            requestBatchStrategyList(gameBaseDetailList, createPackageNames(gameBaseDetailList), 1000);
        }
        int surplus = gameBaseDetails.size() % 15;
        if (surplus > 0) {
            List<GameBaseDetail> gameBaseDetailList = gameBaseDetails.subList(requestNum * 15, requestNum * 15 + surplus);
            requestBatchStrategyList(gameBaseDetailList, createPackageNames(gameBaseDetailList), 1000);
        }
    }

    private ArrayList<String> createPackageNames(List<GameBaseDetail> gameBaseDetails) {
        ArrayList<String> packageNames = new ArrayList<>();
        for (GameBaseDetail gameBaseDetail : gameBaseDetails) {
            packageNames.add(gameBaseDetail.getPkgName());
        }
        return packageNames;
    }


    /**
     * 根据游戏列表请求攻略列表
     *
     * @param packageNames
     */
    private void requestBatchStrategyList(final List<GameBaseDetail> gameDetailList, ArrayList<String> packageNames, int pageSize) {
        mClient.requestBatchStrategyList(new HjRequestBatchListListener<HjBatchInfoItem>() {

            @Override
            public void onSuccess(ArrayList<HjBatchInfoItem> arrayList) {
                HashMap<String, GameBaseDetail> map = new HashMap<>();
                for (GameBaseDetail gameBaseDetail : gameDetailList) {
                    map.put(gameBaseDetail.getPkgName(), gameBaseDetail);
                }
                for (HjBatchInfoItem infoItem : arrayList) {
                    boolean hasStrategy = infoItem.getList() != null && infoItem.getList().size() > 0;
                    map.get(infoItem.getPackage_name()).setHasStrategy(hasStrategy);
                }

                List<GameBaseDetail> saveData = new ArrayList<>();
                for (String pkg : map.keySet()) {
                    saveData.add(map.get(pkg));
                }
                FileDownloaders.update(saveData);

                EventBus.getDefault().post(new NotifyHasStrategyEvent());
            }

            @Override
            public void onFailed(String s) {
                Logger.i("onFailed");
            }

            @Override
            public void onEmpty() {
                Logger.i("onEmpty");
            }
        }, packageNames, pageSize);
    }


    public static HjSdk getInstance() {
        if (instance == null) {
            synchronized (HjSdk.class) {
                if (instance == null) {
                    instance = new HjSdk();
                }
            }
        }
        return instance;
    }

    private HjSdk() {
    }


}
