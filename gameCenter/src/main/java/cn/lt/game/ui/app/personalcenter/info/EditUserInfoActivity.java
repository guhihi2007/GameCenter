package cn.lt.game.ui.app.personalcenter.info;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import cn.lt.game.BuildConfig;
import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.datalayer.ResponseEvent;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.global.Constant;
import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.CheckUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.TimeUtils;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.lib.util.Utils;
import cn.lt.game.lib.util.image.ImageloaderUtil;
import cn.lt.game.lib.web.WebCallBackToObj;
import cn.lt.game.lib.web.WebCallBackToString;
import cn.lt.game.lib.widget.MessageDialog;
import cn.lt.game.lib.widget.time.ScreenInfo;
import cn.lt.game.lib.widget.time.WheelAreaMain;
import cn.lt.game.lib.widget.time.WheelTimeMain;
import cn.lt.game.ui.app.personalcenter.BindEmailActivity;
import cn.lt.game.ui.app.personalcenter.BindPhoneActivity;
import cn.lt.game.ui.app.personalcenter.PCNet;
import cn.lt.game.ui.app.personalcenter.PersonalCenterActivity;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;
import cn.lt.game.ui.app.personalcenter.UserInfoUpdateListening;
import cn.lt.game.ui.app.personalcenter.model.UserBaseInfo;
import cn.lt.game.ui.app.sidebar.LoadingDialog;
import de.greenrobot.event.EventBus;

public class EditUserInfoActivity extends BaseActivity implements View.OnClickListener, UserInfoUpdateListening {

    private ImageView btnBack;
    private RelativeLayout rlEditAvatar;
    private RelativeLayout rlEditNickName;
    private RelativeLayout rlEditSignature;
    private RelativeLayout rlBindPhone;
    private RelativeLayout rlBindEmail;
    private RelativeLayout rlEditSex;
    private RelativeLayout rlEditArea;
    private RelativeLayout rlEditPassword;
    private RelativeLayout rlToggleUser;
    private RelativeLayout rlBirthday;
    private TextView tvLogout;
    private TextView email;

    private ImageView ivAvatar;
    private TextView tvNickName;
    private TextView tvSignature;
    private TextView tvPhoneState;
    private TextView tvEmailState;
    private TextView tvSex;
    private TextView tvBirthday;
    private TextView tvArea;
    private TextView tvNowUserName;

    private EditUserInfoDialog exitDialog;

    private WheelAreaMain wheelAreaMain;
    private WheelTimeMain wheelTimeMain;

