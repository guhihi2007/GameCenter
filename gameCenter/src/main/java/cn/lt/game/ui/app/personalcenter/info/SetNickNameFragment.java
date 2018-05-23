package cn.lt.game.ui.app.personalcenter.info;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import cn.lt.game.R;
import cn.lt.game.global.Constant;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.ui.app.personalcenter.BaseFragment;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.ActionBarSetting;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;

public class SetNickNameFragment extends BaseFragment implements OnClickListener,UserInfoUpdateListening {
	private EditText tvNickName;
	private ImageView imgHead;
	private Button btnFinish;

	private Bitmap bitmap;

	@Override
	protected ActionBarSetting getActionBar() {
		ActionBarSetting bar = new ActionBarSetting();
		bar.tvTitleText = R.string.set_nickname;
		return bar;
	}
	
	private void updateUserDate(UserBaseInfo user) {
		user.setNickname(tvNickName.getText().toString());
		PCNet.updateUserInfo(user, new WebCallBackToObj<UserBaseInfo>() {
			
			@Override
			public void onFailure(int statusCode, Throwable error) {
				ToastUtils.showToast(getActivity(), error.getMessage());
				hideLoadingDialog();
			}
			
			@Override
			protected void handle(UserBaseInfo info) {
				UserInfoManager.instance().setUserBaseInfo(info, false);
				ToastUtils.showToast(getActivity(), "修改成功");
				getActivity().finish();
				hideLoadingDialog();
			}
		});
	}
	
	@Override
	protected int getFragmentLayoutRes() {
		return R.layout.fragment_set_nickname;
	}
	
	@Override
	protected void initView() {
		UserInfoManager.instance().addListening(this);
	}
	
	@Override
	protected void findView() {
		tvNickName = (EditText) view.findViewById(R.id.input_nickname);
		tvNickName.requestFocus();
		view.findViewById(R.id.head_area).setOnClickListener(this);
		imgHead = (ImageView) view.findViewById(R.id.head_img);
		btnFinish = (Button) view.findViewById(R.id.btn_finish);

		btnFinish.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoadingDialog("正在提交");
				if(!CheckUtil.checkNickName(getActivity(), tvNickName.getText().toString())){
					hideLoadingDialog();
					return;
				}
				if(bitmap==null){
					UserBaseInfo user = UserInfoManager.instance().getUserInfo();
					updateUserDate(user);
				}else{
					PCNet.updateAvatar(bitmap, new WebCallBackToString() {

						@Override
						public void onFailure(int statusCode, Throwable error) {
							Toast.makeText(getActivity(), "修改失败", Toast.LENGTH_SHORT).show();
							hideLoadingDialog();
						}

						@Override
						public void onSuccess(String result) {
							UserBaseInfo user = UserInfoManager.instance().getUserInfo();
							try {
								JSONObject jsonObj = new JSONObject(result);
								jsonObj = jsonObj.optJSONObject("data");
								if(jsonObj!=null){
									user.setAvatar(jsonObj.optString("avatar",""));
								}
							} catch (JSONException e) {
							}
							updateUserDate(user);
							hideLoadingDialog();
						}
					});
				}
			}
		});

	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.head_area:
//			Utils.albumSelectPopWindow(this).showAtLocation(view, Gravity.CENTER, 0, 0);
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
		default:
			break;
		}
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
					imgHead.setImageBitmap(bitmap);
				} else {
					ToastUtils.showToast(mActivity, "没有数据1");
				}
			} else {
				ToastUtils.showToast(mActivity, "没有数据2");
			}

			return;
		}

//		if (resultCode == Activity.RESULT_OK) {
//			if(requestCode == 1){
//				bitmap = data.getParcelableExtra("data");
//				imgHead.setImageBitmap(bitmap);
//			}else if (requestCode == 2) {
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
	public void updateUserInfo(UserBaseInfo userBaseInfo) {
		tvNickName.setText(userBaseInfo.getNickname());
		ImageloaderUtil.loadUserHead(getActivity(),userBaseInfo.getAvatar(), imgHead);
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
		setmPageAlias(Constant.PAGE_PERSONAL_REGISTER_SET_ALIAS);
	}
}
