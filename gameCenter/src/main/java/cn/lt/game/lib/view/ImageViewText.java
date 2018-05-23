package cn.lt.game.lib.view;

import android.content.Context;
import android.text.TextUtils.TruncateAt;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.lt.game.R;

public class ImageViewText extends RelativeLayout {
	public  ImageView imageView;
	public  TextView textView;

	public ImageViewText(Context context, int imageWidth,int imageHeight,int imageMarginLeft, int imageMarginBottom, float textSize) {
		super(context);
		imageView = new ImageView(context);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				 imageWidth,  imageHeight);
//		lp.setMargins(imagePaddingRight, 0, 0, 0)
//		lp.setMargins(0, 0, imageMarginRight, imageMarginBottom);
		lp.setMargins(imageMarginLeft, 0,(int) context.getResources().getDimension(R.dimen.footer_padding), imageMarginBottom);
		imageView.setId(1);
		imageView.setLayoutParams(lp);
		addView(imageView);
		textView = new TextView(context);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				imageWidth, LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.BELOW, 1);
		params.addRule(RelativeLayout.ALIGN_LEFT, 1);
		params.addRule(RelativeLayout.ALIGN_RIGHT, 1);
		textView.setSingleLine(true);
		textView.setEllipsize(TruncateAt.END);
		textView.setLines(1);
		textView.setGravity(Gravity.CENTER);
		textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,textSize);
		textView.setLayoutParams(params);
		textView.setTextColor(context.getResources().getColor(R.color.light_black));
		addView(textView);
	}

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public TextView getTextView() {
		return textView;
	}

	public void setTextView(TextView textView) {
		this.textView = textView;
	}
	
	public void setText(String text){
		textView.setText(text);
	}

	public ImageViewText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub

	}
}
