package cn.lt.game.lib.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;

public class StorageInfo {

	public static long[] getRomMemroy() {
		long[] romInfo = new long[2];
		//Total rom memory
		romInfo[0] = getTotalInternalMemorySize();

		//Available rom memory
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		romInfo[1] = blockSize * availableBlocks;
		return romInfo;
	}

	public static long getTotalInternalMemorySize() {
		File path = Environment.getDataDirectory();
		StatFs stat = new StatFs(path.getPath());
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}
	
	public static long[] getSDCardMemory() {
		long[] sdCardInfo=new long[2];
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			StatFs sf = new StatFs(sdcardDir.getPath());
			long bSize = sf.getBlockSize();
			long bCount = sf.getBlockCount();
			long availBlocks = sf.getAvailableBlocks();

			sdCardInfo[0] = bSize * bCount;//总大小
			sdCardInfo[1] = bSize * availBlocks;//可用大小
		}
		return sdCardInfo;
	}


}
