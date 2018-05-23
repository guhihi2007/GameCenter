package cn.lt.game.lib.view;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.lib.util.ToastUtils;
import cn.lt.game.ui.app.gamegift.GiftSearchActivity;
import cn.lt.game.ui.app.gamestrategy.StrategySearchActivity;

public class SearchView extends FrameLayout {
	private ImageButton searchBt;
	private EditText searchEt;
	private int searchWhat = -1;
	private int gameId;
	private String gamePackage;
	private isTopActivityCallBack isTopActivityCallBack;
	public static final String KEYWORD = "keyWord";
	public isTopActivityCallBack getIsTopActivityCallBack() {
		return isTopActivityCallBack;
	}

	public void setIsTopActivityCallBack(
			isTopActivityCallBack isTopActivityCallBack) {
		this.isTopActivityCallBack = isTopActivityCallBack;
	}

	public SearchView(Context context) {
		super(context);
	}

	public SearchView(final Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.strategycenter_searchbar,
				this);
		searchBt = (ImageButton) findViewById(R.id.strategycenter_searchbar_search_img);
		searchEt = (EditText) findViewById(R.id.strategycenter_searchbar_search_edt);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SearchBar);
		String name = a.getString(R.styleable.SearchBar_searchTitle);
		searchWhat = a.getInt(R.styleable.SearchBar_searchWhat, -1);
//		searchBt.setText(name);
		searchBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				String data = searchEt.getText().toString().trim();
				if (data != "" && !data.equals("")) {
					switch (searchWhat) {
					// 1跳转到礼包搜索

					case 1:
						if (isTopActivityCallBack != null) {
							isTopActivityCallBack.OnRefreshCurrentClass();
						} else {
							Intent intent = new Intent(context,
									GiftSearchActivity.class);
							intent.putExtra(KEYWORD, getSearchEt().getText().toString().trim());
							context.startActivity(intent);
//							setEtTextCharacters(null);
						}

						break;
					// 2跳转到攻略搜索
					case 2:
						if (isTopActivityCallBack != null) {
							isTopActivityCallBack.OnRefreshCurrentClass();
						} else {
							Intent intent1 = new Intent(context,
									StrategySearchActivity.class);
							context.startActivity(intent1);
						}

						break;
					default:
						break;
					}
				} else {
					ToastUtils.showToast(context, "请输入关键字");
				}
			}
		});
		if (a != null) {
			a.recycle();
		}

		addSearchEditTextOnEditorActionListener();
	}

	public EditText getSearchEt() {
		return searchEt;
	}
	
	public void setEtTextCharacters(String text) {
		getSearchEt().setText(text);
	}

	public int getSearchEditTextID() {

		return searchEt.getId();
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public String getGamePackage() {
		return gamePackage;
	}

	public void setGamePackage(String gamePackage) {
		this.gamePackage = gamePackage;
	}

	public interface isTopActivityCallBack {
		void OnRefreshCurrentClass();
	}

	@SuppressWarnings("unused")
	private boolean isTopActivity(Context context) {
		boolean isTop = false;
		ActivityManager am = (ActivityManager) context
				.getSystemService(Activity.ACTIVITY_SERVICE);
		ComponentName cn = am.getRunningTasks(1).get(0).topActivity;

		if (cn.getClassName().contains(
				((Activity) context).getComponentName().getClassName())) {
			isTop = true;
		}

		return isTop;
	}

	public void addSearchEditTextOnEditorActionListener() {
		searchEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				searchBt.performClick();
				return true;
			}
		});
	}
}
