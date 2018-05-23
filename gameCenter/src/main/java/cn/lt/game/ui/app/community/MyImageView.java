package cn.lt.game.ui.app.community;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import java.util.ArrayList;

public class MyImageView extends ImageView {
	private int position;
	private ArrayList<String> paths;

	public ArrayList<String> getPaths() {
		return paths;
	}

	public void setPaths(ArrayList<String> paths) {
		this.paths = paths;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public MyImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

}
