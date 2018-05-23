package cn.lt.game.jsonparser;


import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonParser {

	public abstract Object parseJson(int reqCnt, String result, Handler handler, int msgId, boolean needsend, Object games)
			throws JSONException;

	public JSONArray getPageDataArray(String result) 
			throws JSONException {
		JSONObject jResult = new JSONObject(result);
		JSONObject jData = jResult.getJSONObject("dataJson");
		JSONArray jGames = jData.getJSONArray("modelList");
		return jGames;
	}
	
	public JSONArray getDataArray(String result) 
			throws JSONException {
		JSONObject jResult = new JSONObject(result);
		JSONArray jData = jResult.getJSONArray("dataJson");
		return jData;
	}
	
	public JSONArray getDataArray_new(String result) 
			throws JSONException {
		JSONObject jResult = new JSONObject(result);
		JSONArray jData = jResult.getJSONArray("data");
		return jData;
	}
	
	public JSONObject getDataObject(String result) 
			throws JSONException {
		JSONObject jResult = new JSONObject(result);
		JSONObject jData = jResult.getJSONObject("dataJson");
		return jData;
	}
	
	public int getPageCount(String result)
			throws JSONException {
		JSONObject obj = getDataObject(result);
		return obj.getInt("pageCount");
	}

}