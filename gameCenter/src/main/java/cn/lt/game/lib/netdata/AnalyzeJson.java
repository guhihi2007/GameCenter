package cn.lt.game.lib.netdata;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;

import cn.lt.game.bean.AnalyzeConfig;
import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.DataTransformer;
import cn.lt.game.domain.UIModuleList;
import cn.lt.game.lib.util.LogUtils;

/**
 * Created by Administrator on 2015/11/12.
 */
public class AnalyzeJson {

    public static UIModuleList analyzeJson(String str) {

        //LogUtils.e("base64",NetDataEncoding.decode(NetDataEncoding.encode(str)));

        str = str.trim();
        if (str.length() == 0) {
            return null;
        }
        if (str.toCharArray()[0] == '[') {
            return analyzeArray(str);
        } else {
            UIModuleList list = new UIModuleList();
            BaseUIModule module = analyzeBean(str);
            if (module != null) {
                list.add(module);
            }
            return list;
        }
    }

    private static UIModuleList analyzeArray(String str) {
        JSONArray jsonArray;
        UIModuleList reList = new UIModuleList();
        Gson gson = new Gson();
        try {
            jsonArray = new JSONArray(str);
            JSONObject jsonObj;
            BaseUIModule bean;
            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj = jsonArray.getJSONObject(i);
                bean = AutoCallAnalyzeFun(jsonObj, gson);
                if (bean != null) {
                    reList.add(bean);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reList;
    }

    private static BaseUIModule analyzeBean(String str) {
        Gson gson = new Gson();
        BaseUIModule bean = null;
        try {
            JSONObject jsonObj = new JSONObject(str);
            bean = AutoCallAnalyzeFun(jsonObj, gson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return bean;
    }

    private static BaseUIModule AutoCallAnalyzeFun(JSONObject jsonObj, Gson gson) {
        BaseBean bean;
        BaseUIModule baseUIModule = null;
        try {
            String typeString;
            AnalyzeJsonBean analyzeJsonBean;
            typeString = jsonObj.optString("type", "");
            if (!TextUtils.isEmpty(typeString)) {
                analyzeJsonBean = AnalyzeConfig.getMap().get(typeString);
//                LogUtils.e("lt_Analyze", "typeString:" + typeString);
                if (analyzeJsonBean.isAutoAnalyze()) {
                    bean = gson.fromJson(jsonObj.opt("data").toString(), ((TypeToken) analyzeJsonBean.getValue()).getType());
                } else {
                    // 手动解析的代码，使用反射调用method，method为静态方法
                    Method method = (Method) analyzeJsonBean.getValue();
                    bean = (BaseBean) method.invoke(null, jsonObj.opt("data").toString());
                }
                if (bean != null) {
                    bean.setType(typeString);
                    baseUIModule = DataTransformer.getUIDataFromNetData(bean);
                }
            }
        } catch (Exception e) {
            LogUtils.e("lt_Analyze", e.getMessage());
            e.printStackTrace();
        }

        return baseUIModule;
    }

}