    private Bitmap bitmap;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_info);
        ((TextView) findViewById(R.id.tv_page_title)).setText(R.string.user_center_title_edit_info);

        btnBack = (ImageView) findViewById(R.id.btn_page_back);
        rlEditAvatar = (RelativeLayout) findViewById(R.id.user_center_edit_avatar);
        rlEditNickName = (RelativeLayout) findViewById(R.id.user_center_edit_nickname);
        rlEditSignature = (RelativeLayout) findViewById(R.id.user_center_edit_signature);
        rlBindPhone = (RelativeLayout) findViewById(R.id.user_center_edit_bind_phone);
        rlBindEmail = (RelativeLayout) findViewById(R.id.user_center_edit_bind_email);
        rlEditSex = (RelativeLayout) findViewById(R.id.user_center_edit_sex);
        rlEditArea = (RelativeLayout) findViewById(R.id.user_center_edit_area);
        rlEditPassword = (RelativeLayout) findViewById(R.id.user_center_edit_password);
        rlToggleUser = (RelativeLayout) findViewById(R.id.user_center_toggle_user);
        rlBirthday = (RelativeLayout) findViewById(R.id.user_center_edit_birthday);
        email = (TextView) findViewById(R.id.email);

        ivAvatar = (ImageView) findViewById(R.id.user_center_avatar);
        tvNickName = (TextView) findViewById(R.id.user_center_nick_name);
        tvSignature = (TextView) findViewById(R.id.user_center_signature);
        tvPhoneState = (TextView) findViewById(R.id.user_center_bind_phone_state);
        tvEmailState = (TextView) findViewById(R.id.user_center_check_email_state);
        tvSex = (TextView) findViewById(R.id.user_center_sex);
        tvBirthday = (TextView) findViewById(R.id.user_center_birthday);
        tvArea = (TextView) findViewById(R.id.user_center_area);
        tvNowUserName = (TextView) findViewById(R.id.user_center_now_user_name);

        tvLogout = (TextView) findViewById(R.id.user_center_logout);

        btnBack.setOnClickListener(this);
        rlEditAvatar.setOnClickListener(this);
        rlEditNickName.setOnClickListener(this);
        rlEditSignature.setOnClickListener(this);
        rlBindPhone.setOnClickListener(this);
        rlBindEmail.setOnClickListener(this);
        rlEditSex.setOnClickListener(this);
        rlBirthday.setOnClickListener(this);
        rlEditArea.setOnClickListener(this);
        rlEditPassword.setOnClickListener(this);
        rlToggleUser.setOnClickListener(this);
        tvLogout.setOnClickListener(this);

        UserInfoManager.instance().addListening(this);

        exitDialog = new EditUserInfoDialog(this, getResources().getString(R.string.gentle_reminder), getResources().getString(R.string.cancel_ignor_bt), getResources().getString(R.string.gallery_send));
        loadingDialog = new LoadingDialog(this);
        EventBus.getDefault().register(this);
    }

    public void onEventMainThread(ResponseEvent event) {
        UIModuleList list = (UIModuleList) event.obj;
        if (event.obj != null) {
            Log.e("new_api", "size:" + ((UIModuleList) event.obj).size());
            for (int i = 0; i < list.size(); i++) {
                Log.e("new_api", "UI_UIType:" + list.get(i).getUIType());
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_page_back:
                finish();
                break;
            case R.id.user_center_edit_avatar:
//                Utils.albumSelectPopWindow(this).showAtLocation(this.getWindow().getDecorView(), Gravity.CENTER, 0, 0);
//                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                ImagePicker imagePicker = ImagePicker.getInstance();
                imagePicker.setImageLoader(new GlideImageLoader());
                imagePicker.setMultiMode(false);
                imagePicker.setFocusWidth(800);   //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
                imagePicker.setFocusHeight(800);  //裁剪框的高度。单位像素（圆形自动取宽高最小值）
                imagePicker.setOutPutX(1000);//保存文件的宽度。单位像素
                imagePicker.setOutPutY(1000);//保存文件的高度。单位像素

                Intent imageIntent = new Intent(this, ImageGridActivity.class);
                startActivityForResult(imageIntent, 10);

                break;
            case R.id.user_center_edit_nickname:
                exitDialog.setTitle("昵称");
                exitDialog.setRightOnClickListener(new EditUserInfoDialog.RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        EditText etText = (EditText) exitDialog.findViewById(R.id.edit_text);
                        String nickName = etText.getText().toString();
                        loadingDialog.show();
                        if (!CheckUtil.checkNickName(getApplicationContext(), tvNickName.getText().toString())) {
                            loadingDialog.hide();
                        }
                        UserBaseInfo user = UserInfoManager.instance().getUserInfo();
                        user.setNickname(nickName);
                        updataUserInfo(user);
                    }
                });
                exitDialog.show();
                exitDialog.setMessageLayout(R.layout.layout_user_center_dialog_context_nickname, new EditUserInfoDialog.MessageLayoutCallback() {
                    @Override
                    public void messageLayout(View view) {
                        EditText etText = (EditText) exitDialog.findViewById(R.id.edit_text);
                        etText.setText(tvNickName.getText());
                        etText.requestFocus();
                    }
                });
                break;
            case R.id.user_center_edit_signature:
                exitDialog.setTitle("个性签名");
                exitDialog.setRightOnClickListener(new EditUserInfoDialog.RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        EditText etText = (EditText) exitDialog.findViewById(R.id.edit_text);
                        String signature = etText.getText().toString();
                        loadingDialog.show();

                        UserBaseInfo user = UserInfoManager.instance().getUserInfo();
                        user.setSummary(signature);
                        updataUserInfo(user);
                    }
                });
                exitDialog.show();
                exitDialog.setMessageLayout(R.layout.layout_user_center_dialog_context_nickname, new EditUserInfoDialog.MessageLayoutCallback() {
                    @Override
                    public void messageLayout(View view) {
                        EditText etText = (EditText) exitDialog.findViewById(R.id.edit_text);
                        etText.setText(tvSignature.getText());
                        etText.requestFocus();
                    }
                });
                break;
            case R.id.user_center_edit_bind_phone:
                Intent intent = new Intent(getApplicationContext(), BindPhoneActivity.class);
                if (TextUtils.isEmpty(UserInfoManager.instance().getUserInfo().getMobile())) {
                    intent.putExtra("type", BindPhoneActivity.BIND_PHONE);
                } else {
                    intent.putExtra("type", BindPhoneActivity.MODIFY_PHONE);
                }
                startActivity(intent);
                break;
            case R.id.user_center_edit_bind_email:
                intent = new Intent(getApplicationContext(), BindEmailActivity.class);
                if (TextUtils.isEmpty(UserInfoManager.instance().getUserInfo().getEmail())) {
                    intent.putExtra("type", BindEmailActivity.BIND_EMAIL);
                } else {
                    intent.putExtra("type", BindEmailActivity.MODIFY_EMAIL);
                }
                startActivity(intent);
                break;
            case R.id.user_center_edit_birthday:
                exitDialog.setTitle("选择生日");
                exitDialog.setRightOnClickListener(new EditUserInfoDialog.RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        String time = wheelTimeMain.getTime();
                        loadingDialog.show();
                        UserBaseInfo user = UserInfoManager.instance().getUserInfo();
                        user.setBirthday(TimeUtils.getStringToDate(time).getTime());
                        updataUserInfo(user);
                    }
                });
                exitDialog.show();
                exitDialog.setMessageLayout(R.layout.timepicker, new EditUserInfoDialog.MessageLayoutCallback() {
                    @Override
                    public void messageLayout(View view) {
                        ScreenInfo screenInfo = new ScreenInfo(EditUserInfoActivity.this);
                        wheelTimeMain = new WheelTimeMain(view);
                        wheelTimeMain.screenheight = screenInfo.getHeight();
                        wheelTimeMain.initDateTimePicker(1990, 1, 2);
                    }
                });
                break;
            case R.id.user_center_edit_sex:
                exitDialog.setTitle("性别");
                exitDialog.setRightOnClickListener(new EditUserInfoDialog.RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        RadioButton rbSexMale = (RadioButton) exitDialog.findViewById(R.id.sex_male);
                        String sex;
                        loadingDialog.show();
                        if (rbSexMale.isChecked()) {
                            sex = "male";
                        } else {
                            sex = "female";
                        }
                        UserBaseInfo user = UserInfoManager.instance().getUserInfo();
                        user.setSex(sex);
                        updataUserInfo(user);
                    }
                });
                exitDialog.show();
                exitDialog.setMessageLayout(R.layout.layout_user_center_dialog_context_sex, new EditUserInfoDialog.MessageLayoutCallback() {
                    @Override
                    public void messageLayout(View view) {
                        RadioButton rbSexMale = (RadioButton) exitDialog.findViewById(R.id.sex_male);
                        RadioButton rbSexfeMale = (RadioButton) exitDialog.findViewById(R.id.sex_female);
                        if ("male".equals(tvSex.getText())) {
                            rbSexMale.setChecked(true);
                        } else {
                            rbSexfeMale.setChecked(true);
                        }
                    }
                });

                RadioButton rbSexMale = (RadioButton) exitDialog.findViewById(R.id.sex_male);
                RadioButton rbSexFeMale = (RadioButton) exitDialog.findViewById(R.id.sex_female);
                if ("female".equals(UserInfoManager.instance().getUserInfo().getSex())) {
                    rbSexFeMale.setChecked(true);
                } else {
                    rbSexMale.setChecked(true);
                }
                break;
            case R.id.user_center_edit_area:
                exitDialog.setTitle("选择地区");
                exitDialog.setRightOnClickListener(new EditUserInfoDialog.RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        String area = wheelAreaMain.getArea();
                        loadingDialog.show();
                        UserBaseInfo user = UserInfoManager.instance().getUserInfo();
                        user.setAddress(area);
                        updataUserInfo(user);
                    }
                });
                exitDialog.show();
                exitDialog.setMessageLayout(R.layout.areapicker, new EditUserInfoDialog.MessageLayoutCallback() {
                    @Override
                    public void messageLayout(View view) {
                        ScreenInfo screenInfo = new ScreenInfo(EditUserInfoActivity.this);
                        wheelAreaMain = new WheelAreaMain(view);
                        wheelAreaMain.screenheight = screenInfo.getHeight();
                        wheelAreaMain.initDateAreaPicker();
                    }
                });
                break;
            case R.id.user_center_edit_password:
                intent = new Intent(this, PersonalCenterActivity.class);
                intent.putExtra("type", "modifyPW_Fragment");
                startActivity(intent);
                break;
            case R.id.user_center_toggle_user:
                UserInfoManager.instance().starLogin(getApplicationContext(), true);
                break;
            case R.id.user_center_logout:
                final MessageDialog exitDialog = new MessageDialog(this, "退出登录", "确定退出当前账号吗？", "取消", "确定");
                exitDialog.setRightOnClickListener(new MessageDialog.RightBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        UserInfoManager.instance().userLogout(false);
                        exitDialog.cancel();
                    }
                });
                exitDialog.setLeftOnClickListener(new MessageDialog.LeftBtnClickListener() {

                    @Override
                    public void OnClick(View view) {
                        exitDialog.cancel();
                    }
                });
                exitDialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void userLogin(UserBaseInfo userBaseInfo) {

    }

    @Override
    public void updateUserInfo(UserBaseInfo userBaseInfo) {
        tvNickName.setText(userBaseInfo.getNickname());
        if (!TextUtils.isEmpty(userBaseInfo.getSummary())) {
            tvSignature.setText(userBaseInfo.getSummary());
        } else {
            tvSignature.setText(R.string.user_center_user_not_signature);
        }
        if (!TextUtils.isEmpty(userBaseInfo.getMobile())) {
            tvPhoneState.setTextColor(getResources().getColor(R.color.detail_review));
            tvPhoneState.setText(userBaseInfo.getMobile());
        } else {
            tvPhoneState.setText(R.string.user_center_user_not_bind);
            tvPhoneState.setTextColor(getResources().getColor(R.color.light_yellow));
        }
        if (!TextUtils.isEmpty(userBaseInfo.getEmail())) {
            if (userBaseInfo.getEmail_auth() == 1) {
                tvEmailState.setTextColor(getResources().getColor(R.color.detail_review));
                tvEmailState.setText(userBaseInfo.getEmail());
            } else {
                tvEmailState.setText(R.string.user_center_user_not_check);
                tvEmailState.setTextColor(getResources().getColor(R.color.light_yellow));
                email.setVisibility(View.VISIBLE);
                email.setText(userBaseInfo.getEmail());
            }
        } else {
            tvEmailState.setText(R.string.user_center_user_not_bind);
            tvEmailState.setTextColor(getResources().getColor(R.color.light_yellow));
//            tvEmailAddress.setVisibility(View.GONE);
        }
        if ("male".equals(userBaseInfo.getSex())) {
            tvSex.setText("男");
            tvSex.setTextColor(getResources().getColor(R.color.detail_review));
        } else if ("female".equals(userBaseInfo.getSex())) {
            tvSex.setText("女");
            tvSex.setTextColor(getResources().getColor(R.color.detail_review));
        } else {
            tvSex.setText("未设置");
            tvSex.setTextColor(getResources().getColor(R.color.light_yellow));
        }
        if (userBaseInfo.getBirthday() != 0 && !TextUtils.isEmpty(TimeUtils.getLongtoString(userBaseInfo.getBirthday()))) {
            tvBirthday.setText(TimeUtils.getLongtoString(userBaseInfo.getBirthday()));
            tvBirthday.setTextColor(getResources().getColor(R.color.detail_review));
        } else {
            tvBirthday.setText("未设置");
            tvBirthday.setTextColor(getResources().getColor(R.color.light_yellow));
        }
        if (!TextUtils.isEmpty(userBaseInfo.getAddress())) {
            tvArea.setText(userBaseInfo.getAddress());
            tvArea.setTextColor(getResources().getColor(R.color.detail_review));
        } else {
            tvArea.setText("未设置");
            tvArea.setTextColor(getResources().getColor(R.color.light_yellow));
        }
        tvNowUserName.setText(userBaseInfo.getUserName());

        ImageloaderUtil.loadUserHead(this, userBaseInfo.getAvatar(), ivAvatar);
    }

    @Override
    public void userLogout() {
        finish();
    }


    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx); }

    /**
     * 图片裁剪
     * @param u
     * @return
     */
    @NonNull
    private Intent CutForPhoto(Uri u) {
        File bb = new File(getRealPathFromURI(u));
        Log.e("nohc","path: " + bb.getAbsolutePath());


        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider", bb);
        } else {
            uri = Uri.fromFile(bb);
        }

        Intent intent = new Intent("com.android.camera.action.CROP");

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 100);
        intent.putExtra("outputY", 100);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);

        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "ltgame.jpg");
        Uri outputUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileProvider",file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        return intent;
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
                    PCNet.updateAvatar(bitmap, new WebCallBackToString() {

                        @Override
                        public void onFailure(int statusCode, Throwable error) {
                            ToastUtils.showToast(getApplicationContext(), "修改失败");
                            loadingDialog.hide();
                        }

                        @Override
                        public void onSuccess(String result) {
                            UserBaseInfo user = UserInfoManager.instance().getUserInfo();
                            try {
                                JSONObject jsonObj = new JSONObject(result);
                                jsonObj = jsonObj.optJSONObject("data");
                                if (jsonObj != null) {
                                    user.setAvatar(jsonObj.optString("avatar", ""));
                                }
                                //此处需要更新更新个人信息之后，后台才能保存数据
                                updataUserInfo(user);
                            } catch (JSONException e) {
                            }
                            loadingDialog.hide();
                        }
                    });
                } else {
                    ToastUtils.showToast(this, "没有数据1");
                }
            } else {
                ToastUtils.showToast(this, "没有数据2");
            }

            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void updataUserInfo(UserBaseInfo user) {
        PCNet.updateUserInfo(user, new WebCallBackToObj<UserBaseInfo>() {

            @Override
            public void onFailure(int statusCode, Throwable error) {
//                ToastUtils.showToast(getApplicationContext(), error.getMessage());
                LogUtils.d(LogTAG.USER, "gamecenter:自己更新updataUserInfo--异常"+error.getMessage());
                loadingDialog.hide();
            }

            @Override
            protected void handle(UserBaseInfo info) {
                //先获取用户账号再保存
                info.setUserName(UserInfoManager.instance().getUserInfo().getUserName());
                info.setToken(UserInfoManager.instance().getUserInfo().getToken());//更新没有返回token
                UserInfoManager.instance().setUserBaseInfo(info, false);
                LogUtils.i("UserProvider","UserBaseInfo="+UserInfoManager.instance().getUserInfo());
                UserInfoManager.instance().updateHistoryUserAvatar(UserInfoManager.instance().getUserInfo().getId(), UserInfoManager.instance().getUserInfo().getAvatar());
                ToastUtils.showToast(getApplicationContext(), "修改成功");
                loadingDialog.hide();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        UserInfoManager.instance().removeListening(this);
    }

    @Override
    public void setPageAlias() {
        setmPageAlias(Constant.PAGE_PERSONAL_EDIT);
    }
}
