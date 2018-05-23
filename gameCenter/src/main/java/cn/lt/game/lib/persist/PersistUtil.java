package cn.lt.game.lib.persist;

import com.qq.e.comm.util.Md5Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import cn.lt.game.global.LogTAG;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.lib.util.file.TTGC_DirMgr;
import cn.lt.game.threadPool.ThreadPoolProxyFactory;

/**
 * Created by JohnsonLin on 2017/8/2.
 */

public class PersistUtil {

    public static synchronized void persistData(final Serializable obj, String fileName) {
        if (null == obj) {
            return;
        }
        final String md5Name = Md5Util.encode(fileName);
        LogUtils.i(LogTAG.persistTAG, fileName + "的 md5 = " + md5Name);

        ThreadPoolProxyFactory.getCachedThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                File files = new File(TTGC_DirMgr.PHONE_STORAGE_ROOT_PATH + File.separator + PersistConst.PERSIST_DIR);
                if (!files.exists()) {
                    files.mkdirs();
                }
                File file = new File(files + File.separator + md5Name);
                ObjectOutputStream oos = null;
                try {
                    oos = new ObjectOutputStream(new FileOutputStream(file));
                    oos.writeObject(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(null != oos) {
                        try {
                            oos.close();
                            oos = null;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }

    public static Object readData(final String fileName) {
        Object data = null;

        String md5Name = Md5Util.encode(fileName);

        String filePath = TTGC_DirMgr.PHONE_STORAGE_ROOT_PATH + File.separator + PersistConst.PERSIST_DIR + File.separator + md5Name;
        File file = new File(filePath);

        if (file.exists()) {
            ObjectInputStream ois = null;
            try {
                ois = new ObjectInputStream(new FileInputStream(filePath));
                data = ois.readObject();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if(null != ois) {
                    try {
                        ois.close();
                        ois = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return data;
    }
}
