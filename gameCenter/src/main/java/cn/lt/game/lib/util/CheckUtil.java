package cn.lt.game.lib.util;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.lt.game.R;

/**
 * 数据格式验证，返回false是验证不通过
 *
 * @author LT
 */
public class CheckUtil {

    public final static String checkPassWorld = "!\"#$%&'()*+,-./0123456789:;<=>?@ABCDEFGHIJKLMNOPQRSTUVWXYZ[\\]^_`abcdefghijklmnopqrstuvwxyz{|}~";

    /**
     * 验证邮箱
     *
     * @param email 邮箱
     * @return 不是返回false，是返回true
     */
    public static boolean isEmail(String email) {
        boolean flag = false;
        try {
            String check = "^([a-z0-9A-Z]+[-|_|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
            Pattern regex = Pattern.compile(check);
            Matcher matcher = regex.matcher(email);
            flag = matcher.matches();
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }

    /**
     * 验证手机格式
     *
     * @param mobiles 手机号
     * @return 不是返回false，是返回true
     */
    public static boolean isMobileNO(String mobiles) {
        /*
         * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）,176
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
        String telRegex = "[1]\\d{10}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        if (TextUtils.isEmpty(mobiles)) return false;
        else return mobiles.matches(telRegex);
    }

    public static boolean checkPassWordLength(String passWord) {
        return !(passWord.getBytes().length < 6 || passWord.getBytes().length > 20);
    }

    public static boolean checkNickNameLength(String nickName) {
        int length = nickName.getBytes().length;
        try {
            length = nickName.getBytes("GBK").length;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return !(length > 14 || length < 1);
    }

    public static boolean checkNickNameSpace(String nickName) {
        return nickName.indexOf(' ') == -1;
    }

    public static boolean checkNickName(Context context, String nickName) {
        if (TextUtils.isEmpty(nickName)) {
            ToastUtils.showToast(context, "昵称不能为空");
            return false;
        }
        if (!CheckUtil.checkNickNameSpace(nickName)) {
            ToastUtils.showToast(context, "昵称中不能含有空格");
            return false;
        } else if (!CheckUtil.checkNickNameLength(nickName)) {
            ToastUtils.showToast(context, "昵称最多支持14个字符");
            return false;
        }
        return true;
    }

    public static boolean checkSignature(Context context, String signature) {
        if (!CheckUtil.checkNickNameLength(signature)) {
            ToastUtils.showToast(context, "昵称最多支持50个字符");
            return false;
        }
        return true;
    }

    public static boolean checkPhoneRegisterInfo(Context context, String userName, String passWord, String code) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord) || TextUtils.isEmpty(code)) {
            ToastUtils.showToast(context, "账号、密码以及验证码不能为空");
            return false;
        } else if (!isMobileNO(userName)) {
            ToastUtils.showToast(context, "手机号码格式错误");
            return false;
        } else if (!checkPassWordLength(passWord)) {
            ToastUtils.showToast(context, "密码长度需要保持在6-20个字符之间");
            return false;
        }
        return true;
    }

    public static boolean checkLoginInfo(Context context, String userName, String passWord) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
            ToastUtils.showToast(context, "账号、密码不能为空");
            return false;
        } else if (!isMobileNO(userName) && !isEmail(userName)) {
            ToastUtils.showToast(context, "用户名格式错误");
            return false;
        } else if (!checkPassWordLength(passWord)) {
            ToastUtils.showToast(context, "密码长度需要保持在6-20个字符之间");
            return false;
        }
        return true;
    }

    public static boolean checkEmailRegisterInfo(Context context, String userName, String passWord) {
        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(passWord)) {
            ToastUtils.showToast(context, "账号、密码不能为空");
            return false;
        } else if (!isEmail(userName)) {
            ToastUtils.showToast(context, "邮箱格式错误");
            return false;
        } else if (!checkPassWordLength(passWord)) {
            ToastUtils.showToast(context, "密码长度需要保持在6-20个字符之间");
            return false;
        }
        return true;
    }

    public static boolean checkModifyPassWordInfo(Context context, String oldPassWord, String newPassWord1, String newPassWord2) {
        if (TextUtils.isEmpty(oldPassWord)) {
            ToastUtils.showToast(context, "旧密码不能为空");
            return false;
        } else if (TextUtils.isEmpty(newPassWord1)) {
            ToastUtils.showToast(context, "新密码不能为空");
            return false;
        } else if (TextUtils.isEmpty(newPassWord2)) {
            ToastUtils.showToast(context, "请重复输入一次新密码");
            return false;
        } else if (!checkPassWordLength(oldPassWord)) {
            ToastUtils.showToast(context, "旧密码输入错误");
            return false;
        } else if (!checkPassWordLength(newPassWord1)) {
            ToastUtils.showToast(context, "新设置的密码长度需要保持在6-20个字符之间");
            return false;
        } else if (!newPassWord1.equals(newPassWord2)) {
            ToastUtils.showToast(context, "两次输入的新密码不相同");
            return false;
        }
        return true;
    }

    public static boolean checkFindPassWord(Context context, String newPassWord1, String newPassWord2) {
        if (TextUtils.isEmpty(newPassWord1)) {
            ToastUtils.showToast(context, "新密码不能为空");
            return false;
        } else if (TextUtils.isEmpty(newPassWord2)) {
            ToastUtils.showToast(context, "请重复输入一次新密码");
            return false;
        } else if (!checkPassWordLength(newPassWord1)) {
            ToastUtils.showToast(context, "新设置的密码长度需要保持在6-20个字符之间");
            return false;
        } else if (!newPassWord1.equals(newPassWord2)) {
            ToastUtils.showToast(context, "两次输入的新密码不相同");
            return false;
        }
        return true;
    }

    public static boolean checkBindPhone(Context context, String phoneNember, String code) {
        if (TextUtils.isEmpty(phoneNember)) {
            ToastUtils.showToast(context, "手机号码不能为空");
            return false;
        } else if (TextUtils.isEmpty(code)) {
            ToastUtils.showToast(context, "验证码不能为空");
            return false;
        } else if (!isMobileNO(phoneNember)) {
            ToastUtils.showToast(context, "手机号码格式错误");
            return false;
        }
        return true;
    }

    public static boolean checkBindEmail(Context context, String emailNember) {
        if (!isEmail(emailNember)) {
            ToastUtils.showToast(context, "邮箱格式错误");
            return false;
        }
        return true;
    }

    /**
     * 检查验证码是否可以继续发送
     */
    public static boolean checkCode(long codeTime, TextView btnSendVerifyCode, int count) {
        long time = System.currentTimeMillis();
        if (time - codeTime < 60000) {
            btnSendVerifyCode.setEnabled(false);
//            btnSendVerifyCode.setBackgroundResource(R.mipmap.btn_cancel_press);
            btnSendVerifyCode.setText((60 - (time - codeTime) / 1000) + "s后重试");
            btnSendVerifyCode.setTextColor(Color.parseColor("#cccccc"));
//            handler.sendEmptyMessageDelayed(0, 1000);
            return false;
        } else {
//            btnSendVerifyCode.setBackgroundResource(R.drawable.btn_green_selector);
            if (count > 0) {
                btnSendVerifyCode.setText(R.string.get_verify_retry);
            } else {
                btnSendVerifyCode.setText(R.string.get_verify_code);
            }
            btnSendVerifyCode.setTextColor(btnSendVerifyCode.getContext().getResources().getColor(R.color.theme_green));
            btnSendVerifyCode.setEnabled(true);
            return true;
        }
    }

}
