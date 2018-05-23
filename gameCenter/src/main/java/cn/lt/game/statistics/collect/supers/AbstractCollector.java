package cn.lt.game.statistics.collect.supers;

import android.content.Context;

import java.lang.reflect.Constructor;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.statistics.database.dao.supers.AbstractDao;
import cn.lt.game.statistics.database.service.supers.AbstractService;

/***
 * 此类为各类事件统计的超类；
 *
 * @author Administrator
 *
 * @param <T>
 *            事件类型的实体对象；
 * @param <S>
 *            事件数据库操作的业务对象；
 * @param <D>
 *            事件数据库操作的DAO对象；
 */
public abstract class AbstractCollector<T, S, D> {

    protected Map<String, String> mDatas = new HashMap<String, String>();

    protected Context mContext;

    protected final String TAG = this.getClass().getName();

    /**
     * 是否在上传数据到服务器的整个操作过程；
     */
    private boolean isReportting;

    public AbstractCollector(Context context) {
        this.mContext = context;
        if (mDatas == null) {
            mDatas = new HashMap<String, String>();
        }
    }

    public boolean isReportting() {
        return isReportting;
    }

    public void setReportting(boolean isReportting) {
        this.isReportting = isReportting;
    }

    /**
     * 执行上传数据到服务器的操作流程；
     */
    public final void report() {
        try {
            setReportting(true);
            Map<String, String> map = gatherAllData(getIService(), getIDao());
            if (map == null || map.size() == 0) {
                wakeUp();
                return;
            }
            submitData(map);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.i("GOOD", "report error -->" + this);
        }

    }

    /**
     * 删除单条数据； 只有安装完成才会调用
     *
     * @param data 要保存的数据；
     */
    public final synchronized void remove(T data) {
        if (isReportting()) {
            try {
                wait(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        removeSingleDataFromDB(data, getIService(), getIDao());
    }

    /**
     * 删除单条数据；
     *
     * @param data 要保存的数据；
     */
    public final synchronized void removeSingle(T data) {
        if (isReportting()) {
            try {
                wait(30 * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        removeSingleDataFromDB(data, getIService(), getIDao());
    }

    /**
     * 执行保存数据；
     *
     * @param data 要保存的数据；
     */
    public final synchronized void save(T data) {
        saveSingleDataToDB(data, getIService(), getIDao());
    }

    /**
     * 此方法为默认生成泛型“D”的一个实例对象，此泛型对象必须提供一个带Context参数 的构造函数；
     * <p>
     * 如果需要实现其他带不同参数的对象需要自行覆盖该方法的实现；
     *
     * @return 数据库操作业务Servcie对象；
     */
    @SuppressWarnings("unchecked")
    protected AbstractService<T> getIService() {
        try {
            Class<T> entityClass = dealGenericType(1);
            Constructor<T> c = entityClass.getConstructor(Context.class);
            Object o = c.newInstance(mContext);
            return (AbstractService<T>) o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 此方法为默认生成泛型“S”的一个实例对象，此泛型对象必须提供一个带Context参数 的构造函数；
     * <p>
     * 如果需要实现其他带不同参数的对象需要自行覆盖该方法的实现；
     *
     * @return 数据库操作Dao对象；
     */
    @SuppressWarnings("unchecked")
    protected AbstractDao<T> getIDao() {
        try {
            Class<T> entityClass = dealGenericType(2);
            Constructor<T> c = entityClass.getConstructor(Context.class);
            Object o = c.newInstance(mContext);
            return (AbstractDao<T>) o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 该方法用来获得该对象持有的指定位置的泛型类对象；
     *
     * @param index 对应的第几个泛型类型；顺序从0开始
     * @return
     */
    @SuppressWarnings("unchecked")
    private Class<T> dealGenericType(int index) {
        Class<T> entityClass = null;
        Object genericClass = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[index];
        if (genericClass instanceof ParameterizedType) { // 处理多级泛型
            entityClass = (Class<T>) ((ParameterizedType) genericClass).getRawType();
        } else if (genericClass instanceof GenericArrayType) { // 处理数组泛型
            entityClass = (Class<T>) ((GenericArrayType) genericClass).getGenericComponentType();
        } else if (genericClass instanceof TypeVariable) { // 处理泛型擦拭对象

        } else {
            entityClass = (Class<T>) genericClass;
        }
        return entityClass;
    }

    /**
     * 删除所有数据；
     *
     * @param service
     * @param dao
     */
    protected void removeAllFromDB(AbstractService<T> service, AbstractDao<T> dao) {
        try {
            service.deleteAllDataFromDB(dao);
        } catch (Exception e) {
            e.printStackTrace();
        }
        wakeUp();
    }

    /**
     * 有其他线程在等待时，需要调用此方法唤醒；
     */
    protected synchronized void wakeUp() {
        setReportting(false);
        notifyAll();
    }

    /**
     * 保存数据到数据库；
     *
     * @param data    要保存的数据；
     * @param service 执行的业务逻辑；
     * @param dao     执行数据库操作的对象；
     */
    protected abstract void saveSingleDataToDB(T data, AbstractService<T> service, AbstractDao<T> dao);

    /**
     * 提交数据到服务器；
     *
     * @param map
     */
    protected abstract void submitData(Map<String, String> map);

    /**
     * 查询数据库数据；
     *
     * @return
     */
    protected abstract Map<String, String> gatherAllData(AbstractService<T> service, AbstractDao<T> dao);

    /**
     * 删除数据库中指定的数据；
     *
     * @param data
     * @param service
     * @param dao
     */
    protected abstract void removeSingleDataFromDB(T data, AbstractService<T> service, AbstractDao<T> dao);
}
