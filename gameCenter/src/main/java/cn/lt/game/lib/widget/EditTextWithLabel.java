package cn.lt.game.lib.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.lt.game.R;

public class EditTextWithLabel extends LinearLayout {

	public EditTextWithLabel(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			return;
		}
		setOrientation(HORIZONTAL);
		setBackgroundResource(R.color.background_grey);
		View view = LayoutInflater.from(context).inflate(
				R.layout.edit_text_with_lablel, this, true);
		label = (TextView) view.findViewById(R.id.comp_le_label);
		editText = (EditText) view.findViewById(R.id.comp_le_content);

		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.EditTextWithLabel);
		String labelText = typedArray
				.getString(R.styleable.EditTextWithLabel_label);
		if (labelText != null) {
			label.setText(labelText);
		}
		String hint = typedArray.getString(R.styleable.EditTextWithLabel_hint);
		if (hint != null) {
			editText.setHint(hint);
		}
		typedArray.recycle();
	}

	public boolean setSubViewFocus() {
		return editText.requestFocus();
	}

	public String getContent() {
		return editText.getText().toString();
	}

	public void setContent(String str) {
		editText.setText(str);
	}

	public void setError(String err) {
		editText.setError(err);
	}

	public void marginLeft(int l) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
		params.leftMargin = l;
		setLayoutParams(params);
	}

	public void marginRight(int r) {
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) getLayoutParams();
		params.rightMargin = r;
		setLayoutParams(params);
	}

	public void setInputType(int type) {
		editText.setInputType(type);
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (isInEditMode()) {
			return;
		}
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		if (isInEditMode()) {
			return;
		}
	}
	
	private TextView label;
	private EditText editText;

}
