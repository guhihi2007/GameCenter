package cn.lt.game.lib.web;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/**
 * 该方法对过滤后的数据直接交由回调处理，不做解析
 */
public abstract class WebCallBackToEvent extends WebCallBackBase implements FileUploadProgressListener {

    public abstract void onSuccess(String result) throws JSONException;


    @Override
    public void route(String result) {
        try {
            if (result.toCharArray()[0] == '[') {
                onSuccess(result);
                return;
            }
            JSONObject obj = new JSONObject(result);
            if (obj.optString("error", null) != null) {
                onFailure(ErrorFlag.dataError, new Exception(obj.optString("message")));
            } else {
                onSuccess(result);
            }
        } catch (JSONException e) {
            onFailure(ErrorFlag.handleError, e);
        }
    }

    @Override
    public void transferred(long uploadSize, long totalSize) {
        super.transferred(uploadSize, totalSize);
    }

    @Override
    public void onFailure(int statusCode, Throwable error) {
        if(statusCode==ErrorFlag.userLogout){
            UserInfoManager.instance().userLogout(false);
        }
    }
}
