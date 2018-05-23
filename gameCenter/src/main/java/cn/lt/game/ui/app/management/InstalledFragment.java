package cn.lt.game.ui.app.management;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.baidu.mobstat.StatService;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.base.BaseFragment;
import cn.lt.game.download.FileDownloaders;
import cn.lt.game.event.DownloadInitEvent;
import cn.lt.game.event.InstallEvent;
import cn.lt.game.event.UninstallEvent;
import cn.lt.game.event.UpgradeEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.ScreenUtils;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.OpenAppUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.view.NetWorkStateView;
import cn.lt.game.lib.view.NetWorkStateView.JumpIndexCallBack;
import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.ui.app.gamestrategy.HjSdk;
import de.greenrobot.event.EventBus;

/**
 * 玩游戏列表
 */
public class InstalledFragment extends BaseFragment implements JumpIndexCallBack {

    private Activity mActivity = null;
    private InstalledAdapter mAdapter;
    private NetWorkStateView netWorkStateView;
    private View mView = null;
    private ListView listView;

    private List<GameBaseDetail> installedGameList = new ArrayList<>();

    public InstalledFragment() {
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_MANGER_PALY);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        mView = inflater.inflate(R.layout.fragment_managemet_layout, container, false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        findView();
        return mView;
    }


    @Override
    public void onPause() {
        StatService.onPause(this);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        StatService.onResume(this);
        getData();
    }

    private void findView() {
        netWorkStateView = (NetWorkStateView) mView.findViewById(R.id.management_networkStateView);
        netWorkStateView.setNoDataLayoutText("没有安装任何游戏", "马上去下载");
        netWorkStateView.setIsfinish(false);
        netWorkStateView.setJumpIndexCallBack(this);

        listView = (ListView) mView.findViewById(R.id.management_listView);
        mAdapter = new InstalledAdapter(mActivity, installedGameList);
        listView.setAdapter(mAdapter);

        mAdapter.setOnItemMoreClickListener(new InstalledAdapter.OnItemMoreClickListener() {
            @Override
            public void onclick(int position, View view, final GameBaseDetail gameBaseDetail) {
                ItemPopupWindow popupWindow = new ItemPopupWindow(mActivity, new int[]{R.string.open});
                popupWindow.showAsDropDown(view, -(int) ScreenUtils.dpToPx(mActivity, 35), (int) ScreenUtils.dpToPx(mActivity, 8));

                popupWindow.setOnPopupItemClickListener(new ItemPopupWindow.OnPopupItemClickListener() {
                    @Override
                    public void onClick(int position) {
                        if(!OpenAppUtil.openApp(gameBaseDetail,mActivity,getPageAlias())) {
                            ToastUtils.showToast(mActivity, "打开失败");
                        }
                    }
                });
            }
        });
    }


    public void onEventMainThread(InstallEvent event) {
        LogUtils.e("InstallEvent");
        getData();
    }

    public void onEventMainThread(UpgradeEvent upgradeEvent) {
        LogUtils.e("AppAutoService --> upgradeEvent in InstalledFragment");
        getData();
    }

    public void onEventMainThread(UninstallEvent event) {
        LogUtils.e("UninstallEvent");
        for (int i = 0; i < installedGameList.size(); i++) {
            if (installedGameList.get(i).getPkgName().equals(event.packageName)) {
                installedGameList.remove(i);
                break;
            }
        }

        mAdapter.notifyDataSetChanged();
        initEmptyLayout();
    }

    public void onEventMainThread(DownloadInitEvent event) {
        getData();
        HjSdk.getInstance().requestBatchStrategyList(installedGameList);
    }

    private void getData() {
        installedGameList.clear();
        installedGameList.addAll(FileDownloaders.getAllInstalledDownFileInfo());
        mAdapter.notifyDataSetChanged();
        initEmptyLayout();
    }


    private void initEmptyLayout() {
        if (mAdapter.getCount() == 0) {
            netWorkStateView.showNetworkNoDataLayout();
            listView.setVisibility(View.GONE);
        } else {
            netWorkStateView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }

    @Override
    public void jump() {
//        ((HomeActivity)getActivity()).subCheck(TabEnum.INDEX);
        ActivityActionUtils.jumpToHomeActivityIndex(mActivity);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        LogUtils.i(LogTAG.HTAG,"InstalledFragment" + isVisibleToUser);
    }
}
