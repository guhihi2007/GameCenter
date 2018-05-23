package cn.lt.game.bean;

import java.io.Serializable;


public class PkgInfo implements Serializable {
    private String app_name;
    private String package_name;
    private String version_code;
    private String version_name;
    private String uesd_time;

    public PkgInfo() {
    }

    public PkgInfo(String package_name, String version_code) {
        this.package_name = package_name;
        this.version_code = version_code;
    }

    public PkgInfo(String package_name, String version_code, String version_name) {
        this.package_name = package_name;
        this.version_code = version_code;
        this.version_name = version_name;
    }

    public PkgInfo(String app_name, String package_name, String version_code, String version_name, String uesd_time) {
        this.app_name = app_name;
        this.package_name = package_name;
        this.version_code = version_code;
        this.version_name = version_name;
        this.uesd_time = uesd_time;
    }

    public String getPackage_name() {
        return package_name;
    }

    public void setPackage_name(String package_name) {
        this.package_name = package_name;
    }

    public String getVersion_code() {
        return version_code;
    }

    public void setVersion_code(String version_code) {
        this.version_code = version_code;
    }
}
