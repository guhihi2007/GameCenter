package cn.lt.game.lib.web;

import cn.lt.game.lib.netdata.ErrorFlag;

/**
 * http的回调，可以自动解析注入json
 *
 * @param <> json注入并返回的对象
 */
public abstract class WebCallBackToObject<T> extends WebCallBackBase {

    protected abstract void handle(T info);

    @Override
    public void route(String result) {
        try {
            T list = (T) cn.lt.game.lib.netdata.AnalyzeJson.analyzeJson(result);
            handle(list);
        } catch (Exception e) {
            e.printStackTrace();
            onFailure(ErrorFlag.handleError, e);
        }
    }
}
