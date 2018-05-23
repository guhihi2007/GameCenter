package cn.lt.game.threadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * @author chengyong
 * @time 2016/10/10 21:06
 * @des 线程池工厂
 */
public class ThreadPoolProxyFactory {
    /*
      1.使用方便
      2.共用了一个线程池
      3.对线程池代理创建封装
     */
    static ThreadPoolProxy mInstalledEventThreadPoolProxy;
    static ThreadPoolProxy mUninstallThreadPoolProxy;
    static ThreadPoolProxy mNormalThreadPoolProxy;
    static ExecutorService mCachedThreadPool;
    static ScheduledExecutorService mScheduledThreadPool;

    /**
     * @return
     * @des 处理安装完成的线程池代理
     * @des 核心线程池的大小30个
     */
    public static ThreadPoolProxy getDealInstalledEventThreadPoolProxy() {
        if (mInstalledEventThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mInstalledEventThreadPoolProxy == null) {
                    mInstalledEventThreadPoolProxy = new ThreadPoolProxy(30, 30);
                }
            }
        }
        return mInstalledEventThreadPoolProxy;
    }

    /**
     * @return
     * @des 处理卸载完成的线程池代理
     * @des 核心线程池的大小2个
     */
    public static ThreadPoolProxy getDealUnInstalledEventThreadPoolProxy() {
        if (mUninstallThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mUninstallThreadPoolProxy == null) {
                    mUninstallThreadPoolProxy = new ThreadPoolProxy(2, 2);
                }
            }
        }
        return mUninstallThreadPoolProxy;
    }


    /**
     * @return
     * @des 处理普通的线程池代理
     * @des 核心线程池的大小5个
     */
    public static ThreadPoolProxy getNormalThreadPoolProxy() {
        if (mNormalThreadPoolProxy == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mNormalThreadPoolProxy == null) {
                    mNormalThreadPoolProxy = new ThreadPoolProxy(10, 10);
                }
            }
        }
        return mNormalThreadPoolProxy;
    }

    /**
     * @des 无上限线程池
     * @des 可复用已经销毁的线程
     */
    public static ExecutorService getCachedThreadPool() {
        if (mCachedThreadPool == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mCachedThreadPool == null) {
                    mCachedThreadPool = Executors.newCachedThreadPool();
                }
            }
        }
        return mCachedThreadPool;
    }
    /**
     * @des 定时器
     * @des 延时执行
     */
    public static ScheduledExecutorService getScheduledThreadPool() {
        if (mScheduledThreadPool == null) {
            synchronized (ThreadPoolProxyFactory.class) {
                if (mScheduledThreadPool == null) {
                    mScheduledThreadPool = Executors.newScheduledThreadPool(1);
                }
            }
        }
        return mScheduledThreadPool;
    }
}
