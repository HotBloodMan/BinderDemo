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

public abstract interface IDongleRemoteInterface extends IInterface
{
    public abstract boolean invokeRemoteFunction(int paramInt, Bundle paramBundle1, Bundle paramBundle2)
            throws RemoteException;

    public abstract boolean invokeRemoteFunctionAsync(int paramInt, Bundle paramBundle, IDongleIpcResultCallBack paramIDongleIpcResultCallBack)
            throws RemoteException;

    public abstract boolean setRemoteDongleEventCallBack(IDongleRemoteEventCallBack paramIDongleRemoteEventCallBack)
            throws RemoteException;

    public static abstract class Stub extends Binder
            implements IDongleRemoteInterface
    {
        private static final String DESCRIPTOR = "com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface";
        static final int TRANSACTION_invokeRemoteFunction = 2;
        static final int TRANSACTION_invokeRemoteFunctionAsync = 3;
        static final int TRANSACTION_setRemoteDongleEventCallBack = 1;

        public Stub()
        {
            attachInterface(this, "com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
        }

        public static IDongleRemoteInterface asInterface(IBinder paramIBinder)
        {
            if (paramIBinder == null)
                return null;
            IInterface localIInterface = paramIBinder.queryLocalInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
            if ((localIInterface != null) && ((localIInterface instanceof IDongleRemoteInterface)))
                return (IDongleRemoteInterface)localIInterface;
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
                    paramParcel2.writeString("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
                    return true;
                case 1:
                    paramParcel1.enforceInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
                    boolean bool3 = setRemoteDongleEventCallBack(IDongleRemoteEventCallBack.Stub.asInterface(paramParcel1.readStrongBinder()));
                    paramParcel2.writeNoException();
                    int n = 0;
                    if (bool3)
                        n = 1;
                    paramParcel2.writeInt(n);
                    return true;
                case 2:
                    paramParcel1.enforceInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
                    int k = paramParcel1.readInt();
                    Bundle localBundle2;
                    Bundle localBundle3;
                    if (paramParcel1.readInt() != 0)
                    {
                        localBundle2 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1);
                        localBundle3 = new Bundle();
                        boolean bool2 = invokeRemoteFunction(k, localBundle2, localBundle3);
                        paramParcel2.writeNoException();
                        if (!bool2)
                            break label201;
                    }
                    label201: for (int m = 1; ; m = 0)
                    {
                        paramParcel2.writeInt(m);
                        if (localBundle3 == null)
                            break label207;
                        paramParcel2.writeInt(1);
                        localBundle3.writeToParcel(paramParcel2, 1);
                        return true;
                        localBundle2 = null;
                        break;
                    }
                    label207: paramParcel2.writeInt(0);
                    return true;
                case 3:
            }
            paramParcel1.enforceInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
            int i = paramParcel1.readInt();
            if (paramParcel1.readInt() != 0);
            for (Bundle localBundle1 = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle1 = null)
            {
                boolean bool1 = invokeRemoteFunctionAsync(i, localBundle1, IDongleIpcResultCallBack.Stub.asInterface(paramParcel1.readStrongBinder()));
                paramParcel2.writeNoException();
                int j = 0;
                if (bool1)
                    j = 1;
                paramParcel2.writeInt(j);
                return true;
            }
        }

        private static class Proxy
                implements IDongleRemoteInterface
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
                return "com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface";
            }

            public boolean invokeRemoteFunction(int paramInt, Bundle paramBundle1, Bundle paramBundle2)
                    throws RemoteException
            {
                int i = 1;
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                while (true)
                {
                    try
                    {
                        localParcel1.writeInterfaceToken("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
                        localParcel1.writeInt(paramInt);
                        if (paramBundle1 == null)
                            continue;
                        localParcel1.writeInt(1);
                        paramBundle1.writeToParcel(localParcel1, 0);
                        this.mRemote.transact(2, localParcel1, localParcel2, 0);
                        localParcel2.readException();
                        if (localParcel2.readInt() != 0)
                        {
                            if (localParcel2.readInt() == 0)
                                continue;
                            paramBundle2.readFromParcel(localParcel2);
                            return i;
                            localParcel1.writeInt(0);
                            continue;
                        }
                    }
                    finally
                    {
                        localParcel2.recycle();
                        localParcel1.recycle();
                    }
                    i = 0;
                }
            }

            public boolean invokeRemoteFunctionAsync(int paramInt, Bundle paramBundle, IDongleIpcResultCallBack paramIDongleIpcResultCallBack)
                    throws RemoteException
            {
                int i = 1;
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                while (true)
                {
                    try
                    {
                        localParcel1.writeInterfaceToken("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
                        localParcel1.writeInt(paramInt);
                        if (paramBundle == null)
                            continue;
                        localParcel1.writeInt(1);
                        paramBundle.writeToParcel(localParcel1, 0);
                        if (paramIDongleIpcResultCallBack != null)
                        {
                            localIBinder = paramIDongleIpcResultCallBack.asBinder();
                            localParcel1.writeStrongBinder(localIBinder);
                            this.mRemote.transact(3, localParcel1, localParcel2, 0);
                            localParcel2.readException();
                            int j = localParcel2.readInt();
                            if (j == 0)
                                break label138;
                            return i;
                            localParcel1.writeInt(0);
                            continue;
                        }
                    }
                    finally
                    {
                        localParcel2.recycle();
                        localParcel1.recycle();
                    }
                    IBinder localIBinder = null;
                    continue;
                    label138: i = 0;
                }
            }

            public boolean setRemoteDongleEventCallBack(IDongleRemoteEventCallBack paramIDongleRemoteEventCallBack)
                    throws RemoteException
            {
                int i = 1;
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken("com.iflytek.xiri.dongle.ipc.IDongleRemoteInterface");
                    IBinder localIBinder;
                    if (paramIDongleRemoteEventCallBack != null)
                    {
                        localIBinder = paramIDongleRemoteEventCallBack.asBinder();
                        localParcel1.writeStrongBinder(localIBinder);
                        this.mRemote.transact(1, localParcel1, localParcel2, 0);
                        localParcel2.readException();
                        int j = localParcel2.readInt();
                        if (j == 0)
                            break label84;
                    }
                    while (true)
                    {
                        return i;
                        localIBinder = null;
                        break;
                        label84: i = 0;
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