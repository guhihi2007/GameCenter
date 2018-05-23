package cn.lt.game.ui.app.personalcenter.info;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import cn.lt.game.R;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.EditTextWithLabel;
import cn.lt.game.lib.widget.time.JudgeDate;
import cn.lt.game.lib.widget.time.ScreenInfo;
import cn.lt.game.lib.widget.time.WheelAreaMain;
import cn.lt.game.lib.widget.time.WheelTimeMain;
import cn.lt.game.ui.app.personalcenter.BaseFragment;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.PersonalCenterActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;


public class PersonalInfoFragment extends BaseFragment implements OnClickListener,UserInfoUpdateListening{
	private ImageView headImg;
	private EditTextWithLabel nickname;
	private RadioButton maleBtn;
	private RadioButton femaleBtn;
	private TextView birthday;
	private TextView area;

	private Bitmap bitmap;
	private UserBaseInfo user;
	private WebCallBackToObj<UserBaseInfo> updateInfoCallBack;
	
	private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
	private WheelTimeMain wheelTimeMain;
	private WheelAreaMain wheelAreaMain;
	
	@Override
	protected ActionBarSetting getActionBar() {
		ActionBarSetting bar = new ActionBarSetting();
		bar.tvTitleText = R.string.personal_info;
		bar.btnNextText = R.string.finish;
		bar.btnNextBackground = R.drawable.btn_dark_green_selector;
		bar.btnNextClickListener = this;
		return bar;
	}

	@Override
	protected int getFragmentLayoutRes() {
		return R.layout.fragment_personal_info;
	}

