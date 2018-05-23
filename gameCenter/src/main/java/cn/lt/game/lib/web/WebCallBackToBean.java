package cn.lt.game.lib.web;

import com.google.gson.Gson;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.lt.game.lib.netdata.ErrorFlag;

/**
 * http的回调，可以自动解析注入json
 *
 * @param <T> json注入并返回的对象
 */
public abstract class WebCallBackToBean<T> extends WebCallBackBase {

    protected abstract void handle(T info);

    public T parseData(String result) {
        Gson gson = new Gson();

        Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];

        return gson.fromJson(result, type);
    }

    @Override
    public void route(String result) {
        try {
//            new JSONObject(result);
//            if (obj.optInt("code", 200) == 200) {
//                String data = obj.optString("data");
//                try {
//                    if (result.contains("data") && TextUtils.isEmpty(data)) {
//                        handle(null);
//                    } else {
                        T info = parseData(result);
                        handle(info);
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    onFailure(ErrorFlag.handleError, e);
//                }
//            } else {
//                if (obj.optInt("code", 200) == ErrorFlag.userLogout) {
//                    UserInfoManager.instance().userLogout();
//                }
//                onFailure(obj.optInt("code", 200), new Exception(obj.optString("message", "")));
//            }
        } catch (Exception e) {
            onFailure(ErrorFlag.dataError, e);
        }
     }

}
