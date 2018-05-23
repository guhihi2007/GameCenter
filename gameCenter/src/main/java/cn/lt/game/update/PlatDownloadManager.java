package cn.lt.game.update;

import android.content.Context;

import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PlatDownloadManager {

    private List<RequestCallBack<File>> mCallbacks;

    public void registCallback(RequestCallBack<File> callback) {
        if (mCallbacks == null) {
            mCallbacks = new ArrayList<RequestCallBack<File>>();
        }
        mCallbacks.add(callback);
    }

    public void unRegisterCallback(RequestCallBack<File> callback) {
        if (callback != null && mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }

    public void unRegisterAllCallback() {
        if (mCallbacks != null) {
            mCallbacks.clear();
        }
    }

    public PlatDownloadManager(Context appContext) {
    }

    public void addNewDownload(String url, String fileName, String target, boolean autoResume, boolean autoRename, final RequestCallBack<File> callback) {
        registCallback(callback);
        final PlatDownloadInfo downloadInfo = new PlatDownloadInfo();
        downloadInfo.setDownloadUrl(url);
        downloadInfo.setAutoRename(autoRename);
        downloadInfo.setAutoResume(autoResume);
        downloadInfo.setFileName(fileName);
        downloadInfo.setFileSavePath(target);
        HttpUtils http = new HttpUtils();
        http.configRequestThreadPoolSize(3);
        HttpHandler<File> handler = http.download(url, target, autoResume, autoRename, new ManagerCallBack(downloadInfo, mCallbacks));
        downloadInfo.setHandler(handler);
        downloadInfo.setState(handler.getState());
    }

    public class ManagerCallBack extends RequestCallBack<File> {
        private PlatDownloadInfo downloadInfo;
        //        private List<RequestCallBack<File>> callbacks;
        List<RequestCallBack<File>> tempCallBackList = new ArrayList<>(); //modify by atian 2016//11/29

        private ManagerCallBack(PlatDownloadInfo downloadInfo, List<RequestCallBack<File>> callbacks) {
//            this.callbacks = callbacks;
            tempCallBackList.addAll(callbacks);
            this.downloadInfo = downloadInfo;
        }

        @Override
        public Object getUserTag() {

            return null;
        }

        @Override
        public void setUserTag(Object userTag) {
        }

        @Override
        public void onStart() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

        }

        @Override
        public void onCancelled() {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
        }

        @Override
        public void onLoading(long total, long current, boolean isUploading) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }

            downloadInfo.setFileLength(total);
            downloadInfo.setProgress(current);
            if (tempCallBackList != null) {
                for (RequestCallBack<File> callback : tempCallBackList) {
                    callback.onLoading(total, current, isUploading);
                }
            }
        }

        @Override
        public void onSuccess(ResponseInfo<File> responseInfo) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            if (tempCallBackList != null) {
                for (RequestCallBack<File> callback : tempCallBackList) {
                    callback.onSuccess(responseInfo);
                }
            }
        }

        @Override
        public void onFailure(HttpException error, String msg) {
            HttpHandler<File> handler = downloadInfo.getHandler();
            if (handler != null) {
                downloadInfo.setState(handler.getState());
            }
            if (tempCallBackList != null) {
                for (RequestCallBack<File> callback : tempCallBackList) {
                    callback.onFailure(error, msg);
                }
            }
        }
    }

}
