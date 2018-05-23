package cn.lt.game.lib.web;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.CodeChangeUtil;
import cn.lt.game.net.NetResponse;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/**
 * http的回调，可以自动解析注入json
 * 
 * @param <T>
 *            json注入并返回的对象
 */
public abstract class WebCallBackToObj<T extends Object> extends
		WebCallBackBase {

	protected abstract void handle(T info);

	public T parseData(String result) {
		Gson gson = new Gson();
		@SuppressWarnings("unchecked")
		Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass()
				.getGenericSuperclass()).getActualTypeArguments()[0];
		Type type = new ResponseParameterizedType(entityClass);
		NetResponse<T> response = gson.fromJson(result, type);
		return response == null ? null : response.getData();
	}

	@Override
	public void route(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			if (obj.optInt("status", 1) == 1) {
				String data = obj.optString("data");
				try {
					if (result.contains("data") && TextUtils.isEmpty(data)) {
						Log.e("net", "data数据为空");
						handle(null);
					} else {
						System.out.println("result " + CodeChangeUtil.unicodeToString(result));
						T info = parseData(result);
						handle(info);
					}
				} catch (Exception e) {
					e.printStackTrace();
					onFailure(ErrorFlag.handleError, e);
				}
			} else {
				if (obj.optInt("status", 1) == ErrorFlag.userLogout) {
					UserInfoManager.instance().userLogout(false);
				}
				onFailure(obj.optInt("status", 1),
						new Exception(obj.optString("message", "")));
			}
		} catch (JSONException e) {
			onFailure(ErrorFlag.dataError, e);
		}
	}
	
	

	private static class ResponseParameterizedType implements ParameterizedType {

		private Type type;

		private ResponseParameterizedType(Type type) {
			this.type = type;
		}

		@Override
		public Type[] getActualTypeArguments() {
			return new Type[] { type };
		}

		@Override
		public Type getRawType() {
			return NetResponse.class;
		}

		@Override
		public Type getOwnerType() {
			return null;
		}
	}

}
