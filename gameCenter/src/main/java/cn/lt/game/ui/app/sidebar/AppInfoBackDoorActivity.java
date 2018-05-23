package cn.lt.game.ui.app.sidebar;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.BuildConfig;
import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.ActivityActionUtils;
import cn.lt.game.lib.util.CodeChangeUtil;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.net.Host;
import cn.lt.game.service.NoticeService;

public class AppInfoBackDoorActivity extends BaseActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private String[] nameArr = {"BaiduMobAd_STAT_ID",
            "BaiduMobAd_CHANNEL", "PUSH_APPID", "DATA_EYE_APP_ID", "PUSH_APPKEY", "PUSH_APPSECRET"};
    public static String payloadId = "";
    public static String GeTuipushCID;
    public static String XiaoMiRegistId = "";
    public static String IgexinPushReceiverAction = "";

    private static final String TAG = "infoBackDoor";
    private List<AppInfoBean> infoList;
    //    private String[] cityList={"默认","上海市","北京市","深圳市","广州市","晋江市","长沙市","武汉市"};
    private List<String> cityList = new ArrayList<>();
    private ListView lv_appInfo;
    private PopupWindow mPopupWindow;
    private ListView contentView;
    private MyAdapter mMyAdapter;
    private Button mSelectCity, mBtnRequest;
    private EditText mEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info_back_door);
        initView();
        getInfo();
        infoList.add(new AppInfoBean("Click Me Jump To AllDownFileActivity!!", ""));
        infoList.add(new AppInfoBean("OpenLog", ""));
        infoList.add(new AppInfoBean("CloseLog", ""));

        infoList.add(new AppInfoBean("open_exposure", ""));
        infoList.add(new AppInfoBean("close_exposure", ""));
        setData();
