package cn.lt.game.ui.app.adapter;

import java.util.List;

import cn.lt.game.domain.UIModule;
import cn.lt.game.domain.UIModuleGroup;
import cn.lt.game.domain.detail.GameDomainBaseDetail;
import cn.lt.game.ui.app.adapter.data.ItemData;

/***
 * Created by Administrator on 2015/12/21.
 */
public interface IDownloadingStatusExtractor {
    List<String> getUrlsFromGroup(UIModuleGroup<ItemData<UIModule<GameDomainBaseDetail>>> group);

    String getUrlsFromModule(UIModule<GameDomainBaseDetail> module);
}
