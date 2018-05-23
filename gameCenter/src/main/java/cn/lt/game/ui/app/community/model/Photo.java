package cn.lt.game.ui.app.community.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;


public class Photo implements Serializable,Parcelable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String thumbnail;//缩略图地址
	public String original;//原图地址
	public long id;


	public Photo(Parcel in) {
		thumbnail = in.readString();
		original = in.readString();
	}
	
	public Photo() {
		super();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(thumbnail);
		dest.writeString(original);
		dest.writeLong(id);
	}
	
	public static Parcelable.Creator<Photo> CREATOR = new Creator<Photo>() {

		@Override
		public Photo createFromParcel(Parcel source) {
			return new Photo(source);
		}

		@Override
		public Photo[] newArray(int size) {
			return new Photo[size];
		}
	};
}
