package com.ljt.binderdemo;

import android.os.Bundle;
import android.os.RemoteException;

import java.util.ArrayList;

/**
 * Created by 1 on 2017/9/5.
 */

public class DongleRemoteInterfaceImpl extends IDongleRemoteInterface.Stub
{
    private static final String INVOKE_PARAM1 = "invoke_param1";
    private static final String INVOKE_PARAM2 = "invoke_param2";
    private static final String INVOKE_PARAM3 = "invoke_param3";
    private static final String INVOKE_RET = "return";
    private static final String TAG = "DongleRemoteInterfaceImpl";
    private DongleManager dongleManager = DongleManager.getInstance();
    private ArrayList<IDongleRemoteEventCallBack> mEventCallbackList = new ArrayList();

    private boolean convertDongleInfoToDongleParams(DeviceBase.DeviceInfo paramDeviceInfo, Bundle paramBundle)
    {
        if ((paramDeviceInfo == null) || (paramBundle == null))
            return false;
        paramBundle.putInt("dongle_id", paramDeviceInfo.dongleID);
        paramBundle.putInt("vid", paramDeviceInfo.vendorID);
        paramBundle.putInt("pid", paramDeviceInfo.productID);
        paramBundle.putInt("dongle_type", paramDeviceInfo.dongleType.ordinal());
        paramBundle.putString("dongle_uuid", paramDeviceInfo.dongleUUID);
        paramBundle.putString("device_name", paramDeviceInfo.deviceName);
        return true;
    }

    public boolean invokeRemoteFunction(int paramInt, Bundle paramBundle1, Bundle paramBundle2)
            throws RemoteException
    {
        if (paramBundle2 == null)
        {
            MyLog.Log.e("DongleRemoteInterfaceImpl", "result params is null!");
            return false;
        }
        switch (paramInt)
        {
            default:
                MyLog.logD("DongleRemoteInterfaceImpl", "unsupport funcID: " + paramInt);
                return false;
            case 1:
                MyLog.logD("DongleRemoteInterfaceImpl", "invokeRemoteFunction, FUNC_GET_DONGLE_LIST");
                ArrayList localArrayList = this.dongleManager.getDongleList();
                int[] arrayOfInt = new int[localArrayList.size()];
                for (int i4 = 0; i4 < arrayOfInt.length; i4++)
                    arrayOfInt[i4] = ((DeviceBase.DeviceInfo)localArrayList.get(i4)).dongleID;
                paramBundle2.putBoolean("return", true);
                paramBundle2.putIntArray("dongle_list", arrayOfInt);
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
        }
        while (true)
        {
            return true;
            if (paramBundle1 == null)
                continue;
            MyLog.logD("DongleRemoteInterfaceImpl", "invokeRemoteFunction, FUNC_GET_DONGLE_INFO");
            int i3 = paramBundle1.getInt("invoke_param1", -1);
            DeviceBase.DeviceInfo localDeviceInfo = this.dongleManager.getDongleInfo(i3);
            if (localDeviceInfo != null)
            {
                paramBundle2.putBoolean("return", true);
                convertDongleInfoToDongleParams(localDeviceInfo, paramBundle2);
                continue;
            }
            paramBundle2.putBoolean("return", false);
            continue;
            if (paramBundle1 == null)
                continue;
            MyLog.logD("DongleRemoteInterfaceImpl", "invokeRemoteFunction, FUNC_SEND_IR_CODE");
            int i2 = paramBundle1.getInt("invoke_param1", -1);
            byte[] arrayOfByte2 = paramBundle1.getByteArray("invoke_param2");
            paramBundle2.putBoolean("return", this.dongleManager.sendIrCode(i2, arrayOfByte2));
            continue;
            if (paramBundle1 == null)
                continue;
            MyLog.logD("DongleRemoteInterfaceImpl", "invokeRemoteFunction, FUNC_SAVE_IR_TO_RC");
            int n = paramBundle1.getInt("invoke_param1");
            int i1 = paramBundle1.getInt("invoke_param2");
            byte[] arrayOfByte1 = paramBundle1.getByteArray("invoke_param3");
            paramBundle2.putBoolean("return", this.dongleManager.saveIrCodeToRC(n, i1, arrayOfByte1));
            continue;
            if (paramBundle1 == null)
                continue;
            MyLog.logD("DongleRemoteInterfaceImpl", "invokeRemoteFunction, FUNC_READ_IR_FROM_RC");
            int k = paramBundle1.getInt("invoke_param1");
            int m = paramBundle1.getInt("invoke_param2");
            IrCodeDataAssist.IrCodeDataInfo localIrCodeDataInfo = new IrCodeDataAssist.IrCodeDataInfo();
            if (this.dongleManager.readIrCodeFromRC(k, m, localIrCodeDataInfo) == 0)
            {
                paramBundle2.putBoolean("return", true);
                paramBundle2.putInt("freq", localIrCodeDataInfo.freq);
                paramBundle2.putByteArray("ircode_bytes", localIrCodeDataInfo.irCodeBytes);
                continue;
            }
            paramBundle2.putBoolean("return", false);
            continue;
            if (paramBundle1 == null)
                continue;
            MyLog.logD("DongleRemoteInterfaceImpl", "invokeRemoteFunction, FUNC_DELETE_IR_FROM_RC");
            int i = paramBundle1.getInt("invoke_param1");
            int j = paramBundle1.getInt("invoke_param2");
            paramBundle2.putBoolean("return", this.dongleManager.deleteIrCodeFromRC(i, j));
        }
    }

