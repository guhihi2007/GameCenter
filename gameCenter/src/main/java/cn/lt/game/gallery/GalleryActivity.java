package cn.lt.game.gallery;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import cn.lt.game.R;
import cn.lt.game.application.MyApplication;
import cn.lt.game.base.BaseActivity;
import cn.lt.game.gallery.ListImageDirPopupWindow.OnImageDirSelected;
import cn.lt.game.lib.util.ToastUtils;

//自定义相册功能
public class GalleryActivity extends BaseActivity implements OnImageDirSelected,
		OnClickListener {
	private ProgressDialog mProgressDialog;
	/**
	 * 存储文件夹中的图片数量
	 */
	private int            mPicsSize;
	/**
	 * 图片数量最多的文件夹
	 */
	private File           mImgDir;
	/**
	 * 所有的图片
	 */
	private List<String>   mImgs;

	private GridView  mGirdView;
	private MyAdapter mAdapter;
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private HashSet<String>   mDirPaths     = new HashSet<String>();
	/**
	 * 扫描拿到所有的图片文件夹
	 */
	private List<ImageFloder> mImageFloders = new ArrayList<ImageFloder>();

	private RelativeLayout mBottomLy;

	private TextView    mChooseDir;
	private TextView    mImageCount;
	private ImageButton cancel;
	private Button      confirm;
	int totalCount = 0;

	private int mScreenHeight;

	private int maxCount = 0;

	private ListImageDirPopupWindow mListImageDirPopupWindow;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			mProgressDialog.dismiss();
			// 为View绑定数据
			data2View();
			// 初始化展示文件夹的popupWindw
			initListDirPopupWindw();
		}
	};

	private void data2View() {
		if (mImgDir == null) {
			return;
		}

		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg");
            }
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.gallery_grid_item, mImgDir.getAbsolutePath(),confirm
				, maxCount);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(totalCount + "张");
		confirm.setText(String.format(getResources().getString(R.string.finish_select_image), MyAdapter.mSelectedImage.size(),maxCount));
	}

    /**
	 * 初始化展示文件夹的popupWindw
	 */
	private void initListDirPopupWindw() {
		mListImageDirPopupWindow = new ListImageDirPopupWindow(
				LayoutParams.MATCH_PARENT, (int) (mScreenHeight * 0.7),
				mImageFloders, LayoutInflater.from(getApplicationContext())
				.inflate(R.layout.list_dir, null));

		mListImageDirPopupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// 设置背景颜色变暗
				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = 1.0f;
				getWindow().setAttributes(lp);
			}
		});
		// 设置选择文件夹的回调
		mListImageDirPopupWindow.setOnImageDirSelected(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mScreenHeight = MyApplication.height;
		Intent in = getIntent();
		maxCount = in.getExtras().getInt("max_image_count");// 从上一级传过来的，如果不需要图片限制
		initView();
		getImages();
		initEvent();
	}

	@Override
	public void setPageAlias() {

	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			ToastUtils.showToast(this, "Sd卡不存在");
			return;
		}
		mProgressDialog = ProgressDialog.show(this, "", "加载中...");

		new Thread(new Runnable() {
			@Override
			public void run() {

				String firstImage = null;

				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = GalleryActivity.this
						.getContentResolver();
				// 只查询jpeg和png的图片
				Cursor mCursor = mContentResolver
						.query(mImageUri, null,
								MediaStore.Images.Media.MIME_TYPE + "=? or "
										+ MediaStore.Images.Media.MIME_TYPE
										+ "=? or "
										+ MediaStore.Images.Media.MIME_TYPE
										+ "=?", new String[] { "image/jpg",
										"image/png", "image/jpeg" },
								MediaStore.Images.Media.DATE_MODIFIED);
				while (mCursor.moveToNext()) {
					// 获取图片的路径
					String path = mCursor.getString(mCursor
							.getColumnIndex(MediaStore.Images.Media.DATA));
					// 拿到第一张图片的路径
					if (firstImage == null)
						firstImage = path;
					// 获取该图片的父路径名
					File parentFile = new File(path).getParentFile();
					if (parentFile == null)
						continue;
					String dirPath = parentFile.getAbsolutePath();
					ImageFloder imageFloder = null;
					// 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
					if (mDirPaths.contains(dirPath)) {
						continue;
					} else {
						mDirPaths.add(dirPath);
						// 初始化imageFloder
						imageFloder = new ImageFloder();
						imageFloder.setDir(dirPath);
						imageFloder.setFirstImagePath(path);
					}

					int picSize = parentFile.list(new FilenameFilter() {
						@Override
						public boolean accept(File dir, String filename) {
                            return filename.endsWith(".jpg")
                                    || filename.endsWith(".png")
                                    || filename.endsWith(".jpeg");
                        }
					}).length;
					totalCount += picSize;

					imageFloder.setCount(picSize);
					mImageFloders.add(imageFloder);

					if (picSize > mPicsSize) {
						mPicsSize = picSize;
						mImgDir = parentFile;
					}
				}
				mCursor.close();
				// 扫描完成，辅助的HashSet也就可以释放内存了
				mDirPaths = null;
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0x110);
			}
		}).start();

	}
	/**
	 * 初始化View
	 */
	private void initView() {
		cancel = (ImageButton) findViewById(R.id.back);
		confirm = (Button)findViewById(R.id.confirm);
		mGirdView = (GridView) findViewById(R.id.id_gridView);
		mChooseDir = (TextView) findViewById(R.id.id_choose_dir);
		mImageCount = (TextView) findViewById(R.id.id_total_count);
		mBottomLy = (RelativeLayout) findViewById(R.id.id_bottom_ly);
	}

	private void initEvent() {
		/**
		 * 为底部的布局设置点击事件，弹出popupWindow
		 */
		mBottomLy.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mListImageDirPopupWindow
						.setAnimationStyle(R.style.anim_popup_dir);
				mListImageDirPopupWindow.showAsDropDown(mBottomLy, 0, 0);

				WindowManager.LayoutParams lp = getWindow().getAttributes();
				lp.alpha = .3f;
				getWindow().setAttributes(lp);
			}
		});
		cancel.setOnClickListener(this);
		confirm.setOnClickListener(this);
	}

	@Override
	public void selected(ImageFloder floder) {
		mImgDir = new File(floder.getDir());
		mImgs = Arrays.asList(mImgDir.list(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String filename) {
                return filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg");

            }
		}));
		/**
		 * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
		 */
		mAdapter = new MyAdapter(getApplicationContext(), mImgs,
				R.layout.gallery_grid_item, mImgDir.getAbsolutePath(),
				confirm, maxCount);
		mGirdView.setAdapter(mAdapter);
		mImageCount.setText(floder.getCount() + "张");
		mChooseDir.setText(floder.getName());
		mListImageDirPopupWindow.dismiss();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.back) {
			MyAdapter.mSelectedImage.clear();
			GalleryActivity.this.finish();
		} else {
			if (!confirm.getText().toString().contains("0")) {
				setResult(5, getIntent());
				GalleryActivity.this.finish();
			} else {
				ToastUtils.showToast(GalleryActivity.this, "请选择照片");
			}
		}
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			MyAdapter.mSelectedImage.clear();
			GalleryActivity.this.finish();
			return false;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}
}