	@Override
	protected void initView() {
		headImg = (ImageView) view.findViewById(R.id.head_img);
		nickname = (EditTextWithLabel)view.findViewById(R.id.set_nickname);
		maleBtn = (RadioButton)view.findViewById(R.id.radioMale);
		femaleBtn = (RadioButton)view.findViewById(R.id.radioFemale);
		birthday = (TextView)view.findViewById(R.id.set_birthday);
		birthday.setInputType(InputType.TYPE_NULL);
		birthday.setOnClickListener(this);
		area = (TextView)view.findViewById(R.id.set_area);
		area.setOnClickListener(this);
		
		headImg.setOnClickListener(this);
		
		updateInfoCallBack = new  WebCallBackToObj<UserBaseInfo>() {
			
			@Override
			public void onFailure(int statusCode, Throwable error) {
				System.out.println(error.getMessage());
				ToastUtils.showToast(getActivity(), error.getMessage());
				hideLoadingDialog();
			}
			
			@Override
			protected void handle(UserBaseInfo info) {
				UserInfoManager.instance().setUserBaseInfo(info, false);
				ToastUtils.showToast(getActivity(), "修改成功");
				hideLoadingDialog();
				((PersonalCenterActivity)getActivity()).onBack();
			}
		};
		
		UserInfoManager.instance().addListening(this);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		showLoadingDialog("正在获取详细信息");
		PCNet.fetchUserInfo(new WebCallBackToObj<UserBaseInfo>() {
			
			@Override
			protected void handle(UserBaseInfo info) {
				UserInfoManager.instance().setUserBaseInfo(info, false);
				hideLoadingDialog();
			}
			
			@Override
			public void onFailure(int statusCode, Throwable error) {
				ToastUtils.showToast(getActivity().getApplicationContext(), error.getMessage());
//				setUserBaseInfo(UserInfoManager.instance().getUserInfo());
				hideLoadingDialog();
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	protected void findView() {
		
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
			if (data != null && requestCode == 10) {
				ArrayList<ImageItem> images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);

				if (images != null && images.size() > 0) {
					loadingDialog.show();
					ImageItem imageItem = images.get(0);
					bitmap = BitmapFactory.decodeFile(imageItem.path);
					headImg.setImageBitmap(bitmap);
				} else {
					ToastUtils.showToast(mActivity, "没有数据1");
				}
			} else {
				ToastUtils.showToast(mActivity, "没有数据2");
			}

			return;
		}


//		if (resultCode == Activity.RESULT_OK) {
//			if(requestCode==1){
//				//相册返回
//				bitmap = data.getParcelableExtra("data");
//				if (bitmap == null&&data.getData()!=null) {
//					Intent intent = new Intent("com.android.camera.action.CROP");
//					intent.setType("image/*");
//					intent.setDataAndType(data.getData(), "image/jpeg");
//					intent.putExtra("crop", "true");
//					intent.putExtra("aspectX", 1);
//					intent.putExtra("aspectY", 1);
//					// outputX outputY 是裁剪图片宽高
//					intent.putExtra("outputX", 100);
//					intent.putExtra("outputY", 100);
//					intent.putExtra("return-data", true);
//					intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//					intent.putExtra("noFaceDetection", true);
//					this.startActivityForResult(intent, 1);
//				}else{
//					headImg.setImageBitmap(bitmap);
//					File bb = new File(Environment.getExternalStorageDirectory() .getAbsolutePath() + File.separator + "ltgame.jpg");
//					if(bb.exists()){
//						bb.delete();
//					}
//				}
//			} else if (requestCode == 2) {
//				// 照相返回
//				File bb = new File(Environment.getExternalStorageDirectory() .getAbsolutePath() + File.separator + "ltgame.jpg");
//				Intent intent = new Intent("com.android.camera.action.CROP");
//				intent.setType("image/*");
//				intent.setDataAndType(Uri.fromFile(bb), "image/jpeg");
//				intent.putExtra("crop", "true");
//				intent.putExtra("aspectX", 1);
//	            intent.putExtra("aspectY", 1);
//	            // outputX outputY 是裁剪图片宽高
//	            intent.putExtra("outputX",100);
//	            intent.putExtra("outputY",100);
//	            intent.putExtra("return-data", true);
//	            intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
//	            intent.putExtra("noFaceDetection", true);
//				this.startActivityForResult(intent, 1);
//			}
//        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.head_img:
//			Utils.albumSelectPopWindow(this).showAtLocation(view, Gravity.CENTER, 0, 0);
//			InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//			imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

			ImagePicker imagePicker = ImagePicker.getInstance();
			imagePicker.setImageLoader(new GlideImageLoader());
			imagePicker.setMultiMode(false);
			imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
			imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
			imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
			imagePicker.setOutPutY(1000);//保存文件的高度。单位像素

			Intent imageIntent = new Intent(mActivity, ImageGridActivity.class);
			startActivityForResult(imageIntent, 10);
			break;
		case R.id.btn_next:
			showLoadingDialog("正在提交，请稍后...");
			user = buildUserBaseInfo();
			if(!CheckUtil.checkNickName(getActivity(), user.getNickname())){
				hideLoadingDialog();
				return;
			}
			
			if(bitmap==null){
				PCNet.updateUserInfo(user, updateInfoCallBack);
			}else{
				PCNet.updateAvatar(bitmap, new WebCallBackToString() {
					
					@Override
					public void onFailure(int statusCode, Throwable error) {
						ToastUtils.showToast(getActivity().getApplicationContext(), error.getMessage());
						hideLoadingDialog();
					}
					
					@Override
					public void onSuccess(String result) {
						try {
							JSONObject jsonObj = new JSONObject(result);
							jsonObj = jsonObj.optJSONObject("data");
							if(jsonObj!=null){
								user.setAvatar(jsonObj.optString("avatar",""));
							}
						} catch (JSONException e) {
						}
						PCNet.updateUserInfo(user, updateInfoCallBack);
					}
				});
			}
			break;
		case R.id.set_birthday:
			LayoutInflater inflater=LayoutInflater.from(getActivity());
			final View timepickerview=inflater.inflate(R.layout.timepicker, null);
			ScreenInfo screenInfo = new ScreenInfo(getActivity());
			wheelTimeMain = new WheelTimeMain(timepickerview);
			wheelTimeMain.screenheight = screenInfo.getHeight();
			String time = birthday.getText().toString();
			Calendar calendar = Calendar.getInstance();
			if(JudgeDate.isDate(time, "yyyy-MM-dd")){
				try {
					calendar.setTime(dateFormat.parse(time));
				} catch (java.text.ParseException e) {
					e.printStackTrace();
				}
			}
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			wheelTimeMain.initDateTimePicker(year,month,day);
			new AlertDialog.Builder(getActivity())
			.setTitle("选择时间")
			.setView(timepickerview)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					birthday.setText(wheelTimeMain.getTime());
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
			break;
		case R.id.set_area:
			inflater=LayoutInflater.from(getActivity());
			final View Areapickerview=inflater.inflate(R.layout.areapicker, null);
			screenInfo = new ScreenInfo(getActivity());
			wheelAreaMain = new WheelAreaMain(Areapickerview);
			wheelAreaMain.screenheight = screenInfo.getHeight();
			wheelAreaMain.initDateAreaPicker();
			new AlertDialog.Builder(getActivity())
			.setTitle("选择地区")
			.setView(Areapickerview)
			.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					area.setText(wheelAreaMain.getArea());
				}
			})
			.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			})
			.show();
			break;
		default:
			break;
		}
	}
	
	private UserBaseInfo buildUserBaseInfo() {
		UserBaseInfo user = new UserBaseInfo();
		user.setAddress(area.getText().toString());
		user.setNickname(nickname.getContent());
		user.setAvatar(UserInfoManager.instance().getUserInfo().getAvatar());
		user.setBirthday(TimeUtils.getStringToDate(birthday.getText().toString()).getTime());
		if(maleBtn.isChecked()){
			user.setSex("male");
		}else{
			user.setSex("female");
		}
		return user;
	}
	
	@Override
	public void updateUserInfo(UserBaseInfo userBaseInfo) {
		ImageloaderUtil.loadUserHead(getActivity(),userBaseInfo.getAvatar(), headImg);
		nickname.setContent(userBaseInfo.getNickname());
		if (userBaseInfo.getSex().equalsIgnoreCase("male")) {
			maleBtn.setChecked(true);
		} else {
			femaleBtn.setChecked(true);
		}
			birthday.setText(TimeUtils.getLongtoString(userBaseInfo.getBirthday()));
		area.setText(userBaseInfo.getAddress());
	}

	@Override
	public void userLogout() {
		getActivity().finish();
	}

	@Override
	public void userLogin(UserBaseInfo userBaseInfo) {
	}

	@Override
	public void setPageAlias() {

	}
}
