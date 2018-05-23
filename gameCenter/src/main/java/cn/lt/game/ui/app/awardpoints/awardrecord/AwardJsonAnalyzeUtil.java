package cn.lt.game.ui.app.awardpoints.awardrecord;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chengyong
 * @time 2017/6/13 13:54
 * @des ${TODO}
 */

public class AwardJsonAnalyzeUtil {
    /**
     * 重新整理数据结构，方便适配。
     * @param result
     */
    public static List<AwardRecordBean> parseResult(String result) {
        try {
            List<AwardRecordBean> list = new ArrayList<>();
            JSONObject rootJsonObj = new JSONObject(result);
            JSONArray rootJsonArr = (JSONArray)rootJsonObj.opt("data");
            for (int i = 0; i < rootJsonArr.length(); i++) {
                JSONObject itemJsonObject = rootJsonArr.getJSONObject(i);
                String dataTime = itemJsonObject.getString("date");
                AwardRecordBean awardRecordBean = new AwardRecordBean();
                awardRecordBean.dataTime = dataTime;
                awardRecordBean.isTime = true;
                list.add(awardRecordBean);
                JSONArray infosJsonArr = itemJsonObject.getJSONArray("list");
                for (int j = 0; j < infosJsonArr.length(); j++) {
                    JSONObject infoJsonObj = infosJsonArr.getJSONObject(j);
                    String prize_status = infoJsonObj.getString("prize_status");
                    String prize_pic = infoJsonObj.getString("prize_pic");
                    String prize_word = infoJsonObj.getString("prize_word");
                    String id = infoJsonObj.getString("id");
                    String prize_style = infoJsonObj.getString("prize_style");
                    String valid_date = infoJsonObj.getString("valid_date");
                    String prize_name = infoJsonObj.getString("prize_name");
                    String prize_id = infoJsonObj.getString("prize_id");
                    AwardRecordBean realAwardRecordBean = new AwardRecordBean();
                    realAwardRecordBean.prize_status = prize_status;
                    realAwardRecordBean.prize_pic = prize_pic;
                    realAwardRecordBean.prize_word = prize_word;
                    realAwardRecordBean.id = id;
                    realAwardRecordBean.prize_style = prize_style;
                    realAwardRecordBean.valid_date = valid_date;
                    realAwardRecordBean.prize_name = prize_name;
                    realAwardRecordBean.prize_id = prize_id;
                    realAwardRecordBean.isTime = false;
                    list.add(realAwardRecordBean);
                }
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
