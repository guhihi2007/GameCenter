package cn.lt.game.net;

import cn.lt.game.application.Configuration;

public class Host {

    public enum HostType {
        SERVER_HOST, UCENETER_HOST, UCENETER_BASE_HOST, FORUM_HOST, GIFT_HOST, DCENTER_HOST, GCENTER_HOST, CDN_LIMIT_HOST,YZF_HOST,HOT_HOST
    }

    private static String server_host;
    private static String ucenter_host;
    private static String forum_host;
    private static String uceneter_base_host;
    private static String dcenter_host;
    private static String gcenter_host;
    private static String cdnlimit_host;
    private static String yzf_host = "http://192.168.2.63:8484/api/sign/test";
    private static String hot_host;
    public static String getHost() {
        return Configuration.getHostConfig();
    }

    public static String getHost(HostType type) {
        switch (type) {

            case GCENTER_HOST:
                return gcenter_host;

            case SERVER_HOST:
                return server_host;

            case UCENETER_HOST:
                return ucenter_host;

            case FORUM_HOST:
                return forum_host;

            case GIFT_HOST:
                return server_host;

            case UCENETER_BASE_HOST:
                return uceneter_base_host;

            case DCENTER_HOST:
                return dcenter_host;

            case CDN_LIMIT_HOST:
                return cdnlimit_host;

            case YZF_HOST:
                return yzf_host;

            case HOT_HOST:
                return hot_host;
            default:
                return null;
        }
    }

    public static void setHost(HostType type, String host) {
        switch (type) {
            case GCENTER_HOST: {
                gcenter_host = host;
                server_host = gcenter_host.substring(0, gcenter_host.lastIndexOf("/api"));
                break;
            }
            case UCENETER_HOST: {
                ucenter_host = host;
                break;
            }

            case FORUM_HOST: {
                forum_host = host;
                break;
            }

            case UCENETER_BASE_HOST: {
                uceneter_base_host = host;
                break;

            }
            case DCENTER_HOST: {
                dcenter_host = host;
                break;
            }
            case CDN_LIMIT_HOST: {
                cdnlimit_host = host;
                break;
            }
            case HOT_HOST: {
                hot_host = host;
                break;
            }
            default: {
                return;
            }
        }
    }
}
