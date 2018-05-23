package cn.lt.game.ui.app.voucher;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.lt.game.R;
import cn.lt.game.bean.ExchangeBean;
import cn.lt.game.bean.ExchangeVoucherItemBean;
import cn.lt.game.event.ExchangeVoucherEvent;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.DensityUtil;
import cn.lt.game.lib.util.GsonUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.net.Host;
import cn.lt.game.net.Net;
import cn.lt.game.net.Uri2;
import cn.lt.game.statistics.ReportEvent;
import cn.lt.game.statistics.StatisticsEventData;
import cn.lt.game.statistics.manger.DCStat;
import de.greenrobot.event.EventBus;

/**
 * Created by Erosion on 2018/1/16.
 */

public class ExchangeVoucherAdapter extends BaseAdapter {
    private List<ExchangeVoucherItemBean> beans = new ArrayList<>();
    private Context context;
    private LayoutInflater mInflater;

    public ExchangeVoucherAdapter (Context context) {
        mInflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setList(List<ExchangeVoucherItemBean> beans) {
        this.beans = beans;
    }

    @Override
    public int getCount() {
        return beans.size();
    }

    @Override
    public Object getItem(int position) {
        return beans.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.exchange_voucher_item,null);
            holder.icon = (ImageView) convertView.findViewById(R.id.icon);
            holder.voucherName = (TextView) convertView.findViewById(R.id.voucher_name);
            holder.minMoney = (TextView) convertView.findViewById(R.id.max_money);
            holder.requiredPoint = (TextView) convertView.findViewById(R.id.required_point);
            holder.exchangeButton = (Button) convertView.findViewById(R.id.exchange_item_button);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relative);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final ExchangeVoucherItemBean bean = beans.get(position);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.relativeLayout.getLayoutParams();
        if (position == beans.size() - 1) {
            layoutParams.setMargins(0, 0,0, DensityUtil.dip2px(context,8));
        } else {
            layoutParams.setMargins(0, 0,0,DensityUtil.dip2px(context,0));
        }

        ImageloaderUtil.loadRoundImage(context,bean.getGame_icon(),holder.icon);
        holder.voucherName.setText(bean.getName());
        if (bean.getMin_money()== 0) {
            holder.minMoney.setText("无门槛使用");
        } else {
            holder.minMoney.setText("充值满" + bean.getMin_money() + "元可用");
        }
        holder.requiredPoint.setText(bean.getPoint() + "");

        if (bean.getRemainder() <= 0) {
            holder.exchangeButton.setEnabled(false);
            holder.exchangeButton.setText("已领完");
            holder.exchangeButton.setTextColor(Color.parseColor("#ffffff"));
            holder.exchangeButton.setBackgroundResource(R.drawable.exchange_voucher_button);
        } else {
            holder.exchangeButton.setEnabled(true);
            holder.exchangeButton.setText("兑换");
            holder.exchangeButton.setTextColor(Color.parseColor("#86bf13"));
            holder.exchangeButton.setBackgroundResource(R.drawable.btn_green_selector);
        }

        holder.exchangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exchangeVoucher(bean.getId(),bean.getName());
                DCStat.clickEvent(new StatisticsEventData(ReportEvent.ACTION_CLICK, Constant.PAGE_EXCHANGE_VOUCHER,position + 1,"voucher",1,bean.getVoucher_id() + "",null,"","","",""));
            }
        });
        return convertView;
    }

    private void exchangeVoucher(int exchangeId, final String voucherName) {
        Map<String,String> params = new HashMap<>();
        params.put("exchange_id",String.valueOf(exchangeId));
        Net.instance().executePost(Host.HostType.GCENTER_HOST, Uri2.EXCHANGE_VOUCHER, params, new WebCallBackToString() {
            @Override
            public void onSuccess(String result) {
                LogUtils.i("Erosion","result ==== " + result.toString());
                ExchangeBean bean = GsonUtil.GsonToBean(result,ExchangeBean.class);
                LogUtils.i("Erosion","code:" + bean.getCode());
                switch (bean.getCode()) {
                    case 0:
                        String str = "兑换成功！恭喜获得“" + voucherName + "”一张，您可以在代金券列表中查看。";
                        Spannable spannable = new SpannableString(str);
                        spannable.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.hot_tuijian_top)),10,10 + voucherName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        final MessageDialog exchangeSuccess = new MessageDialog(context,"兑换成功",str,"确认",spannable);
                        exchangeSuccess.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                            @Override
                            public void OnClick(View view) {
                                exchangeSuccess.dismiss();
                                EventBus.getDefault().post(new ExchangeVoucherEvent(true,true));
                            }
                        });
                        exchangeSuccess.show();
                        break;
                    case 1:
                    case 3:
                        String message1 = "这张代金券已经被抢光啦。您可以查看其它代金券。";
                        Spannable spannable1 = new SpannableString(message1);
                        final MessageDialog dialog = new MessageDialog(context,"兑换失败",message1,"确认",spannable1);
                        dialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                            @Override
                            public void OnClick(View view) {
                                dialog.dismiss();
                                EventBus.getDefault().post(new ExchangeVoucherEvent(true));
                            }
                        });
                        dialog.show();
                        break;
                    case 2:
                        String message2 = "您的可用积分不够。可前往“我->我的任务”页面获取更多积分。";
                        Spannable spannable2 = new SpannableString(message2);
                        final MessageDialog dialog1 = new MessageDialog(context,"兑换失败",message2,"确认",spannable2);
                        dialog1.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {
                            @Override
                            public void OnClick(View view) {
                                dialog1.dismiss();
                                EventBus.getDefault().post(new ExchangeVoucherEvent(true));
                            }
                        });
                        dialog1.show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(int statusCode, Throwable error) {
                ToastUtils.showToast(context,"网络异常");
                LogUtils.i("Erosion","onFailure=====" + statusCode + ",error===" + error.getMessage().toString());
            }
        });

    }

    class ViewHolder{
        ImageView icon;
        TextView voucherName;
        TextView minMoney;
        TextView requiredPoint;
        Button exchangeButton;
        RelativeLayout relativeLayout;
    }
}
