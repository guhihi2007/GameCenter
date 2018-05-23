package cn.lt.game.lib.util;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by LinJunSheng on 2016/7/1.
 */

public class TXTUtil {

    public static String getString(InputStream inputStream) {
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer("");
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

//    public static List<AppBriefBean> getWhite() {
//        List<AppBriefBean> whitelist = new ArrayList<>();
//        String whiteJson = TXTUtil.getString(LTApplication.instance.getResources().openRawResource(R.raw.white));
//        JSONArray jsonArry = null;
//        Gson gson = new Gson();
//        try {
//            jsonArry = new JSONArray(whiteJson);
//            JSONObject jsonObj;
//
//            List<AdsBean> adsList = new ArrayList<>();
//
//            for (int i = 0; i < jsonArry.length(); i++) {
//                jsonObj = jsonArry.getJSONObject(i);
//                AdsBean adsBean = gson.fromJson(jsonObj.toString(), AdsBean.class);
//
//                adsList.add(adsBean);
//                LogUtils.i(LogTAG.AdTAG , "白名单名称 = " + adsBean.getTitle());
//            }
//            return whitelist = AppBeanTransfer.transferAdsList(adsList, 0);
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }
}