    public boolean invokeRemoteFunctionAsync(int paramInt, Bundle paramBundle, IDongleIpcResultCallBack paramIDongleIpcResultCallBack)
            throws RemoteException
    {
        MyLog.logD("DongleRemoteInterfaceImpl", "invokeRemoteFunctionAsync, fundID = " + paramInt);
        switch (paramInt)
        {
            default:
                return false;
            case 1000:
        }
        if (paramBundle != null)
            new Thread(new Runnable(paramBundle.getInt("invoke_param1", -1), paramIDongleIpcResultCallBack)
            {
                public void run()
                {
                    IrCodeDataAssist.IrCodeDataInfo localIrCodeDataInfo = new IrCodeDataAssist.IrCodeDataInfo();
                    int i = DongleRemoteInterfaceImpl.this.dongleManager.learnIrCode(this.val$dongleID, localIrCodeDataInfo);
                    Bundle localBundle = new Bundle();
                    localBundle.putInt("return", i);
                    if (i == 0)
                    {
                        localBundle.putInt("freq", localIrCodeDataInfo.freq);
                        localBundle.putByteArray("ircode_bytes", localIrCodeDataInfo.irCodeBytes);
                    }
                    if (this.val$callback != null);
                    try
                    {
                        this.val$callback.onReturn(localBundle);
                        return;
                    }
                    catch (RemoteException localRemoteException)
                    {
                    }
                }
            }).start();
        return true;
    }

    public void notifyDongleClose(int paramInt)
    {
        MyLog.logD("DongleRemoteInterfaceImpl", "notify dongle close: " + paramInt);
        ArrayList localArrayList = this.mEventCallbackList;
        monitorenter;
        int i = 0;
        try
        {
            while (i < this.mEventCallbackList.size())
            {
                IDongleRemoteEventCallBack localIDongleRemoteEventCallBack = (IDongleRemoteEventCallBack)this.mEventCallbackList.get(i);
                try
                {
                    localIDongleRemoteEventCallBack.onDongleClose(paramInt);
                    i++;
                }
                catch (RemoteException localRemoteException)
                {
                    MyLog.logE("DongleRemoteInterfaceImpl", "remote exception: " + localRemoteException.getMessage());
                    this.mEventCallbackList.remove(i);
                }
            }
        }
        finally
        {
            monitorexit;
        }
        monitorexit;
    }

    public void notifyDongleKeyEvent(int paramInt1, int paramInt2, int paramInt3)
    {
        MyLog.logD("DongleRemoteInterfaceImpl", "notify dongle key event: " + paramInt1 + ", keyCode = " + paramInt2 + ", keyStatus = " + paramInt3);
        ArrayList localArrayList = this.mEventCallbackList;
        monitorenter;
        int i = 0;
        try
        {
            while (i < this.mEventCallbackList.size())
            {
                IDongleRemoteEventCallBack localIDongleRemoteEventCallBack = (IDongleRemoteEventCallBack)this.mEventCallbackList.get(i);
                try
                {
                    localIDongleRemoteEventCallBack.onDongleKeyEvent(paramInt1, paramInt2, paramInt3);
                    i++;
                }
                catch (RemoteException localRemoteException)
                {
                    MyLog.logE("DongleRemoteInterfaceImpl", "remote exception: " + localRemoteException.getMessage());
                    this.mEventCallbackList.remove(i);
                }
            }
        }
        finally
        {
            monitorexit;
        }
        monitorexit;
    }

    public void notifyDongleOpen(int paramInt, DeviceBase.DeviceInfo paramDeviceInfo)
    {
        MyLog.logD("DongleRemoteInterfaceImpl", "notify dongle open: " + paramInt);
        ArrayList localArrayList = this.mEventCallbackList;
        monitorenter;
        int i = 0;
        try
        {
            while (i < this.mEventCallbackList.size())
            {
                IDongleRemoteEventCallBack localIDongleRemoteEventCallBack = (IDongleRemoteEventCallBack)this.mEventCallbackList.get(i);
                try
                {
                    Bundle localBundle = new Bundle();
                    convertDongleInfoToDongleParams(paramDeviceInfo, localBundle);
                    localIDongleRemoteEventCallBack.onDongleOpen(paramInt, localBundle);
                    i++;
                }
                catch (RemoteException localRemoteException)
                {
                    MyLog.logE("DongleRemoteInterfaceImpl", "remote exception: " + localRemoteException.getMessage());
                    this.mEventCallbackList.remove(i);
                }
            }
        }
        finally
        {
            monitorexit;
        }
        monitorexit;
    }

    public boolean setRemoteDongleEventCallBack(IDongleRemoteEventCallBack paramIDongleRemoteEventCallBack)
            throws RemoteException
    {
        if (paramIDongleRemoteEventCallBack == null)
            return false;
        synchronized (this.mEventCallbackList)
        {
            this.mEventCallbackList.add(paramIDongleRemoteEventCallBack);
            return true;
        }
    }
}