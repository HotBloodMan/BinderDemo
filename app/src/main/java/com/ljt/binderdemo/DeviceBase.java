package com.ljt.binderdemo;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by 1 on 2017/9/5.
 */

public abstract class DeviceBase  implements Basebase
{
    private static final String TAG = "DeviceBase";
    private boolean bIsDeviceReady = false;
    protected RecvDataEvent ignoreRecvDataEvent = new RecvDataEvent()
    {
        public void onRecvData(byte[] paramArrayOfByte, int paramInt)
        {
            MyLog.say_d("DeviceBase", "ignore recv data packet ...");
        }
    };
    protected VoiceDataEvent ignoreVoiceDataEvent = new VoiceDataEvent()
    {
        public void onRecordFinish()
        {
        }

        public void onRecvData(DeviceBase.DEVICE_VOICE_CODEC paramDEVICE_VOICE_CODEC, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
        {
            MyLog.say_d("DeviceBase", "ignore voice data packets ...");
        }
    };
    private DeviceInfo mDeviceInfo = new DeviceInfo();
    private DeviceIspEvent mDeviceIspEvent = null;
    private Handler mIspEventHandler = null;
    private HandlerThread mIspEventThread = null;

    public abstract boolean changeKeyScanCode(DongleKeyDefines paramDongleKeyDefines, int paramInt);

    public void closeDevice()
    {
        monitorenter;
        try
        {
            this.mDeviceIspEvent = null;
            this.mIspEventHandler = null;
            if (this.mIspEventThread != null)
            {
                this.mIspEventThread.quit();
                this.mIspEventThread = null;
            }
            this.mDeviceInfo.clear();
            this.bIsDeviceReady = false;
            return;
        }
        finally
        {
            monitorexit;
        }
        throw localObject;
    }

    protected final String convertBytesToMarketInfo(byte[] paramArrayOfByte)
    {
        if (paramArrayOfByte == null)
            return null;
        for (int i = 0; ; i++)
            if ((i >= paramArrayOfByte.length) || (paramArrayOfByte[i] == 0))
                return new String(paramArrayOfByte, 0, i);
    }

    public abstract boolean deleteIrFromRC(int paramInt);

    public final int getDeviceID()
    {
        return this.mDeviceInfo.dongleID;
    }

    public final DeviceInfo getDeviceInfo()
    {
        return this.mDeviceInfo;
    }

    public abstract DongleTypes getDeviceType();

    public int getDongleSectionSize()
    {
        return 32;
    }

    protected abstract int getRCStatus();

    public boolean isDeviceReady()
    {
        return this.bIsDeviceReady;
    }

    public abstract boolean isWorking();

    public abstract int learnIrCode(IrCodeDataAssist.IrCodeDataInfo paramIrCodeDataInfo);

    protected final void onRecvIspKeyEvent(DongleKeyDefines paramDongleKeyDefines, int paramInt)
    {
        DeviceIspEvent localDeviceIspEvent = this.mDeviceIspEvent;
        if (localDeviceIspEvent == null)
            return;
        switch (paramInt)
        {
            default:
                MyLog.say_e("DeviceBase", "unknown key status!");
                return;
            case 0:
                runInIspDispatchThread(new Runnable(localDeviceIspEvent, paramDongleKeyDefines)
                {
                    public void run()
                    {
                        this.val$ispEvent.onKeyUp(DeviceBase.this.mDeviceInfo.dongleID, this.val$dongleKeyCode);
                    }
                });
                return;
            case 1:
        }
        runInIspDispatchThread(new Runnable(localDeviceIspEvent, paramDongleKeyDefines)
        {
            public void run()
            {
                this.val$ispEvent.onKeyDown(DeviceBase.this.mDeviceInfo.dongleID, this.val$dongleKeyCode);
            }
        });
    }

    public final boolean openDevice(int paramInt1, int paramInt2, int paramInt3, String paramString, DeviceIspEvent paramDeviceIspEvent)
    {
        monitorenter;
        try
        {
            this.bIsDeviceReady = true;
            this.mDeviceIspEvent = paramDeviceIspEvent;
            Object[] arrayOfObject = new Object[1];
            arrayOfObject[0] = Long.valueOf(SystemClock.elapsedRealtime());
            this.mIspEventThread = new HandlerThread(String.format("isp_thread_%d", arrayOfObject));
            this.mIspEventThread.start();
            this.mIspEventHandler = new Handler(this.mIspEventThread.getLooper());
            stopIspVoiceRecord();
            this.mDeviceInfo.dongleID = paramInt1;
            this.mDeviceInfo.dongleType = getDeviceType();
            this.mDeviceInfo.vendorID = paramInt2;
            this.mDeviceInfo.productID = paramInt3;
            this.mDeviceInfo.deviceName = paramString;
            this.mDeviceInfo.dongleUUID = UUIDAssist.uuid_to_string(readFromDongle(0));
            this.mDeviceInfo.dongleMarketInfo = convertBytesToMarketInfo(readDongleMarketInfo());
            this.mDeviceInfo.rcStatus = getRCStatus();
            if (this.mDeviceInfo.rcStatus == 1)
            {
                if (this.mDeviceInfo.rcUUID == null)
                    this.mDeviceInfo.rcUUID = UUIDAssist.uuid_to_string(readRCUUID());
                if (this.mDeviceInfo.rcMarketInfo == null)
                    this.mDeviceInfo.rcMarketInfo = convertBytesToMarketInfo(readRCMarketInfo());
            }
            return true;
        }
        finally
        {
            monitorexit;
        }
        throw localObject;
    }

    public abstract int queryKeyIspMode(DongleKeyDefines paramDongleKeyDefines);

    public abstract int queryKeyScanCode(DongleKeyDefines paramDongleKeyDefines);

    public abstract byte[] readDongleMarketInfo();

    public abstract byte[] readFromDongle(int paramInt);

    public abstract int readIrFromRC(int paramInt, IrCodeDataAssist.IrCodeDataInfo paramIrCodeDataInfo);

    public abstract int readIrInfoFromRC(int paramInt, IrCodeDataAssist.IrCodeDataInfo paramIrCodeDataInfo);

    public abstract byte[] readRCMarketInfo();

    public abstract byte[] readRCUUID();

    protected final void recvRcStatusChange(int paramInt)
    {
        this.mDeviceInfo.rcStatus = paramInt;
        runInIspDispatchThread(new Runnable(paramInt)
        {
            public void run()
            {
                if ((DeviceBase.this.mDeviceInfo.rcStatus == 1) && (!DeviceBase.this.isWorking()))
                {
                    if (DeviceBase.this.mDeviceInfo.rcUUID == null)
                        DeviceBase.this.mDeviceInfo.rcUUID = UUIDAssist.uuid_to_string(DeviceBase.this.readRCUUID());
                    if (DeviceBase.this.mDeviceInfo.rcMarketInfo == null)
                        DeviceBase.this.mDeviceInfo.rcMarketInfo = DeviceBase.this.convertBytesToMarketInfo(DeviceBase.this.readRCMarketInfo());
                }
                DeviceBase.DeviceIspEvent localDeviceIspEvent = DeviceBase.this.mDeviceIspEvent;
                if (localDeviceIspEvent != null)
                    localDeviceIspEvent.onRcStatusChange(DeviceBase.this.mDeviceInfo.dongleID, this.val$rcStatus);
            }
        });
    }

    public abstract boolean restoreAllKeyScanCodes();

    public abstract boolean restoreKeyIspMode(DongleKeyDefines paramDongleKeyDefines);

    public abstract boolean restoreKeyScanCode(DongleKeyDefines paramDongleKeyDefines);

    protected final void runInIspDispatchThread(Runnable paramRunnable)
    {
        if (this.mIspEventHandler != null)
            this.mIspEventHandler.post(paramRunnable);
    }

    public abstract boolean saveIrToRC(int paramInt, byte[] paramArrayOfByte);

    public abstract boolean saveUUIDToRC(byte[] paramArrayOfByte);

    public abstract boolean sendIrCode(byte[] paramArrayOfByte);

    public abstract boolean setKeyIspMode(DongleKeyDefines paramDongleKeyDefines);

    public abstract boolean simulateKeyEvent(int paramInt1, int paramInt2);

    public abstract boolean startIspVoiceRecord(VoiceDataEvent paramVoiceDataEvent);

    public abstract boolean stopIspVoiceRecord();

    public abstract boolean writeToDongle(int paramInt, byte[] paramArrayOfByte);

    public static enum DEVICE_VOICE_CODEC
    {
        static
        {
            DEVICE_VOICE_CODEC[] arrayOfDEVICE_VOICE_CODEC = new DEVICE_VOICE_CODEC[2];
            arrayOfDEVICE_VOICE_CODEC[0] = ico_codec;
            arrayOfDEVICE_VOICE_CODEC[1] = siren7_codec;
            $VALUES = arrayOfDEVICE_VOICE_CODEC;
        }
    }

    public static class DeviceInfo
            implements Cloneable
    {
        public String deviceName;
        public int dongleID;
        public String dongleMarketInfo;
        public DongleTypes dongleType;
        public String dongleUUID;
        public int productID;
        public String rcMarketInfo;
        public int rcStatus;
        public String rcUUID;
        public int vendorID;

        public DeviceInfo()
        {
            clear();
        }

        public void clear()
        {
            this.dongleID = -1;
            this.dongleType = DongleTypes.dongle_unknown;
            this.vendorID = -1;
            this.productID = -1;
            this.dongleUUID = null;
            this.dongleMarketInfo = null;
            this.deviceName = null;
            this.rcStatus = 0;
            this.rcUUID = null;
            this.rcMarketInfo = null;
        }

        public Object clone()
        {
            try
            {
                DeviceInfo localDeviceInfo = (DeviceInfo)super.clone();
                return localDeviceInfo;
            }
            catch (CloneNotSupportedException localCloneNotSupportedException)
            {
            }
            return null;
        }
    }

    public static abstract interface DeviceIspEvent
    {
        public abstract void onKeyDown(int paramInt, DongleKeyDefines paramDongleKeyDefines);

        public abstract void onKeyUp(int paramInt, DongleKeyDefines paramDongleKeyDefines);

        public abstract void onRcStatusChange(int paramInt1, int paramInt2);
    }

    public static abstract interface RecvDataEvent
    {
        public abstract void onRecvData(byte[] paramArrayOfByte, int paramInt);
    }

    public static abstract interface VoiceDataEvent
    {
        public abstract void onRecordFinish();

        public abstract void onRecvData(DeviceBase.DEVICE_VOICE_CODEC paramDEVICE_VOICE_CODEC, byte[] paramArrayOfByte, int paramInt1, int paramInt2);
    }
}