//"默认","上海市","北京市","深圳市","广州市","晋江市","长沙市","武汉市"
        cityList.add("无");
        cityList.add("上海市");
        cityList.add("北京市");
        cityList.add("深圳市");
        cityList.add("广州市");
        cityList.add("晋江市");
        cityList.add("长沙市");
        cityList.add("武汉市");
        cityList.add("郑州市 ");
        cityList.add("无锡市 ");
        cityList.add("绍兴市 ");
        cityList.add("太原市  ");

    }


    private void initView() {
        lv_appInfo = (ListView) findViewById(R.id.lv_appInfo);
        mSelectCity = (Button) findViewById(R.id.select_city);
        mEt = (EditText) findViewById(R.id.et_id);
        mBtnRequest = (Button) findViewById(R.id.bt_request);
        mSelectCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCity();
            }
        });
        mBtnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pushId = mEt.getText().toString();
                if (TextUtils.isEmpty(pushId) || !BuildConfig.DEBUG) {
                    ToastUtils.showToast(AppInfoBackDoorActivity.this, "！！");
                    return;
                }
                AppInfoBackDoorActivity.this.startService(NoticeService.getIntent(AppInfoBackDoorActivity.this, Integer.parseInt(pushId)));
            }
        });
    }

    /**
     * 弹出下拉选择框
     *
     * @param
     */
    private void showCity() {
        if (mPopupWindow == null) {
            int width = DensityUtil.dip2px(this, 80);
            int height = DensityUtil.dip2px(this, 200);
            mPopupWindow = new PopupWindow(width, height);
            contentView = new ListView(this);
            mMyAdapter = new MyAdapter(cityList);
            contentView.setAdapter(mMyAdapter);
            contentView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                private String text;

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    text = cityList.get(position);
                    ToastUtils.showToast(AppInfoBackDoorActivity.this, text);
                    mSelectCity.setText(text);
                    String unicode_city = CodeChangeUtil.stringToUnicode(text);
                    MyApplication.application.city = unicode_city;
                    if (position == 0) MyApplication.application.city = "";
                    mPopupWindow.dismiss();
                }
            });
            mPopupWindow.setContentView(contentView);
            mPopupWindow.setOutsideTouchable(true);
            mPopupWindow.setBackgroundDrawable(new ColorDrawable());
            mPopupWindow.setFocusable(true);
        }
        mPopupWindow.showAsDropDown(mSelectCity);
    }

    private class MyAdapter extends BaseAdapter {
        List<String> cityList;

        public MyAdapter(List<String> cityList) {
            this.cityList = cityList;
        }

        @Override
        public int getCount() {
            return cityList.size();
        }

        @Override
        public Object getItem(int position) {
            return cityList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(AppInfoBackDoorActivity.this).inflate(R.layout.item_list_city, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.cityId.setText(cityList.get(position));
            return convertView;
        }
    }

    private class ViewHolder {
        public ViewHolder(View v) {
            cityId = (TextView) v.findViewById(R.id.cityId);
        }

        TextView cityId;
    }

    /********************************************************/

    private void setData() {
        AppInfoAdapter adapter = new AppInfoAdapter(this, infoList);
        lv_appInfo.setAdapter(adapter);
        lv_appInfo.setOnItemClickListener(this);
        lv_appInfo.setOnItemLongClickListener(this);
    }

    private void getInfo() {
        getMetaData();
        getReceiverData();
        getHostInfo();
        getVersionInfo();
        getPushInfo();
        Log.i(TAG, infoList.toString());
    }

    private void getHostInfo() {
        infoList.add(new AppInfoBean("BaseHost", Host.getHost()));
    }

    private void getReceiverData() {
        infoList.add(new AppInfoBean("IgexinPush_ReceiverAction", IgexinPushReceiverAction));
    }


    private void getPushInfo() {
        infoList.add(new AppInfoBean("GameCenter_CHANNEL", Constant.CHANNEL));
        infoList.add(new AppInfoBean("PAY_LOAD_ID", payloadId));
        infoList.add(new AppInfoBean("GeTui_PUSH_CID", GeTuipushCID));
        infoList.add(new AppInfoBean("XiaoMi_PUSH_Regist_ID", XiaoMiRegistId));
        infoList.add(new AppInfoBean("GDT_ID", Constant.APPID));
        infoList.add(new AppInfoBean("GDT_SPLASH_ID", Constant.SplashPosID));

    }


    private void getMetaData() {
        infoList = new ArrayList<AppInfoBean>();

        String value = "";
        for (int i = 0; i < nameArr.length; i++) {
            value = getMetaDataByName(nameArr[i]);
            infoList.add(new AppInfoBean(nameArr[i], value));
        }
    }

    private String getMetaDataByName(String name) {
        String msg = "";
        try {
            ApplicationInfo appInfo = this.getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            msg = appInfo.metaData.getString(name);
            if (TextUtils.isEmpty(msg)) {
                int i = appInfo.metaData.getInt(name, 0);
                msg = String.valueOf(i);
            }

        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }

        return msg;
    }

    private void getVersionInfo() {
        try {
            PackageManager pm = getPackageManager();

            PackageInfo pinfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
            infoList.add(new AppInfoBean("versionName", pinfo.versionName));
            infoList.add(new AppInfoBean("versionCode", pinfo.versionCode + ""));
            infoList.add(new AppInfoBean("buildTime", getString(R.string.build_time)));
            infoList.add(new AppInfoBean("IMEI", MyApplication.imei));

        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPageAlias() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfoBean info = infoList.get(position);

        if (info.getName().equals("Click Me Jump To AllDownFileActivity!!")) {
            ActivityActionUtils.activity_jump(this, AllDownloadFileActivity.class);
        } else if (info.getName().equals("OpenLog")) {
            cn.lt.game.lib.util.LogUtils.mDebuggable = LogUtils.LEVEL_ALL;
            Toast.makeText(AppInfoBackDoorActivity.this, "日志已开启", Toast.LENGTH_SHORT).show();
        } else if (info.getName().equals("CloseLog")) {
            cn.lt.game.lib.util.LogUtils.mDebuggable = LogUtils.LEVEL_OFF;
            Toast.makeText(AppInfoBackDoorActivity.this, "日志已关闭", Toast.LENGTH_SHORT).show();
        } else if (info.getName().equals("open_exposure")) {
            Toast.makeText(AppInfoBackDoorActivity.this, "曝光开启", Toast.LENGTH_SHORT).show();
            Constant.EXPOSURE_TOGGLE = true;
        } else if (info.getName().equals("close_exposure")) {
            Toast.makeText(AppInfoBackDoorActivity.this, "曝光关闭", Toast.LENGTH_SHORT).show();
            Constant.EXPOSURE_TOGGLE = false;
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        AppInfoBean bean = infoList.get(position);
        if (bean != null) {
            String name = bean.getName();
            if (name.equals("XiaoMi_PUSH_Regist_ID") || name.equals("GeTui_PUSH_CID") || name.equals("IMEI")) {
                String value = bean.getValue();
                if (null != value && !"".equals(value)) {

                    // 得到剪贴板管理器
                    ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    cmb.setText(bean.getValue().trim());// 复制到剪贴板
                    if (cmb.getText().equals(bean.getValue().trim())) {
                        ToastUtils.showToast(AppInfoBackDoorActivity.this, "已复制到剪贴板");
                    } else {
                        ToastUtils.showToast(AppInfoBackDoorActivity.this, "复制失败，请重试");
                    }
                }
            }
        }

        return false;
    }

    private class AppInfoAdapter extends BaseAdapter {

        private Context context;
        private LayoutInflater inflater;
        private List<AppInfoBean> list;

        public AppInfoAdapter(Context context, List<AppInfoBean> list) {
            this.context = context;
            this.inflater = LayoutInflater.from(context);
            if (list == null) {
                list = new ArrayList<AppInfoBean>();
            } else {
                this.list = list;
            }
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder vh = null;

            if (convertView == null) {
                vh = new ViewHolder();
                convertView = inflater.inflate(R.layout.app_info_item, null);
                vh.tv_appInfoName = (TextView) convertView.findViewById(R.id.tv_appInfoName);
                vh.tv_appInfoValue = (TextView) convertView.findViewById(R.id.tv_appInfoValue);
                convertView.setTag(vh);
            } else {
                vh = (ViewHolder) convertView.getTag();
            }

            AppInfoBean bean = list.get(position);
            vh.tv_appInfoName.setText(bean.getName());
            if (null == bean.getValue() || "".equals(bean.getValue())) {
                vh.tv_appInfoValue.setText("");
            } else {
                vh.tv_appInfoValue.setText(bean.getValue());
            }

            return convertView;
        }

        private class ViewHolder {
            TextView tv_appInfoName;
            TextView tv_appInfoValue;
        }

    }

    private void showRequestPushDialog() {
//        try {
//
//            final EditText editText = new EditText(this);
//            AlertDialog.Builder inputDialog =
//                    new AlertDialog.Builder(this);
//            inputDialog.setTitle("push~ID").setView(editText);
//            inputDialog.setPositiveButton("确定",
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                            String pushId = editText.getText().toString();
//                            if (TextUtils.isEmpty(pushId) || !BuildConfig.DEBUG) {
//                                ToastUtils.showToast(AppInfoBackDoorActivity.this, "！！");
//                                return;
//                            }
//
//                            AppInfoBackDoorActivity.this.startService(NoticeService.getIntent(AppInfoBackDoorActivity.this, Integer.parseInt(pushId)));
//
//                        }
//                    }).show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

    }

}
