package cn.lt.game.ui.app.adapter.parser;


import java.util.ArrayList;
import java.util.List;

import cn.lt.game.domain.BaseUIModule;
import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.lib.util.IntegratedDataUtil;
import cn.lt.game.lib.util.LogUtils;
import cn.lt.game.ui.app.adapter.PresentType;
import cn.lt.game.ui.app.adapter.data.ItemData;

/***
 * Created by Administrator on 2015/11/24.
 */
public class NetDataAddShell {

    public static final int LINE_NUMBER = 3;

    /**
     * 处理单个的BaseUIModule对象。  处理view的两种位置。
     *
     * @param module 单个待处理数据；
     * @param pos    在网络返回数据中的实际位置（上级位置）；
     * @param subPos 在上一级UIModuleGroup中的位置（下级位置）；如果无下级位置则传-1；
     * @return 处理后的对象；
     */
    private static ItemData<? extends BaseUIModule> wrapBaseModule(BaseUIModule module, int pos, int subPos) {
        ItemData<? extends BaseUIModule> item = null;
        try {
            item = new ItemData<>(module);
            item.setmType(module.getUIType());
            item.setPos(pos);
            item.setSubPos(subPos);
//            LogUtils.d("ppp","网络层处理单个Module后得到的pos:"+pos+"===二级："+subPos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return item;
    }

    /**
     * 处理UIModuleGroup对象，将module中的单个元素包装成item对象，不重新去组合，不改变顺序；
     *
     * @param module 单个待处理数据；
     * @param pos    在网络返回数据中的实际位置；
     * @return list
     */
    private static List<ItemData<? extends BaseUIModule>> wrapModuleGroup_2(UIModuleGroup module, int pos) {
        List<ItemData<? extends BaseUIModule>> datas = null;
        try {
            if (module != null) {
                datas = new ArrayList<>();
                PresentType uiType = module.getUIType();
                List<?> modules = module.getData();
                int count = modules.size();
                for (int i = 0; i < count; i++) {
                    Object o = modules.get(i);
                    UIModule m = new UIModule<>(uiType, o);
                    ItemData<? extends BaseUIModule> tempItem = wrapBaseModule(m, pos, i + 1);
                    if (i == 0) {
                        tempItem.setIsFirst(true);
                    } else if (i == count - 1) {
                        tempItem.setIsLast(true);
                    }
                    datas.add(tempItem);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    /**
     * 按照要求重新组合module中的元素返回一个新的list集合；
     *
     * @param module       UIModuleGroup对象；
     * @param pos          在网络返回数据中的实际位置；
     * @param numberOfLine 重新组合时多少个一组；
     * @return 新的list集合；
     */
    private static List<ItemData<? extends BaseUIModule>> splitModuleGroup(UIModuleGroup module, int pos, int numberOfLine) {
        List<ItemData<? extends BaseUIModule>> datas = null;
        try {
            PresentType uiType = module.getUIType();
            List<?> modules = module.getData();
            if (modules.size() > 0) {
                datas = new ArrayList<>();
                List<? extends List<?>> tempModules = IntegratedDataUtil.integratedData(modules, numberOfLine);
                int count = tempModules.size();
                for (int i = 0; i < count; i++) {
                    //extract the groups into UIModuleGroup object;
                    List<?> list = tempModules.get(i);
                    UIModuleGroup<ItemData<? extends BaseUIModule>> group = new UIModuleGroup<>(uiType);
                    int size = list.size();
                    for (int j = 0; j < size; j++) {
                        Object o = list.get(j);
                        UIModule m = new UIModule<>(uiType, o);
                        ItemData<? extends BaseUIModule> singleItem = wrapBaseModule(m, pos, i * LINE_NUMBER + j + 1);
                        group.add(singleItem);
                    }
                    ItemData<? extends BaseUIModule> item = wrapBaseModule(group, pos, i + 1);
                    if (i == 0) {
                        item.setIsFirst(true);
                    }
                    if (i == count - 1) {
                        item.setIsLast(true);
                    }
                    datas.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    /**
     * 将一个UIModuleGroup对象作为一个整体处理，
     *
     * @param module 单个待处理数据
     * @param pos    在网络返回数据中的实际位置；
     * @return Itemdata
     */
    private static ItemData<? extends BaseUIModule> wrapModuleGroup(UIModuleGroup module, int pos) {
        ItemData<? extends BaseUIModule> datas = null;
        try {
            PresentType uiType = module.getUIType();
            List<?> modules = module.getData();
            UIModuleGroup<ItemData<? extends BaseUIModule>> group = new UIModuleGroup<>(uiType);
            int count = modules.size();
            for (int i = 0; i < count; i++) {
                Object o = modules.get(i);
                UIModule m = new UIModule<>(uiType, o);
                ItemData<? extends BaseUIModule> tempItem = wrapBaseModule(m, pos, i + 1);
                if (i == 0) {
                    tempItem.setIsFirst(true);
                } else if (i == count - 1) {
                    tempItem.setIsLast(true);
                }
                group.add(tempItem);
            }
            datas = wrapBaseModule(group, pos, -1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }

    /**
     * 处理从网络层传过来的原始数据；     只是为了重组数据结构（更流畅），并处理position
     *
     * @param list         网络层传入数据；
     * @param lastPosition 请求的页面；
     * @return 处理后的数据集合；
     */
    public static List<ItemData<? extends BaseUIModule>> wrapModuleList(List<? extends BaseUIModule> list, int lastPosition) {
        List<ItemData<? extends BaseUIModule>> datas = null;
        try {
            if (list != null) {
                int count = list.size();
                datas = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    BaseUIModule module = list.get(i);
                    if (module instanceof UIModule) {
                        LogUtils.d("ppp", "网络层的最后位置" + lastPosition);
                        datas.add(wrapBaseModule(module, lastPosition + i + 1, -1));
                    } else if (module instanceof UIModuleGroup) {
                        NetDataAddShellDir dir = module.getUIType().needSplit();
                        if (dir.isNeedSplit()) {
                            datas.addAll(splitModuleGroup((UIModuleGroup) module, lastPosition + i + 1, dir.getNumberOfLine()));
                        } else {
                            if (dir.isWhole()) {
                                datas.add(wrapModuleGroup((UIModuleGroup) module, lastPosition + i + 1));
                            } else {
                                datas.addAll(wrapModuleGroup_2((UIModuleGroup) module, lastPosition + i + 1));
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datas;
    }
}