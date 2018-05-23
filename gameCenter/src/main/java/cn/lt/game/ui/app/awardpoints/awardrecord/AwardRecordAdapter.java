package cn.lt.game.ui.app.awardpoints.awardrecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.view.RxView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import cn.lt.game.R;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.SharedPreferencesUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.net.NetUtils;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.web.WebCallBackToStringForAward;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.ui.app.awardgame.bean.SendAwardBean;
import cn.lt.game.ui.app.awardgame.dialog.ChangeCodeDialog;
import cn.lt.game.ui.app.awardgame.dialog.FeedbackPhysicalDialog;
import cn.lt.game.ui.app.awardgame.dialog.PhysicalCheckDialog;
import cn.lt.game.ui.app.awardgame.dialog.PhysicalEditDialog;
import rx.functions.Action1;

import static cn.lt.game.ui.app.awardgame.view.AwardsPlateView.AWARDCOUPON;

/**
 * @author chengyong
 * @time 2017/6/12 11:27
 * @des ${中奖记录、过期奖品公用适配}
 */

public class AwardRecordAdapter extends BaseAdapter {
    public static final int VIEWTYPE_TIME = 0;// 时间条目
    public static final int VIEWTYPE_NORMAL = 1;//普通条目的类型
    private SharedPreferencesUtil mSp;
    List<AwardRecordBean> mRecordBeanList = new ArrayList<>();
    private Context context;
    public static final String status_wait_send = "1"; //1待发奖 , 2已发奖 , 3未领取,  5已过期 "
    public static final String status_yet_send = "2";
    public static final String status_wait_get = "3";
    public static final String status_time_out = "5";

    public static final String type_physical = "1";
    public static final String type_ticket = "2";
    public static final String type_4G = "3";
    public static final String type_score = "4";
    public static final String type_gift = "5";
    public static final String type_coupon = "6";

    //奖品类型：实物 1，券 2，流量 3，积分 4，礼包 5",代金券 6

    public AwardRecordAdapter(List<AwardRecordBean> recordBeanList, Context context) {
        this.mRecordBeanList = recordBeanList;
        this.context = context;
        if(mSp==null){
            mSp = new SharedPreferencesUtil(context);
        }
    }

    public void setData(List<AwardRecordBean> recordBeanList){
        this.mRecordBeanList = recordBeanList;
    }

