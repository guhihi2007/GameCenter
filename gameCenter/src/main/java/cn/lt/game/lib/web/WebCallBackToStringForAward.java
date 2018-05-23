package cn.lt.game.lib.web;

import org.json.JSONException;
import org.json.JSONObject;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.LogUtils;

/**
 * 该方法对过滤后的数据直接交由回调处理，不做解析
 */
public abstract class WebCallBackToStringForAward extends WebCallBackBase implements FileUploadProgressListener {

	public abstract void onSuccess(String result);

	@Override
	public void route(String result) {
		try {
			JSONObject obj = new JSONObject(result);
			if(obj.optInt("status", 1) == 1){
				try{
					onSuccess(obj.getString("data"));
				}catch(Exception e){
					e.printStackTrace();
					onFailure(ErrorFlag.handleError, new Exception(obj.optString("message","数据解析异常")));
					LogUtils.i(LogTAG.CHOU, "数据解析异常");
				}
			}else{
				onFailure(obj.optInt("status",1), new Exception(obj.optString("message","返回结果为空")));
				LogUtils.i(LogTAG.CHOU, "返回结果为空");
			}
		} catch (JSONException e) {
			onFailure(ErrorFlag.dataError, e);
			LogUtils.i(LogTAG.CHOU, "json异常");
		}
	}
    
    @Override
    public void transferred(long uploadSize, long totalSize) {
    	super.transferred(uploadSize, totalSize);
    }

}
