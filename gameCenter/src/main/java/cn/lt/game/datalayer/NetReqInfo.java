package cn.lt.game.datalayer;

import java.util.Map;

import cn.lt.game.net.Host;

/**
 * Created by Administrator on 2015/11/26.
 */
class NetReqInfo {
    public NetMethodType method;
    public Host.HostType hostType;
    public OnGetUri onGetUri;

    NetReqInfo(OnGetUri onGetUri) {
        this.method = NetMethodType.GET;
        this.hostType = Host.HostType.GCENTER_HOST;
        this.onGetUri = onGetUri;
    }

    NetReqInfo(NetMethodType method, OnGetUri onGetUri) {
        this.method = method;
        this.hostType = Host.HostType.GCENTER_HOST;
        this.onGetUri = onGetUri;
    }

    NetReqInfo(NetMethodType method, Host.HostType hostType, OnGetUri onGetUri) {
        this.method = method;
        this.hostType = hostType;
        this.onGetUri = onGetUri;
    }

    String getUri(Map<String, ?> params) {
        return onGetUri.getUri(params);
    }
}
