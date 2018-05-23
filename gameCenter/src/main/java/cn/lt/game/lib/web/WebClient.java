package cn.lt.game.lib.web;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseInterceptor;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.zip.GZIPInputStream;

import cn.lt.game.lib.netdata.ErrorFlag;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.MapVisitor;
import cn.lt.game.lib.util.Visitor;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;
import cn.lt.game.ui.app.personalcenter.UserInfoManager;

/**
 * @author Administrator
 */
public class WebClient {

    private String encode;
    private DefaultHttpClient httpClient;
    private HttpParams httpParams;
    protected int timeout;
    private int bufferSize;
    private static final String TAG = "WebClient";

    private volatile static WebClient webClient;

    private static List<Future<?>> futureList = new ArrayList<>();

    public ThreadPoolExecutor getThreadPool() {
        return threadPool;
    }

    public void setThreadPool(ThreadPoolExecutor threadPool) {
        this.threadPool = threadPool;
    }

    protected ThreadPoolExecutor threadPool = (ThreadPoolExecutor) ThreadPoolProxyFactory.getCachedThreadPool();


    private static final String HEADER_ACCEPT_ENCODING = "Accept-Encoding";
    private static final String ENCODING_GZIP = "gzip";

    protected final static int HTTP_SUCCESS = 0;
    protected final static int HTTP_FAILURE = 1;

