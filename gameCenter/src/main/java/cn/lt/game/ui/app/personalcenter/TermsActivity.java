package cn.lt.game.ui.app.personalcenter;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import cn.lt.game.R;
import cn.lt.game.base.BaseActivity;

public class TermsActivity extends BaseActivity {

	private ClickListener clickListener = new ClickListener();
	private TextView tvTitle;
	private ImageView btnBack;
	private WebView tvTerms;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_terms_of_service);
		findView();
	}

	@Override
	public void setPageAlias() {

	}

	private void findView() {
		tvTitle = (TextView) findViewById(R.id.tv_page_title);
		tvTitle.setText("服务条款");

		btnBack = (ImageView) findViewById(R.id.btn_page_back);
		btnBack.setOnClickListener(clickListener);
		
		findViewById(R.id.btn_next).setVisibility(View.GONE);
		
		tvTerms = (WebView) findViewById(R.id.content);
		tvTerms.requestFocus();
		tvTerms.loadUrl("file:///android_asset/terms_of_service.html");
		WebSettings webSettings = tvTerms.getSettings();
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);
        webSettings.setSupportZoom(false);
        tvTerms.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) { 
				// 重写此方法表明点击网页里面的链接还是在当前的webview里跳转，不跳到浏览器那边
				view.loadUrl(url);
				return true;
			}
		});

	}

	private class ClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.btn_page_back:
				onBack();
				break;

			default:
				break;
			}
		}

		private void onBack() {
			finish();
		}
	}

}
