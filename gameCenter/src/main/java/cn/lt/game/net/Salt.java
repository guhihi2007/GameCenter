package cn.lt.game.net;

import cn.lt.game.net.Host.HostType;

public class Salt {

	private String mServerSalt;
	private String mUCenterSalt;

	public String getGameSalt() {
		return mServerSalt;
	}

	public void setServerSalt(String serverSalt) {
		mServerSalt = serverSalt;
	}

	public String getUserSalt() {
		return mUCenterSalt;
	}

	public void setUserSalt(String ucenterSalt) {
		mUCenterSalt = ucenterSalt;
	}

	public String getSalt(HostType hostType) {
		switch (hostType) {
		case SERVER_HOST:
			return mServerSalt;

		case UCENETER_HOST:
			return mUCenterSalt;
			
		case GIFT_HOST:
			return mServerSalt;

		default:
			return null;
		}
	}

	public void setSalt(HostType hostType, String salt) {
		switch (hostType) {
		case SERVER_HOST: {
			mServerSalt = salt;
			break;
		}
		case UCENETER_HOST: {
			mUCenterSalt = salt;
			break;
		}
		default:
			return;
		}
	}

}
