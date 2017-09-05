package com.ljt.binderdemo;

import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

/**
 * Created by 1 on 2017/9/5.
 */

public abstract interface IDongleIpcResultCallBack extends IInterface
{
    public abstract void onReturn(Bundle paramBundle)
            throws RemoteException;

    public static abstract class Stub extends Binder
            implements IDongleIpcResultCallBack
    {
        private static final String DESCRIPTOR = "com.iflytek.xiri.dongle.ipc.IDongleIpcResultCallBack";
        static final int TRANSACTION_onReturn = 1;

        public Stub()
        {
            attachInterface(this, "com.iflytek.xiri.dongle.ipc.IDongleIpcResultCallBack");
        }

        public static IDongleIpcResultCallBack asInterface(IBinder paramIBinder)
        {
            if (paramIBinder == null)
                return null;
            IInterface localIInterface = paramIBinder.queryLocalInterface("com.iflytek.xiri.dongle.ipc.IDongleIpcResultCallBack");
            if ((localIInterface != null) && ((localIInterface instanceof IDongleIpcResultCallBack)))
                return (IDongleIpcResultCallBack)localIInterface;
            return new Proxy(paramIBinder);
        }

        public IBinder asBinder()
        {
            return this;
        }

        public boolean onTransact(int paramInt1, Parcel paramParcel1, Parcel paramParcel2, int paramInt2)
                throws RemoteException
        {
            switch (paramInt1)
            {
                default:
                    return super.onTransact(paramInt1, paramParcel1, paramParcel2, paramInt2);
                case 1598968902:
                    paramParcel2.writeString("com.iflytek.xiri.dongle.ipc.IDongleIpcResultCallBack");
                    return true;
                case 1:
            }
            paramParcel1.enforceInterface("com.iflytek.xiri.dongle.ipc.IDongleIpcResultCallBack");
            if (paramParcel1.readInt() != 0);
            for (Bundle localBundle = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle = null)
            {
                onReturn(localBundle);
                paramParcel2.writeNoException();
                return true;
            }
        }

        private static class Proxy
                implements IDongleIpcResultCallBack
        {
            private IBinder mRemote;

            Proxy(IBinder paramIBinder)
            {
                this.mRemote = paramIBinder;
            }

            public IBinder asBinder()
            {
                return this.mRemote;
            }

            public String getInterfaceDescriptor()
            {
                return "com.iflytek.xiri.dongle.ipc.IDongleIpcResultCallBack";
            }

            public void onReturn(Bundle paramBundle)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken("com.iflytek.xiri.dongle.ipc.IDongleIpcResultCallBack");
                    if (paramBundle != null)
                    {
                        localParcel1.writeInt(1);
                        paramBundle.writeToParcel(localParcel1, 0);
                    }
                    while (true)
                    {
                        this.mRemote.transact(1, localParcel1, localParcel2, 0);
                        localParcel2.readException();
                        return;
                        localParcel1.writeInt(0);
                    }
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
                throw localObject;
            }
        }
    }
}