    private static Handler handler = new Handler(Looper.getMainLooper(), new Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            Object[] arr = (Object[]) msg.obj;
            WebCallBackBase callBack = (WebCallBackBase) arr[0];
            //停止监听文件上传进度
            callBack.setIsListener(false);
            switch (msg.what) {
                case HTTP_SUCCESS:
                    callBack.executeFilter(arr[1].toString(), (URI) arr[2]);
                    break;

                case HTTP_FAILURE:
                    int status = (Integer) arr[1];
                    if(status == ErrorFlag.userLogout) {
                        UserInfoManager.instance().userLogout(false);
                    }
                    callBack.onFailure(status, (Exception) arr[2]);
                    break;

                default:
                    break;
            }
            return false;
        }
    });

    public void cancelAllTask() {
        if (futureList.size() == 0) {
            return;
        }

        for (int i = 0; i < futureList.size(); i++) {
            Future<?> future = futureList.remove(i);
            if (future != null) {
                if (!future.isDone()) {
                    future.cancel(true);
                }
            }
        }
        threadPool.purge();
    }

    WebClient() {
        timeout = 10000;
        bufferSize = 8192;
        encode = "UTF-8";
        httpParams = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParams, timeout);
        HttpConnectionParams.setSoTimeout(httpParams, timeout);
        HttpConnectionParams.setSocketBufferSize(httpParams, bufferSize);

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
        schemeRegistry.register(new Scheme("https", SSLSocketFactory.getSocketFactory(), 443));

        ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager(httpParams, schemeRegistry);

        httpClient = new DefaultHttpClient(cm, httpParams);
        httpClient.addRequestInterceptor(new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest request, HttpContext context) {
                // for (int i = 0; i < request.getAllHeaders().length; i++) {
                // System.out.println(request.getAllHeaders()[i]);
                // }
                if (!request.containsHeader(HEADER_ACCEPT_ENCODING)) {
                    request.addHeader(HEADER_ACCEPT_ENCODING, ENCODING_GZIP);
                }

            }
        });

        httpClient.addResponseInterceptor(new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse response, HttpContext context) {
                final HttpEntity entity = response.getEntity();
                if (entity == null) {
                    return;
                }
                final Header encoding = entity.getContentEncoding();
                if (encoding != null) {
                    for (HeaderElement element : encoding.getElements()) {
                        if (element.getName().equalsIgnoreCase(ENCODING_GZIP)) {
                            response.setEntity(new InflatingEntity(response.getEntity()));
                            break;
                        }
                    }
                }
            }
        });
    }

    public static WebClient singleton() {
        if (webClient == null) {
            synchronized (WebClient.class) {
                if (webClient == null) {
                    webClient = new WebClient();
                }
            }
        }
        return webClient;
    }

    /**
     * 参数序列化的http请求get
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doGet_messagePack(String url, WebCallBackBase callBack) {
        doGet_messagePack(url, null, callBack);
    }

    /**
     * 参数序列化的http请求post
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doPost_messagePack(String url, WebCallBackBase callBack) {
        doPost_messagePack(url, null, callBack);
    }

    /**
     * 参数序列化的http请求post
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doPost_messagePack(String url, Map<String, Object> params, WebCallBackBase callBack) {
        doPost_messagePack(url, params, null, callBack);
    }

    /**
     * 参数序列化的http请求put
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doPut_messagePack(String url, WebCallBackBase callBack) {
        doPut_messagePack(url, null, callBack);
    }

    /**
     * 参数序列化的http请求put
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doPut_messagePack(String url, Map<String, String> params, WebCallBackBase callBack) {
        doPut_messagePack(url, params, null, callBack);
    }

    /**
     * 参数序列化的http请求get
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doGet_messagePack(String url, Map<String, String> params, WebCallBackBase callBack) {
        doGet_messagePack(url, params, null, callBack,false);
    }

    /**
     * 参数序列化的http请求get
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doGet_messagePack(String url, Map<String, String> params, Map<String, String> cusHeaders,
                                  WebCallBackBase callBack,boolean isReport) {
        try {
            url = formUrl(url, params);
            System.out.println("url" + url);
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.getMessage());
            if(isReport){ //TODO
                LogUtils.d("junjun", "请求base时的失败了，线程===================:"+Thread.currentThread() );
                callBack.onFailure(ErrorFlag.netError, e);
            }else{
                handler.sendMessage(handler.obtainMessage(HTTP_FAILURE, new Object[]{callBack, ErrorFlag.netError, e}));
            }
            return;
        }
        final HttpGet get = new HttpGet(url);
        if (cusHeaders != null) {
            new MapVisitor<Map<String, String>, String, String>().visitAll(cusHeaders, new Visitor<Map.Entry<String, String>>() {
                @Override
                public void visit(Entry<String, String> e) {
                    get.addHeader(e.getKey(), e.getValue());
                }
            });
        }
        Log.v(TAG, "HttpGet URL" + url);
        if(isReport){
            LogUtils.d("junjun", "请求base时的线程===================:"+Thread.currentThread() );
            new WebRequestThread(get, httpClient, callBack).requestRun();
        }else{
            LogUtils.d("junjun", "task count:" + threadPool.getTaskCount() + "activeCount:" + threadPool.getActiveCount() + "\tQueue size: " + threadPool.getQueue().size());
            futureList.add(threadPool.submit(new WebRequestThread(get, httpClient, handler, callBack)));
        }

    }

    /**
     * 参数序列化的http请求post
     *
     * @param url
     * @param params 支持 String、File、inputStream
     * @return
     * @throws Exception
     */
    public void doPost_messagePack(String url, Map<String, ?> params, Map<String, String> cusHeaders, WebCallBackBase callBack) {
        Log.d("net", url);

        final HttpPost post = new HttpPost(url);
        if (cusHeaders != null) {
            new MapVisitor<Map<String, String>, String, String>().visitAll(cusHeaders, new Visitor<Map.Entry<String, String>>() {
                @Override
                public void visit(Entry<String, String> e) {
                    post.addHeader(e.getKey(), e.getValue());
                }
            });
        }
        if (params != null) {
            if (params.get("json") != null && !TextUtils.isEmpty((String) params.get("json"))) {
                try {
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-type", "application/json");
                    StringEntity se = new StringEntity((String) params.get("json"));
                    post.setEntity(se);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    RequestParams requestParams = new RequestParams();
                    Iterator<?> it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        @SuppressWarnings("unchecked") Map.Entry<String, ?> e = (Map.Entry<String, ?>) it.next();
                        if (e.getValue() != null) {
                            if (e.getValue() instanceof File) {
                                File file = (File) e.getValue();
                                requestParams.put(e.getKey(), file);
                            } else if (e.getValue() instanceof InputStream) {
                                requestParams.put(e.getKey(), (InputStream) e.getValue(), "xx.jpg");
                            } else {
                                requestParams.put(e.getKey(), (String) e.getValue());
                            }
                        }
                    }
                    post.setEntity(new ProgressHttpEntityWrapper(requestParams.getEntity(), callBack));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }

        futureList.add(threadPool.submit(new WebRequestThread(post, httpClient, handler, callBack)));
    }
    public void doPost_ReportData(String url, Map<String, ?> params, Map<String, String> cusHeaders, WebCallBackBase callBack) {
        Log.d("net", url);

        final HttpPost post = new HttpPost(url);
        if (cusHeaders != null) {
            new MapVisitor<Map<String, String>, String, String>().visitAll(cusHeaders, new Visitor<Map.Entry<String, String>>() {
                @Override
                public void visit(Entry<String, String> e) {
                    post.addHeader(e.getKey(), e.getValue());
                }
            });
        }
        if (params != null) {
            if (params.get("json") != null && !TextUtils.isEmpty((String) params.get("json"))) {
                try {
                    post.setHeader("Accept", "application/json");
                    post.setHeader("Content-type", "application/json");
                    StringEntity se = new StringEntity((String) params.get("json"));
                    post.setEntity(se);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else {
                try {
                    RequestParams requestParams = new RequestParams();
                    Iterator<?> it = params.entrySet().iterator();
                    while (it.hasNext()) {
                        @SuppressWarnings("unchecked") Map.Entry<String, ?> e = (Map.Entry<String, ?>) it.next();
                        if (e.getValue() != null) {
                            if (e.getValue() instanceof File) {
                                File file = (File) e.getValue();
                                requestParams.put(e.getKey(), file);
                            } else if (e.getValue() instanceof InputStream) {
                                requestParams.put(e.getKey(), (InputStream) e.getValue(), "xx.jpg");
                            } else {
                                requestParams.put(e.getKey(), (String) e.getValue());
                            }
                        }
                    }
                    post.setEntity(new ProgressHttpEntityWrapper(requestParams.getEntity(), callBack));
                } catch (FileNotFoundException e1) {
                    e1.printStackTrace();
                }
            }
        }
        LogUtils.d("junjun", "Thread.currentThread->" +Thread.currentThread());
       new WebRequestThread(post, httpClient, callBack).requestRun();

    }
    /**
     * 大文件上传，未测试
     *
     * @param url
     * @param file
     * @param cusHeaders
     * @param callBack
     */
    public void sendBigFile(String url, File file, Map<String, String> cusHeaders, WebCallBackBase callBack) {
        Log.d("net", url);
        final HttpPost post = new HttpPost(url);
        if (cusHeaders != null) {
            new MapVisitor<Map<String, String>, String, String>().visitAll(cusHeaders, new Visitor<Map.Entry<String, String>>() {
                @Override
                public void visit(Entry<String, String> e) {
                    post.addHeader(e.getKey(), e.getValue());
                }
            });
        }
        if (file != null) {
            FileEntity fileEntity = new FileEntity(file, "binary/octet-stream");
            post.setEntity(new ProgressHttpEntityWrapper(fileEntity, callBack));
        }
        futureList.add(threadPool.submit(new WebRequestThread(post, httpClient, handler, callBack)));
    }

    /**
     * 参数序列化的http请求put
     *
     * @param url
     * @param params
     * @return
     * @throws Exception
     */
    public void doPut_messagePack(String url, Map<String, String> params, Map<String, String> cusHeaders, WebCallBackBase callBack) {
        final HttpPut put = new HttpPut(url);
        if (cusHeaders != null) {
            new MapVisitor<Map<String, String>, String, String>().visitAll(cusHeaders, new Visitor<Map.Entry<String, String>>() {
                @Override
                public void visit(Entry<String, String> e) {
                    put.addHeader(e.getKey(), e.getValue());
                }
            });
        }
        if (params != null) {
            try {
                List<NameValuePair> data = formPostData(params);
                put.setEntity(new UrlEncodedFormEntity(data, HTTP.UTF_8));
            } catch (UnsupportedEncodingException e) {
                Log.e(TAG, e.getMessage());
                return;
            }
        }
        futureList.add(threadPool.submit(new WebRequestThread(put, httpClient, handler, callBack)));
    }

    /**
     * 参数序列化的http请求put
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doDelect_messagePack(String url, WebCallBackBase callBack) {
        doDelect_messagePack(url, null, callBack);
    }

    /**
     * 参数序列化的http请求put
     *
     * @param url
     * @return
     * @throws Exception
     */
    public void doDelect_messagePack(String url, Map<String, String> cusHeaders, WebCallBackBase callBack) {
        final HttpDelete delect = new HttpDelete(url);
        if (cusHeaders != null) {
            new MapVisitor<Map<String, String>, String, String>().visitAll(cusHeaders, new Visitor<Map.Entry<String, String>>() {
                @Override
                public void visit(Entry<String, String> e) {
                    delect.addHeader(e.getKey(), e.getValue());
                }
            });
        }
        futureList.add(threadPool.submit(new WebRequestThread(delect, httpClient, handler, callBack)));
    }

    public String formUrl(String url, Map<String, String> params) throws UnsupportedEncodingException {
        if (params == null) {
            return url;
        }

        String paramStr = "";
        Iterator<Entry<String, String>> iter = params.entrySet().iterator();
        while (iter.hasNext()) {
            Entry<String, String> entry = iter.next();
            paramStr += "&" + entry.getKey() + "=" + URLEncoder.encode(entry.getValue(), encode);
        }
        if (paramStr.length() > 0) {
            paramStr = paramStr.replaceFirst("&", "?");
        }
        url += paramStr;
        return url;
    }

    private List<NameValuePair> formPostData(Map<String, String> params) {
        List<NameValuePair> data = new ArrayList<NameValuePair>();
        if (params != null) {
            Iterator<Entry<String, String>> iter = params.entrySet().iterator();
            while (iter.hasNext()) {
                Entry<String, String> entry = iter.next();
                data.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
        }
        return data;
    }

    private static class InflatingEntity extends HttpEntityWrapper {
        public InflatingEntity(HttpEntity wrapped) {
            super(wrapped);
        }

        @Override
        public InputStream getContent() throws IOException {
            return new GZIPInputStream(wrappedEntity.getContent());
        }

        @Override
        public long getContentLength() {
            return -1;
        }
    }

}
