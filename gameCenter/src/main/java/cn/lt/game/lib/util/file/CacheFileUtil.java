package cn.lt.game.lib.util.file;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/***
 * 此类主要用于数据的缓存操作，目前提供文件存储和文件读缓存；
 * 
 * @author dxx
 * 
 */
public class CacheFileUtil {

	/**
	 * 数据缓存；
	 * 
	 * @param fileName
	 *            存储文件名字；
	 * @param result
	 *            存储数据
	 */
	public static boolean caheData(String fileName, String result,
			Context context) {

		try {
			File file = context.getCacheDir();
			String filePath = file.getAbsolutePath() + File.separator
					+ fileName + ".txt";
			FileWriter writer = new FileWriter(filePath);
			writer.write(result);
			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			Log.d("CacheFile", "文件缓存错误" + e);
			return false;
		}
	}

	/**
	 * 数据缓存；
	 * 
	 * @param fileName
	 *            存储文件名字；
	 * @param result
	 *            存储数据
	 */
	public static boolean caheData(int fileName, String result, Context context) {
		return caheData(fileName + "", result, context);

	}

	/**
	 * 数据缓存；
	 * 
	 * @param fileName
	 *            存储文件名字；
	 * @param object
	 *            存储对象
	 */
	public static boolean caheObject(String fileName, Object object,
			Context context) {

		try {
			File file = context.getCacheDir();
			String filePath = file.getAbsolutePath() + File.separator
					+ fileName + ".txt";
			ObjectOutputStream writer = new ObjectOutputStream(
					new FileOutputStream(filePath));
			writer.writeObject(object);
			writer.flush();
			writer.close();
			return true;
		} catch (Exception e) {
			Log.d("CacheFile", "文件缓存错误" + e);
			return false;
		}
	}

	/**
	 * 读取文件存储的对象；
	 * 
	 * @param fileName
	 *            存储文件名字；
	 * @param context
	 *            上下文对象
	 */
	public static ArrayList<String> getObjectFromFile(String fileName,
			Context context) {

		try {
			File file = context.getCacheDir();
			String filePath = file.getAbsolutePath() + File.separator
					+ fileName + ".txt";
			ObjectInputStream reader = new ObjectInputStream(
					new FileInputStream(filePath));
			ArrayList<String> paths = (ArrayList<String>) reader.readObject();
			return paths;
		} catch (Exception e) {
			Log.d("CacheFile", "文件缓存错误" + e);
			return new ArrayList<String>();
		}
	}

	/**
	 * 读取文件；
	 * 
	 * @param fileName
	 *            文件名称
	 * @param context
	 * @return
	 */
	public static String getCacheFromFile(String fileName, Context context) {

		try {
			StringBuilder builder = new StringBuilder();
			File f = context.getCacheDir();
			String filePath = f.getAbsolutePath() + File.separator + fileName
					+ ".txt";
			File file = new File(filePath);
			if (file.exists()) {
				BufferedReader reader = new BufferedReader(new FileReader(file));
				String len = "";
				while ((len = reader.readLine()) != null) {
					builder.append(len);
				}
				reader.close();
				return builder.toString();
			}
			Log.d("CacheFile", "无缓存");
			return null;
		} catch (Exception e) {
			Log.d("CacheFile", "读缓存错误" + e);
			return null;
		}

	}

	/**
	 * 读取文件；
	 * 
	 * @param fileName
	 *            文件名称
	 * @param context
	 * @return
	 */
	public static String getCacheFromFile(int fileName, Context context) {
		return getCacheFromFile(fileName + "", context);
	}

}