    @Override
    public int getCount() {
        return mRecordBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mRecordBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        TimeViewHolder timeViewHolder = null;
        final NormalViewHolder normalViewHolder;
        if (getItemViewType(position) == VIEWTYPE_TIME) {
            if (convertView == null) {
                timeViewHolder = new TimeViewHolder();
                View timeView = LayoutInflater.from(context).inflate(R.layout.item_award_record_time, null);
                timeViewHolder.mTime = (TextView) timeView.findViewById(R.id.tv_award_time);
                timeViewHolder.container = (LinearLayout) timeView.findViewById(R.id.award_time_container);
                timeView.setTag(timeViewHolder);
                convertView = timeView;
            }else{
                timeViewHolder=(TimeViewHolder)convertView.getTag();
            }
            timeViewHolder.mTime.setText(mRecordBeanList.get(position).dataTime);
            if(position!=0){
                timeViewHolder.container.setPadding(0,DensityUtil.dip2px(context,-8),0,0);
            }else{
                timeViewHolder.container.setPadding(0,0,0,0);
            }
        }else{
            if (convertView == null) {
                normalViewHolder = new NormalViewHolder();
                View normalView = LayoutInflater.from(context).inflate(R.layout.item_award_record_normal, null);
                normalViewHolder.awardRecordRoot = (RelativeLayout) normalView.findViewById(R.id.awardRecordRoot);
                normalViewHolder.mIcon = (ImageView) normalView.findViewById(R.id.award_listView_item_img);
                normalViewHolder.mTitle = (TextView) normalView.findViewById(R.id.award_item_txName);
                normalViewHolder.mDetailTitle = (TextView) normalView.findViewById(R.id.award_item_detail_tv);
                normalViewHolder.mExpiry = (TextView) normalView.findViewById(R.id.award_listView_item_time);
                normalViewHolder.mStatus = (Button) normalView.findViewById(R.id.lv_award_record_item_status);
                normalView.setTag(normalViewHolder);
                convertView = normalView;
            }else{
                normalViewHolder=(NormalViewHolder)convertView.getTag();
            }
            Glide.with(context).load(mRecordBeanList.get(position).prize_pic).crossFade().placeholder(R.mipmap.img_default_80x80_round).into(normalViewHolder.mIcon);
            normalViewHolder.mTitle.setText(mRecordBeanList.get(position).prize_name);
            normalViewHolder.mDetailTitle.setText(mRecordBeanList.get(position).prize_word);
            normalViewHolder.mExpiry.setText(mRecordBeanList.get(position).valid_date);
            setButtonTextAndColor(normalViewHolder,mRecordBeanList.get(position));
            RxView.clicks(normalViewHolder.mStatus)
                    .throttleFirst(500 , TimeUnit.MILLISECONDS )   //0.5秒钟之内只取一个点击事件，防抖操作
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            LogUtils.i(LogTAG.CHOU, "奖品按钮，点击了");
                            if (checkNetIsConnected()) return;
                            if(mRecordBeanList.get(position).prize_status.equals(status_wait_get)){// 3未领取"
                                showPhysicalEditDialog(mRecordBeanList.get(position));
                            }else if(mRecordBeanList.get(position).prize_status.equals(status_yet_send)
                                    ||mRecordBeanList.get(position).prize_status.equals(status_wait_send)){//1待发奖 , 2已发奖
                                getSendAwardFromNet(mRecordBeanList.get(position));
                            }
                        }
                    }) ;

            // 整个卡片全区域都能触发按钮
            normalViewHolder.awardRecordRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    normalViewHolder.mStatus.performClick();
                }
            });
        }
        return convertView;
    }

    private boolean checkNetIsConnected() {
        if (!NetUtils.isConnected(context)) {
            final MessageDialog messageDialog = new MessageDialog(context, context.getResources().getString(R.string.gentle_reminder), context.getResources().getString(R.string.download_no_network), context.getResources().getString(R.string.cancel_ignor_bt), context.getResources().getString(R.string.go_setting));
            messageDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                @Override
                public void OnClick(View view) {
                    //跳转到系统设置
                    context.startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            messageDialog.show();
            return true;
        }
        return false;
    }

    /**
     * 实物，流量领奖弹框
     */
    public void showPhysicalEditDialog(final AwardRecordBean awardRecordBean) {
        final PhysicalEditDialog dialog = new PhysicalEditDialog(context, awardRecordBean.prize_style.equals(type_physical));
        dialog.setRightOnClickListener(new PhysicalEditDialog.RightBtnClickListener() {
            @Override
            public void OnClick(View view) {
                view.setClickable(false);
                ((Button)view).setText("提交中");
                getAwardFromNet(awardRecordBean, dialog,view);
                LogUtils.i(LogTAG.CHOU, "实物领奖框：点击提交了");
            }
        });
        dialog.show();
    }
    /**
     * 券的反馈信息
     * @param sendAwardBean
     */
    public void showFeedBackCodeDialog(final SendAwardBean sendAwardBean) {
        final ChangeCodeDialog dialog = new ChangeCodeDialog(context,sendAwardBean.exchange_code,sendAwardBean.prize_word);
        dialog.show();
    }
    /**
     * 实物的反馈信息-地址、物流公司等
     * @param sendAwardBean
     */
    public void showFeedBackPhysicalDialog(final SendAwardBean sendAwardBean) {
        new FeedbackPhysicalDialog(context)
                .setmAddressText(sendAwardBean.address)
                .setmCompanyText(sendAwardBean.express_company)
                .setmExpressNumText(sendAwardBean.express_number)
                .setmNumText(sendAwardBean.phone)
                .setMusernameText(sendAwardBean.name)
                .show();
    }

    /**
     * 领奖
     */
    public void getAwardFromNet(final AwardRecordBean awardRecordBean, final PhysicalEditDialog dialog, final View view) {
        final Map<String, String> params = new HashMap<>();
        params.put("name", dialog.getEditName());
        params.put("phone", dialog.getEditPhone());
        params.put("address", dialog.getEditAddress());
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.AWARD_GET + awardRecordBean.id, params, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i(LogTAG.CHOU, "领奖成功：" + result);
                ToastUtils.showToast(context, "领奖成功");
                saveData(dialog.getEditAddress(),dialog.getEditName(),dialog.getEditPhone());
                dialog.hide();
//                awardRecordBean.prize_status= status_wait_send; 产品不确定
                if(awardRecordBean.prize_style.equals(type_4G)){
                    awardRecordBean.prize_status= status_yet_send;
                }else{
                    awardRecordBean.prize_status= status_wait_send;
                }
                view.setClickable(true);
                ((Button)view).setText("提交");
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                view.setClickable(true);
                ((Button)view).setText("提交");
                LogUtils.i(LogTAG.CHOU, "领奖失败：" + statusCode + "====" + error);
                showToast(error);
            }
        });
    }

    private void showToast(Throwable error) {
        ToastUtils.showToast(context, error.getMessage().contains("网络连接失败")?"网络连接失败":error.getMessage());
    }

    /**
     * 存本地
     * @param editAddress
     * @param editName
     * @param editPhone
     */
    private void saveData(String editAddress, String editName, String editPhone) {
        mSp.add(SharedPreferencesUtil.AWARD_EDIT_ADDRESS,editAddress);
        mSp.add(SharedPreferencesUtil.AWARD_EDIT_NAME, editName);
        mSp.add(SharedPreferencesUtil.AWARD_EDIT_PHONE, editPhone);
        LogUtils.i(LogTAG.CHOU, "领奖信息保存成功："+editAddress+editName+editPhone);
    }
    /**
     * 发奖信息==奖品详情
     * @param bean
     */
    public void getSendAwardFromNet(final AwardRecordBean bean) {
        if(bean.prize_style.equals(type_score)){ //积分 信息弹窗
//            ToastUtils.showToast(context, "奖品已发放");
            return;
        }
        Net.instance().executeGet(Host.HostType.GCENTER_HOST, Uri2.AWARD_SEND + bean.id, null,
                new WebCallBackToStringForAward() {
            @Override
            public void onSuccess(String result) {
                SendAwardBean sendAwardBean = new Gson().fromJson(result, SendAwardBean.class);
                LogUtils.i(LogTAG.CHOU, "发奖信息成功：" + sendAwardBean);//券信息、实物物流信息
                if (bean.prize_status.equals(status_yet_send)) {
                    if (bean.prize_style.equals(type_ticket) || bean.prize_style.equals(type_gift)) {
                        showFeedBackCodeDialog(sendAwardBean);//券、礼包 信息弹窗
                    } else if(bean.prize_style.equals(type_physical)){//实物物流信息弹窗
                        showFeedBackPhysicalDialog(sendAwardBean);
                    }else if(bean.prize_style.equals(type_4G)){//流量信息弹窗
                        new PhysicalCheckDialog(context,sendAwardBean.address,sendAwardBean.name,sendAwardBean.phone,true).show();
                    }else if (bean.prize_style.equals(type_coupon)){
                        sendAwardBean.exchange_code=AWARDCOUPON;
                        showFeedBackCodeDialog(sendAwardBean);//券、礼包 信息弹窗
                    }
                } else if (bean.prize_status.equals(status_wait_send) ) {//待发奖、实物。
                    if(bean.prize_style.equals(type_physical)){
                        new PhysicalCheckDialog(context,sendAwardBean.address,sendAwardBean.name,sendAwardBean.phone,false).show();
                    }else if(bean.prize_style.equals(type_4G)){//流量信息弹窗
                        new PhysicalCheckDialog(context,sendAwardBean.address,sendAwardBean.name,sendAwardBean.phone,true).show();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                LogUtils.i(LogTAG.CHOU, "发奖信息失败：" + statusCode + "====" + error);
                showToast(error);
            }
        });
    }

    private void setButtonTextAndColor(NormalViewHolder normalViewHolder, AwardRecordBean bean) {
        switch (bean.prize_status){
            case status_wait_send:
                normalViewHolder.mStatus.setText("待发奖");
                normalViewHolder.mStatus.setTextColor(Color.parseColor("#86bf13")); //@drawable/btn_green_selector
                normalViewHolder.mStatus.setBackgroundResource(R.drawable.btn_green_selector);
                break;
            case status_yet_send:
                normalViewHolder.mStatus.setText("已发奖");
                normalViewHolder.mStatus.setTextColor(Color.parseColor("#86bf13")); //@drawable/btn_green_selector
                normalViewHolder.mStatus.setBackgroundResource(R.drawable.btn_green_selector);
                break;
            case status_wait_get:
                normalViewHolder.mStatus.setText("领取");
                normalViewHolder.mStatus.setTextColor(Color.parseColor("#ff8800")); //@drawable/btn_green_selector
                normalViewHolder.mStatus.setBackgroundResource(R.drawable.btn_orangejuice_selector);
                break;
            case status_time_out:
                normalViewHolder.mStatus.setText("已过期");
                normalViewHolder.mStatus.setTextColor(Color.parseColor("#ffffff")); //@drawable/btn_green_selector
                normalViewHolder.mStatus.setBackgroundResource(R.drawable.btn_gray_selector);
                break;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    /**
     * @param position
     * @return
     * @des 决定普通条目的ViewType类型
     */
    @Override
    public int getItemViewType(int position) {
        AwardRecordBean awardRecordBean = mRecordBeanList.get(position);
        if (awardRecordBean.isTime) {
            return VIEWTYPE_TIME;
        } else {
            return VIEWTYPE_NORMAL;
        }
    }

    private final class NormalViewHolder {
        public RelativeLayout awardRecordRoot;
        public TextView mTitle;
        public TextView mDetailTitle;
        public TextView mExpiry;
        public ImageView mIcon;
        public Button mStatus;

    }

    private final class TimeViewHolder {
        public TextView mTime ;
        public LinearLayout container ;
    }
}
