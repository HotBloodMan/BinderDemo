package com.ljt.binderdemo;

import android.app.Service;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.os.IBinder;

/**
 * Created by 1 on 2017/9/5.
 */

public class DongleRemoteService extends Service
{
    private static final String TAG = "DongleService";
    private DongleRemoteInterfaceImpl mDongleRemoteInterface = new DongleRemoteInterfaceImpl();

    public IBinder onBind(Intent paramIntent)
    {
        return this.mDongleRemoteInterface;
    }

    public void onCreate()
    {
        super.onCreate();
        MyLog.logE("DongleService", "onCreate...");
        DongleManager.getInstance().addDongleCallBack(new DongleManager.DongleManagerEvent()
        {
            public void onDongleClose(int paramInt)
            {
                DongleRemoteService.this.mDongleRemoteInterface.notifyDongleClose(paramInt);
            }

            public void onDongleKeyDown(int paramInt, DongleKeyDefines paramDongleKeyDefines)
            {
                DongleRemoteService.this.mDongleRemoteInterface.notifyDongleKeyEvent(paramInt, paramDongleKeyDefines.ordinal(), 1);
            }

            public void onDongleKeyUp(int paramInt, DongleKeyDefines paramDongleKeyDefines)
            {
                DongleRemoteService.this.mDongleRemoteInterface.notifyDongleKeyEvent(paramInt, paramDongleKeyDefines.ordinal(), 0);
            }

            public void onDongleOpen(int paramInt, DeviceBase.DeviceInfo paramDeviceInfo)
            {
                DongleRemoteService.this.mDongleRemoteInterface.notifyDongleOpen(paramInt, paramDeviceInfo);
            }

            public void onRemoteStatusChange(int paramInt1, int paramInt2)
            {
            }

            public void requestForUsbPermission(UsbDevice paramUsbDevice)
            {
            }
        });
        DongleManager.getInstance().init(this);
    }

    public void onDestroy()
    {
        MyLog.logE("DongleService", "onDestroy ...");
        super.onDestroy();
    }

    public int onStartCommand(Intent paramIntent, int paramInt1, int paramInt2)
    {
        return super.onStartCommand(paramIntent, paramInt1, paramInt2);
    }
}