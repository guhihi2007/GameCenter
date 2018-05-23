package cn.lt.game.lib.util;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/***
 * 处理数据此方法主要用于1、将传入的list中的连续count个元素组合在一起直到所有元素被组合完； 2、计算两个数的百分比；
 * <p/>
 * 3、按照一种格式将一个数字转换成对应的字符串；
 *
 * @author dxx
 */
public class IntegratedDataUtil {
    /**
     * 将传入的list中的连续count个元素组合在一起直到所有元素被组合完
     *
     * @param list
     * @param count 将多少个元素组合在一起
     */
    public static <T> List<List<T>> integratedData(List<T> list, int count) {
        List<List<T>> finalList = new ArrayList<List<T>>();
        if (list == null || list.size() == 0) {
            return null;
        }
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            List<T> temp = new ArrayList<T>();
            for (int i = 0; i < count; i++) {
                try {
                    temp.add(iterator.next());
                } catch (NoSuchElementException e) {
                    e.printStackTrace();
                    break;
                }

            }
            finalList.add(temp);
        }

        return finalList;
    }

    /**
     * 计算remain占 total的百分比并返回百分值字符串；
     *
     * @param remain 做分子
     * @param total  做分母；
     * @return
     */
    public static String calculatePrecent(int remain, int total) {

        if (total <= 0) {
            return null;
        }

        int result = (int) (100f * remain / total);

        return result + "%";
    }

    /**
     * 计算一个long型数据为多少MB大小并返回字符串值；
     *
     * @param size
     * @return
     */
    public static String calculateSizeMB(long size) {

        if (size == 0) {
            return 0 + "MB";
        }
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(1d * size / (1024 * 1024)) + "MB";

    }

    /**
     * 计算一个数据为多少"万"次并返回字符串值；
     *
     * @param downloadCnt
     * @return
     */
    public static String calculateCounts(int downloadCnt) {
        if (downloadCnt > 10000) {
            if (downloadCnt % 10000 > 5000) {
                return "总下载：" + (downloadCnt / 10000) + 1 + "万+";
            }
            return "总下载：" + downloadCnt / 10000 + "万+";
        } else {
            return "总下载：" + downloadCnt + "";
        }
    }

    public static String calculateCountsV4(int downloadCnt) {
        if (downloadCnt > 10000) {
            if (downloadCnt % 10000 > 5000) {
                return (downloadCnt / 10000) + 1 + "万次下载";
            }
            return downloadCnt / 10000 + "万次下载";
        } else {
            return downloadCnt + "次下载";
        }
    }
}
