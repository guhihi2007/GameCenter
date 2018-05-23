package cn.lt.game.update;
import cn.lt.game.update.IPlatUpdateCallback;
interface IPlatUpdateService{

         void checkVersion();

         void requestNetWork();

         void registerCallback(IPlatUpdateCallback callback);

         void removeCallback(IPlatUpdateCallback callback);

 }