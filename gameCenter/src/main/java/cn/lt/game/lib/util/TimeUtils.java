package cn.lt.game.lib.util;

import android.text.TextUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class TimeUtils {

	public static final long SECOND = 1000;
	public static final long MINUTE = 60 * SECOND;
	public static final long HOUR   = 60 * MINUTE;
	public static final long DAY    = 24 * HOUR;
	public static final long WEEK   = 7 * DAY;
	public static final long MONTH  = 30 * DAY;
	public static final long YEAR   = 12 * MONTH;

	public static final long minute = 60 * 1000;
	public static final long hour = 60 * minute;
	public static final long day = 24 * hour;

	/**
	 * 将yyyy-mm-dd格式的字符串转换成Date
	 * 
	 * @param dstr
	 * @return
	 */
	public static Date getStringToDate(String dstr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Date date = null;
		try {
			date = sdf.parse(dstr);
		} catch (ParseException e) {
		}
		return date;
	}
	
	public static Date getDateToStringHaveHour(String dstr) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date date = null;
		try {
			date = sdf.parse(dstr);
		} catch (ParseException e) {
		}
		return date;
	}
	public static String getDateToString(String dstr) {
		Date date = getDateToStringHaveHour(dstr);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
		String time = formatter.format(date);
		return time;
	}

	public static String getStringToDateHaveHour(long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.US);
		return formatter.format(date);
	}

	public static String getLongtoString(long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd ", Locale.US);
		return formatter.format(date);
	}

	public static String getLongtoYear(long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy", Locale.US);
		return formatter.format(date);
	}

	public static String getLongtoTime(long time) {
		Date date = new Date(time);
		SimpleDateFormat formatter = new SimpleDateFormat("HH:mm", Locale.US);
		return formatter.format(date);
	}



	/**
	 * 从yyyy-MM-dd HH:mm:ss格式转为yyyy-MM-dd
	 * 
	 * @param dstr
	 * @return
	 */
	public static String getStringToString(String dstr) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		// Date date = null;
		// String re = null;
		// try {
		// date = sdf.parse(dstr);
		// re = formatter.format(date);
		// } catch (ParseException e) {
		// }
		return dstr.substring(0, dstr.indexOf(" "));
	}

	public static String formatIntToTimeStr(int time) {
		StringBuilder builder = new StringBuilder();
		int hour = time / 3600;
		int minute = (time - hour * 3600) / 60;
		int second = (time - hour * 3600 - minute * 60);
		if (hour == 0) {
			builder.append("  剩").append(minute).append("分")
					.append(second).append("秒");
		} else {
			builder.append(" 剩").append(hour).append("时").append(minute)
					.append("分");
		}

		return builder.toString();
	}

	/**
	 * 获取2个时间的间隔时间,
	 * @param fromTime
	 * @param toTime 格式 yyyy-MM-dd HH:mm:ss
	 * @return 毫秒数
	 */
	public static long getIntervalTime(String fromTime,String toTime){
		Date fromDate = getDateToStringHaveHour(fromTime);
		Date toDate = getDateToStringHaveHour(toTime);
		long ms = Math.abs(fromDate.getTime()-toDate.getTime());
		return ms;
	}

	/**
	 * 距离当前时间的时间差
	 * @param fromTime
	 * @return 毫秒数
	 */
	public static long getInterval(String fromTime){
		return Math.abs(System.currentTimeMillis() - getDateToStringHaveHour(fromTime).getTime());

	}


	public static String curtimeDifference(String str) {
		if(TextUtils.isEmpty(str)){
			return "";
		}
		long time = getDateToStringHaveHour(str).getTime();
		long tempTime = System.currentTimeMillis() - time;
		int re = (int) (tempTime / day);
		if (re > 0) {
			return getStringToDateHaveHour(time);
		}
		re = (int) (tempTime / hour);
		if (re > 0) {
			return re + "小时前";
		}
		re = (int) (tempTime / minute);
		if (re > 0) {
			return re + "分钟前";
		}
		return "刚刚";
	}

	/***
	 *
	 * @param str
	 * @return
	 */
	public static String curtimeDifference2(String str) {
		if(TextUtils.isEmpty(str)){
			return "";
		}
		long time = getDateToStringHaveHour(str).getTime();
		long tempTime = System.currentTimeMillis() - time;
		int re = (int) (tempTime / day);
		if (re > 0) {
			return getLongtoString(time);
		}
		re = (int) (tempTime / hour);
		if (re > 0) {
			return re + "小时前";
		}
		re = (int) (tempTime / minute);
		if (re > 0) {
			return re + "分钟前";
		}
		return "刚刚";
	}
	
	public static String formatTime(Date date){
		StringBuffer  sb =  new StringBuffer() ;
		SimpleDateFormat myFmt=new SimpleDateFormat("hh : mm", Locale.US);
		GregorianCalendar gc = new GregorianCalendar(); 
		int m = gc.get(GregorianCalendar.AM_PM);
		if (m==0) {
			sb.append("上午"+myFmt.format(date));
		}else {
			sb.append("下午" + myFmt.format(date));
		}
		return sb.toString();
		
	}

	/**事件字符串转换成事件字符串*/
	public static String StringToString(String dataStr){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
		Date data = null;
		String dateString="";
		try {
			data = sdf.parse(dataStr);
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd", Locale.US);
			dateString = formatter.format(data);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return dateString;
	}
	/**
	 * 通过字符串格式的时间获取毫秒；
	 *
	 * @param date
	 *            字符串时间
	 * @param format
	 *            时间格式
	 * @return 毫秒
	 * @throws ParseException
	 */
	public static long string2Long(String date, SimpleDateFormat format)
			throws ParseException {
		return format.parse(date).getTime();
	}


	public static String getCurrentDate() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		return sdf.format(date);
	}
	/**
	 *字符串的日期格式的计算
	 */
	public static int daysBetween(String smdate, String bdate) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
		Calendar cal = Calendar.getInstance();
		cal.setTime(sdf.parse(smdate));
		long time1 = cal.getTimeInMillis();
		cal.setTime(sdf.parse(bdate));
		long time2 = cal.getTimeInMillis();
		long between_days = (time2 - time1) / (1000 * 3600 * 24);

		return Integer.parseInt(String.valueOf(between_days));
	}

	/**
	 * 获取剩余时间字符串
	 *
	 * @param surplusTime
	 * @return
	 */
	public static String getSurplusTimeString(long surplusTime) {
		StringBuilder sb     = new StringBuilder();
		boolean       hasDay = false;
		if (surplusTime / DAY > 0) {
			sb.append(surplusTime / DAY + "天");
			surplusTime = surplusTime % DAY;
			hasDay = true;
		}
		if (surplusTime / HOUR > 0 || hasDay) {
			sb.append(surplusTime / HOUR + "时");
			surplusTime = surplusTime % HOUR;
		}
		sb.append(surplusTime / MINUTE + "分");
		surplusTime = surplusTime % MINUTE;

		sb.append(surplusTime / SECOND + "秒");
		return sb.toString();
	}

}
