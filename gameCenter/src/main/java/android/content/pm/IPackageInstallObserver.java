package android.content.pm;

/**
 * Created by wenchao on 2015/9/17.
 */
public interface IPackageInstallObserver extends android.os.IInterface {
    abstract class Stub extends android.os.Binder implements android.content.pm.IPackageInstallObserver{
        public Stub(){
            throw  new RuntimeException("IPackageInstallObserver&Stub$construction");
        }

        public static android.content.pm.IPackageInstallObserver asInterface(android.os.IBinder obj){
            throw  new RuntimeException("IPackageInstallObserver&Stub$asInterface");
        }

        public android.os.IBinder asBinder() {
            throw new RuntimeException("IPackageInstallObserver$Stub$asBinder");
        }

        public boolean onTransact(int code,android.os.Parcel data,android.os.Parcel reply,int flags) throws android.os.RemoteException{
            throw  new RuntimeException("IPackageInstallObserver&Stub$onTransact");
        }
    }

    void packageInstalled(java.lang.String packageName, int returnCode) throws android.os.RemoteException;
}
