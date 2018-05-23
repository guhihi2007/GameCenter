package cn.lt.game.model;


public class ToServiceApp {

	public String package_name;
	public String version_name;
	public String version_code;
	public String package_md5;

	public ToServiceApp(String package_name, String version_name, String version_code) {
		this(package_name,version_name,version_code,null);
	}

	public ToServiceApp(String package_name, String version_name, String version_code,String package_md5) {
		this.package_name = package_name;
		this.version_name = version_name;
		this.version_code = version_code;
		this.package_md5 = package_md5;
	}
}
