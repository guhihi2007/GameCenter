package cn.lt.game.service;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * 预计预下载有多个的情况下准备的传参对象
 * @author liaotao
 *
 */
public class DownloadList implements Parcelable {

	private List<String> list = new ArrayList<String>();

	public DownloadList() {
	}
	
	public DownloadList(Parcel source) {
		readFromParcel(source);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeStringList(list);
	}

	public static final Parcelable.Creator<DownloadList> CREATOR = new Parcelable.Creator<DownloadList>() {

		@Override
		public DownloadList createFromParcel(Parcel source) {
			return new DownloadList(source);
		}

		@Override
		public DownloadList[] newArray(int size) {
			return new DownloadList[size];
		}

	};

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> list) {
		this.list = list;
	}
	
	public void readFromParcel(Parcel source) {
		source.readStringList(list);
	}

}
