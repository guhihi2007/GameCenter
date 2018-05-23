package cn.lt.game.ui.app.sidebar;

/**
 * Created by Alois on 15/3/22.
 */
public class UpdateInfo {
    private static String version;
    private static int version_code;
    private static String download_link;
    private static String feature;
    private static boolean is_force = false;
    private static String created_at;
    private static String package_md5;
    private static long package_size;

    public static long getPackage_size() {
        return package_size;
    }

    public static void setPackage_size(long package_size) {
        UpdateInfo.package_size = package_size;
    }

    public static String getPackage_md5() {
        return package_md5;
    }

    public static void setPackage_md5(String package_md5) {
        UpdateInfo.package_md5 = package_md5;
    }

    public static void setCreated_at(String created_at) {
        UpdateInfo.created_at = created_at;
    }

    public static String getCreated_at() {

        return created_at;
    }

    public static int getVersion_code() {
        return version_code;
    }

    public static void setVersion_code(int version_code) {

        UpdateInfo.version_code = version_code;
    }
    public static void setVersion(String version) {
        UpdateInfo.version = version;
    }

    public static void setDownload_link(String download_link) {
        UpdateInfo.download_link = download_link;
    }

    public static void setFeature(String feature) {
        UpdateInfo.feature = feature;
    }

    public static void setIs_force(boolean is_force) {
        UpdateInfo.is_force = is_force;
    }

    public static String getVersion() {
        return version;
    }

    public static String getDownload_link() {
        return download_link;
    }

    public static String getFeature() {
        return feature;
    }

    public static boolean isIs_force() {
        return is_force;
    }
}
