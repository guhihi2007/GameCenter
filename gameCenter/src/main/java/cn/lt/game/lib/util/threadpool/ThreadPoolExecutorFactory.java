package cn.lt.game.lib.util.threadpool;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by wenchao on 2015/09/21.
 */
public class ThreadPoolExecutorFactory {
    private final int           CPU_COUNT          = Runtime.getRuntime().availableProcessors();
    private final int           CORE_POOL_SIZE     = CPU_COUNT + 1;
    private final int           MAXIMUM_POOL_SIZE  = CPU_COUNT * 2 + 1;
    private final int           KEEP_ALIVE         = 1;
    private       int           DEFAULT_QUEUE_SIZE = 64;
    private final ReentrantLock lock               = new ReentrantLock();

    private static final ThreadFactory sThreadFactory = new ThreadFactory() {

        private final AtomicInteger mCount = new AtomicInteger(1);

        @Override

        public Thread newThread(Runnable r) {
            return new Thread(r, "ThreadPoolThread #gamecenter_custom" + mCount.getAndIncrement());
        }
    };

    private BlockingQueue<Runnable> sTaskQueue;

    private ThreadPoolExecutor THREAD_POOL_EXECUTOR;

    Executor getThreadPoolExecutor() {
        if(THREAD_POOL_EXECUTOR != null)
            return THREAD_POOL_EXECUTOR;

        return getThreadPoolExecutor(0);
    }

    Executor getThreadPoolExecutor(int queueSize) {
        return getThreadPoolExecutor(queueSize, null);
    }

    Executor getThreadPoolExecutor(int queueSize, RejectedExecutionHandler rejectedExecutionHandler) {
        return getThreadPoolExecutor(queueSize, null, rejectedExecutionHandler);
    }

    Executor getThreadPoolExecutor(int queueSize, BlockingQueue<Runnable> queue, RejectedExecutionHandler rejectedExecutionHandler) {
        if(queueSize == 0) {
            if(CPU_COUNT >= 8)
                DEFAULT_QUEUE_SIZE = 256;
            else if(CPU_COUNT >= 4)
                DEFAULT_QUEUE_SIZE = 128;
            else
                DEFAULT_QUEUE_SIZE = 64;
        } else if(queueSize > 0 && queueSize < 64) {
            DEFAULT_QUEUE_SIZE = 64;
        } else {
            DEFAULT_QUEUE_SIZE = queueSize;
        }

        if(queue == null)
            sTaskQueue = new PriorityBlockingQueue<Runnable>(DEFAULT_QUEUE_SIZE);
        else
            sTaskQueue = queue;

        RejectedExecutionHandler rejectHandler = rejectedExecutionHandler;
        if(rejectHandler == null)
            rejectHandler = new ThreadPoolExecutor.DiscardOldestPolicy();

        THREAD_POOL_EXECUTOR = new ThreadPoolExecutor(CORE_POOL_SIZE, MAXIMUM_POOL_SIZE, KEEP_ALIVE,
                TimeUnit.SECONDS, sTaskQueue, sThreadFactory, rejectHandler);

        return THREAD_POOL_EXECUTOR;
    }

    public int getQueueCount(){
        if(sTaskQueue==null){
            return 0;
        }
        return sTaskQueue.size();
    }
    public boolean setExecutorToAsyncTask(int queueSize)
            throws InterruptedException {

        return setExecutorToAsyncTask(queueSize, null);
    }

    public boolean setExecutorToAsyncTask(int queueSize, BlockingQueue<Runnable> queue)
            throws InterruptedException {

        return setExecutorToAsyncTask(queueSize, queue, null);
    }

    public boolean setExecutorToAsyncTask(int queueSize, BlockingQueue<Runnable> queue,
                                                  RejectedExecutionHandler rejectedExecutionHandler) throws InterruptedException {
        if (lock.tryLock(500, TimeUnit.MILLISECONDS)) {
            try {
                return LTAsyncTask.setThreadPoolExecutor(getThreadPoolExecutor(queueSize, queue, rejectedExecutionHandler));
            } finally {
                lock.unlock();
            }
        }

        return false;
    }

    private static ThreadPoolExecutorFactory instance = new ThreadPoolExecutorFactory();
    public static ThreadPoolExecutorFactory getInstance(){
        if(instance == null){
            instance = new ThreadPoolExecutorFactory();
        }
        return instance;
    }

    private ThreadPoolExecutorFactory(){}
}
