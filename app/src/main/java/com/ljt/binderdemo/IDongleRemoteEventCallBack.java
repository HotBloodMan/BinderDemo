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

public abstract interface IDongleRemoteEventCallBack extends IInterface
{
    public abstract void onDongleClose(int paramInt)
            throws RemoteException;

    public abstract void onDongleKeyEvent(int paramInt1, int paramInt2, int paramInt3)
            throws RemoteException;

    public abstract void onDongleOpen(int paramInt, Bundle paramBundle)
            throws RemoteException;

    public static abstract class Stub extends Binder
            implements IDongleRemoteEventCallBack
    {
        private static final String DESCRIPTOR = "com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack";
        static final int TRANSACTION_onDongleClose = 3;
        static final int TRANSACTION_onDongleKeyEvent = 2;
        static final int TRANSACTION_onDongleOpen = 1;

        public Stub()
        {
            attachInterface(this, "com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
        }

        public static IDongleRemoteEventCallBack asInterface(IBinder paramIBinder)
        {
            if (paramIBinder == null)
                return null;
            IInterface localIInterface = paramIBinder.queryLocalInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
            if ((localIInterface != null) && ((localIInterface instanceof IDongleRemoteEventCallBack)))
                return (IDongleRemoteEventCallBack)localIInterface;
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
                    paramParcel2.writeString("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
                    return true;
                case 1:
                    paramParcel1.enforceInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
                    int i = paramParcel1.readInt();
                    if (paramParcel1.readInt() != 0);
                    for (Bundle localBundle = (Bundle)Bundle.CREATOR.createFromParcel(paramParcel1); ; localBundle = null)
                    {
                        onDongleOpen(i, localBundle);
                        paramParcel2.writeNoException();
                        return true;
                    }
                case 2:
                    paramParcel1.enforceInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
                    onDongleKeyEvent(paramParcel1.readInt(), paramParcel1.readInt(), paramParcel1.readInt());
                    paramParcel2.writeNoException();
                    return true;
                case 3:
            }
            paramParcel1.enforceInterface("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
            onDongleClose(paramParcel1.readInt());
            paramParcel2.writeNoException();
            return true;
        }

        private static class Proxy
                implements IDongleRemoteEventCallBack
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
                return "com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack";
            }

            public void onDongleClose(int paramInt)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
                    localParcel1.writeInt(paramInt);
                    this.mRemote.transact(3, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    return;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
                throw localObject;
            }

            public void onDongleKeyEvent(int paramInt1, int paramInt2, int paramInt3)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
                    localParcel1.writeInt(paramInt1);
                    localParcel1.writeInt(paramInt2);
                    localParcel1.writeInt(paramInt3);
                    this.mRemote.transact(2, localParcel1, localParcel2, 0);
                    localParcel2.readException();
                    return;
                }
                finally
                {
                    localParcel2.recycle();
                    localParcel1.recycle();
                }
                throw localObject;
            }

            public void onDongleOpen(int paramInt, Bundle paramBundle)
                    throws RemoteException
            {
                Parcel localParcel1 = Parcel.obtain();
                Parcel localParcel2 = Parcel.obtain();
                try
                {
                    localParcel1.writeInterfaceToken("com.iflytek.xiri.dongle.ipc.IDongleRemoteEventCallBack");
                    localParcel1.writeInt(paramInt);
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