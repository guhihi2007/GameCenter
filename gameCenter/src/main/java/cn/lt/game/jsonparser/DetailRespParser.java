package cn.lt.game.jsonparser;

import android.os.Handler;
import android.text.TextUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.lt.game.model.GameBaseDetail;
import cn.lt.game.model.RecommendGame;
import cn.lt.game.ui.app.gamegift.beans.GiftBaseData;
import cn.lt.game.ui.app.gamegift.beans.GiftGameBaseData;

public class DetailRespParser extends JsonParser {

	@Override
	public Object parseJson(int reqCnt, String result, Handler handler, int msgId, boolean needsend, Object games) throws JSONException {

		JSONObject jGame = getDataObject(result);
		String url = jGame.optString("logoImageUrl", null);
		GameBaseDetail game = games == null ? new GameBaseDetail() : (GameBaseDetail) games;
		game.setId(jGame.optInt("id", 0))
				.setLogoUrl(url).setDownUrl(jGame.optString("downUrl", null))
				.setName(jGame.optString("name", null))
				.setDownloadCnt(jGame.optInt("downloadCnt", 0))
				.setPkgSize(jGame.optInt("size", 0))
				.setPkgName(jGame.optString("packageName"))
				.setMd5(jGame.optString("md5"))
				.setVersion(jGame.optString("version"))
				.setUpdateContent(jGame.optString("updateContent"))
				.setForumId(jGame.optInt("forum_id", 0))
				.setVersionCode(jGame.optInt("versionCode", 0));

		JSONArray jsurl = jGame.optJSONArray("screenshotImageUrls");
		List<String> surl = new ArrayList<>();
		for (int i = 0; i < jsurl.length(); i++) {
			surl.add(((JSONObject) jsurl.get(i)).optString("url"));
		}

		game.setScreenshotUrls(surl)
				.setDescription(jGame.optString("description"))
				.setUpdated_at(jGame.optString("uploadTime"))
				.setCategory(jGame.optString("categoryTitle"));

		return game;

	}

	/* 传入需要解析的result，填充GameDetail */
	public static void parseJson(String result, Object games) throws JSONException {
		JSONObject jResult = new JSONObject(result);
		JSONObject jGame = jResult.getJSONObject("data");
		String url = jGame.optString("icon", null);
		GameBaseDetail game = games == null ? new GameBaseDetail() : (GameBaseDetail) games;
		game.setId(jGame.optInt("id", 0))
				.setLogoUrl(url).setDownUrl(jGame.optString("download_link", null))
				.setName(jGame.optString("title", null))
				.setDownloadCnt(jGame.optInt("download_display", 0))
				.setPkgSize(jGame.optInt("size", 0) * 1024)
				.setPkgName(jGame.optString("package"))
				.setMd5(jGame.optString("md5"))
				.setVersion(jGame.optString("version"))
				.setUpdateContent(jGame.optString("updateContent"))
				.setVersionCode(jGame.optInt("version_code", 0))
				.setForumId(jGame.optInt("forum_id", 0))
				.setReview(jGame.optString("review"));
		JSONArray jsurl = jGame.optJSONArray("screenshots");
		List<String> surl = new ArrayList<>();
		for (int i = 0; i < jsurl.length(); i++) {
			surl.add(jsurl.getString(i));
		}
		JSONArray giftArr = jGame.optJSONArray("gift");
		ArrayList<GiftBaseData> giftList = new ArrayList<GiftBaseData>();
		if (giftArr != null) {
			for (int i = 0; i < giftArr.length(); i++) {
				GiftBaseData gift = new GiftBaseData();
				gift.setId(giftArr.getJSONObject(i).getInt("id"));
				gift.setTitle(giftArr.getJSONObject(i).getString("title"));
				gift.setIs_received(giftArr.getJSONObject(i).getBoolean("is_received"));
				gift.setRemian(giftArr.getJSONObject(i).getInt("remain"));
				gift.setContent(giftArr.getJSONObject(i).getString("content"));
				gift.setTotal(giftArr.getJSONObject(i).getInt("total"));
				gift.setGame_info(detailToGift(game));
				giftList.add(gift);
			}
		}
		JSONArray jRecommend = jGame.optJSONArray("game_recommend");
		ArrayList<RecommendGame> recommendList = new ArrayList<RecommendGame>();
		for (int i = 0; i < jRecommend.length(); i++) {
			RecommendGame recommend = new RecommendGame();
			recommend.setId(jRecommend.getJSONObject(i).getInt("id"));
			recommend.setIcon(jRecommend.getJSONObject(i).getString("icon"));
			recommend.setTitle(jRecommend.getJSONObject(i).getString("title"));
			recommend.setForum_id(jRecommend.getJSONObject(i).getInt("forum_id"));
			recommendList.add(recommend);
		}
		game.setUpdated_at(jGame.optString("updated_at"));
		String[] title = jGame.optString("cat").split(",");
		if (!TextUtils.isEmpty(title[0])) {
			game.setCategory(title[0]);
		} else {
			game.setCategory(jGame.optString("cat"));
		}
	}

	/* GameDetail转GiftGameBaseData */
	private static GiftGameBaseData detailToGift(GameBaseDetail game) {
		GiftGameBaseData baseGift = new GiftGameBaseData();
		baseGift.setId(game.getId());
		baseGift.setIcon(game.getLogoUrl());
		baseGift.setPackageName(game.getPkgName());
		baseGift.setDownload_link(game.getDownUrl());
		baseGift.setMd5(game.getMd5());
		baseGift.setTitle(game.getName());
		baseGift.setSize(game.getPkgSize());
		baseGift.setVersion(game.getVersion());
		baseGift.setVersion_code(game.getVersionCode());
		return baseGift;
	}


	public static ArrayList<GameBaseDetail> parseJsonToStrategySearch(String result) {
		ArrayList<GameBaseDetail> list = new ArrayList<GameBaseDetail>();
		JSONObject jResult;
		GameBaseDetail entity;
		try {
			jResult = new JSONObject(result);
			JSONObject jGame = jResult.getJSONObject("data");
			JSONArray jArray = jGame.getJSONArray("list");
			int size = jArray.length();
			if (size > 14) {
				size = 12;
			}
			for (int i = 0; i < size; i++) {
				entity = new GameBaseDetail();
				entity.setId(jArray.getJSONObject(i).getInt("id")).setName(jArray.getJSONObject(i).getString("title"))
						.setLogoUrl(jArray.getJSONObject(i).getString("icon")).setPkgName(jArray.getJSONObject(i).getString("package")).getForumId();

				list.add(entity);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return list;

	}

}