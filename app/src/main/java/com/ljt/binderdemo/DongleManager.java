package com.ljt.binderdemo;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.text.TextUtils;
import android.widget.Toast;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by 1 on 2017/9/5.
 */

public class DongleManager {
    private static final String ACTION_SCREENOFF = "android.intent.action.SCREEN_OFF";
    private static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";
    private static final String ACTION_SHOTDOWN = "android.intent.action.ACTION_SHUTDOWN";
    private static final String ACTION_USB_PERMISSION = "com.iflytek.request.USB_PERMISSION";
    public static final int DEVICE_ID_VENDOR_BLE = 0;
    private static final String TAG = "DongleManager";
    private static final String USB_CONFIRM_ACTIVITY = "com.android.systemui.usb.UsbConfirmActivity";
    private static final String USB_PERSSION_ACTIVITY = "com.android.systemui.usb.UsbPermissionActivity";
    private static DongleManager instance = null;
    private final String INIT_RC_UUID = "ffffffffffffffffffffffffffffffff";
    private BroadcastReceiver bc = new BroadcastReceiver()
    {
        public void onReceive(Context paramContext, Intent paramIntent)
        {
            String str = paramIntent.getAction();
            MyLog.logD("DongleManager", "usb action = " + str);
            if ("android.hardware.usb.action.USB_DEVICE_ATTACHED".equals(str))
            {
                UsbDevice localUsbDevice3 = (UsbDevice)paramIntent.getParcelableExtra("device");
                DongleManager.this.onUsbDevicePlugIn(localUsbDevice3);
            }
            do
                while (true)
                {
                    return;
                    if ("com.iflytek.request.USB_PERMISSION".equals(str))
                    {
                        UsbDevice localUsbDevice2 = (UsbDevice)paramIntent.getParcelableExtra("device");
                        if (paramIntent.getBooleanExtra("permission", false))
                        {
                            MyLog.logD("DongleManager", "permission ok");
                            DongleManager.this.onUsbDevicePlugIn(localUsbDevice2);
                            return;
                        }
                        DongleManager.this.askForUsbPermission(localUsbDevice2);
                        return;
                    }
                    if ("android.hardware.usb.action.USB_DEVICE_DETACHED".equals(str))
                    {
                        UsbDevice localUsbDevice1 = (UsbDevice)paramIntent.getParcelableExtra("device");
                        DongleManager.getInstance().onUsbDevicePlugOut(localUsbDevice1);
                        return;
                    }
                    if ((!"android.intent.action.ACTION_SHUTDOWN".equals(paramIntent.getAction())) && (!"android.intent.action.SCREEN_OFF".equals(paramIntent.getAction())))
                        break;
                    if (DongleManager.this.mPowerIrCodeManager == null)
                        continue;
                    DongleManager.this.mPowerIrCodeManager.receiveShutDownAndScreenOn(0);
                    return;
                }
            while ((!"android.intent.action.SCREEN_ON".equals(paramIntent.getAction())) || (DongleManager.this.mPowerIrCodeManager == null));
            DongleManager.this.mPowerIrCodeManager.receiveShutDownAndScreenOn(1);
        }
    };
    private boolean isInited = false;
    private Context mContext;
    SharedPreferences.Editor mDevicesEditor;
    DongleIflytekYoung mDongleIflytekYoung = null;
    private DeviceBase.DeviceIspEvent mDongleIspEvent;
    private DongleManagerEvent mDongleManagerEvent = new DongleManagerEvent()
    {
        public void onDongleClose(int paramInt)
        {
            MyLog.logD("DongleManager", "onDongleClose, dongleID=" + paramInt);
            DongleManager.this.mHandler.post(new Runnable(paramInt)
            {
                public void run()
                {
                    if (DongleManager.this.mDongleManagerEvents != null)
                        for (int i = 0; i < DongleManager.this.mDongleManagerEvents.size(); i++)
                        {
                            if (DongleManager.this.mDongleManagerEvents.get(i) == null)
                                continue;
                            ((DongleManager.DongleManagerEvent)DongleManager.this.mDongleManagerEvents.get(i)).onDongleClose(this.val$dongleID);
                        }
                    if (DongleManager.this.mPowerIrCodeManager != null)
                        DongleManager.this.mPowerIrCodeManager.dongleClose(this.val$dongleID);
                    DongleManager.this.mOpts.remove(this.val$dongleID);
                }
            });
        }

        public void onDongleKeyDown(int paramInt, DongleKeyDefines paramDongleKeyDefines)
        {
            DongleManager.this.mHandler.post(new Runnable(paramInt, paramDongleKeyDefines)
            {
                public void run()
                {
                    if (DongleManager.this.mDongleManagerEvents != null)
                    {
                        Log.d("DongleManager", "mDongleManagerEvents.size():" + DongleManager.this.mDongleManagerEvents.size());
                        for (int i = 0; i < DongleManager.this.mDongleManagerEvents.size(); i++)
                        {
                            if (DongleManager.this.mDongleManagerEvents.get(i) == null)
                                continue;
                            ((DongleManager.DongleManagerEvent)DongleManager.this.mDongleManagerEvents.get(i)).onDongleKeyDown(this.val$dongleID, this.val$dongleKeyCode);
                        }
                    }
                    if ((this.val$dongleKeyCode == DongleKeyDefines.key_power) && (DongleManager.this.mPowerIrCodeManager != null))
                        DongleManager.this.mPowerIrCodeManager.clickPowerKey(this.val$dongleID);
                }
            });
        }

        public void onDongleKeyUp(int paramInt, DongleKeyDefines paramDongleKeyDefines)
        {
            DongleManager.this.mHandler.post(new Runnable(paramInt, paramDongleKeyDefines)
            {
                public void run()
                {
                    if (DongleManager.this.mDongleManagerEvents != null)
                        for (int i = 0; i < DongleManager.this.mDongleManagerEvents.size(); i++)
                        {
                            if (DongleManager.this.mDongleManagerEvents.get(i) == null)
                                continue;
                            ((DongleManager.DongleManagerEvent)DongleManager.this.mDongleManagerEvents.get(i)).onDongleKeyUp(this.val$dongleID, this.val$dongleKeyCode);
                        }
                }
            });
        }

        public void onDongleOpen(int paramInt, DeviceBase.DeviceInfo paramDeviceInfo)
        {
            MyLog.logE("DongleManager", "dongleMarketInfo: " + paramDeviceInfo.dongleMarketInfo + ", rcMarketInfo: " + paramDeviceInfo.rcMarketInfo);
            DongleManager.this.mHandler.post(new Runnable(paramInt, paramDeviceInfo)
            {
                public void run()
                {
                    if (DongleManager.this.mDongleManagerEvents != null)
                        for (int i = 0; i < DongleManager.this.mDongleManagerEvents.size(); i++)
                        {
                            if (DongleManager.this.mDongleManagerEvents.get(i) == null)
                                continue;
                            ((DongleManager.DongleManagerEvent)DongleManager.this.mDongleManagerEvents.get(i)).onDongleOpen(this.val$dongleID, this.val$dongleInfo);
                        }
                    if (DongleManager.this.mPowerIrCodeManager != null)
                        DongleManager.this.mPowerIrCodeManager.dongleOpen(this.val$dongleID);
                    if ((this.val$dongleInfo.rcStatus == 1) && (!"ffffffffffffffffffffffffffffffff".equals(this.val$dongleInfo.rcUUID)))
                    {
                        DongleManager.this.mOpts.put(this.val$dongleID, new DongleManager.CtlOpted(DongleManager.this, true, false));
                        MyLog.logD("DongleManager", "dongleInfo.rcStatus == 1 isReportedUUID = " + ((DongleManager.CtlOpted)DongleManager.this.mOpts.get(this.val$dongleID)).isReportRcUuid);
                        DeviceBase.DeviceInfo localDeviceInfo = this.val$dongleInfo;
                        if (this.val$dongleInfo.rcUUID == null);
                        for (String str = ""; ; str = this.val$dongleInfo.rcUUID)
                        {
                            localDeviceInfo.rcUUID = str;
                            if (!DongleManager.this.pudateUUID.equals(this.val$dongleInfo.rcUUID))
                            {
                                DongleManager.access$702(DongleManager.this, this.val$dongleInfo.rcUUID);
                                Collector.getInstance().reportCtlInsert(this.val$dongleInfo.rcUUID, String.valueOf(this.val$dongleInfo.productID), String.valueOf(this.val$dongleInfo.vendorID), String.valueOf(this.val$dongleInfo.dongleID), this.val$dongleInfo.dongleMarketInfo, this.val$dongleInfo.dongleUUID, this.val$dongleInfo.rcMarketInfo, String.valueOf(this.val$dongleInfo.dongleType));
                            }
                            return;
                        }
                    }
                    if ("ffffffffffffffffffffffffffffffff".equals(DongleManager.this.pudateUUID))
                        Collector.getInstance().reportCtlInsert("ctl_unconnected_dongle", String.valueOf(this.val$dongleInfo.productID), String.valueOf(this.val$dongleInfo.vendorID), String.valueOf(this.val$dongleInfo.dongleID), this.val$dongleInfo.dongleMarketInfo, this.val$dongleInfo.dongleUUID, this.val$dongleInfo.rcMarketInfo, String.valueOf(this.val$dongleInfo.dongleType));
                    DongleManager.this.mOpts.put(this.val$dongleID, new DongleManager.CtlOpted(DongleManager.this, false, false));
                    MyLog.logD("DongleManager", "dongleInfo.rcStatus == 0 isReportedUUID = " + ((DongleManager.CtlOpted)DongleManager.this.mOpts.get(this.val$dongleID)).isReportRcUuid);
                }
            });
        }

        public void onRemoteStatusChange(int paramInt1, int paramInt2)
        {
            DongleManager.this.mHandler.post(new Runnable(paramInt1, paramInt2)
            {
                public void run()
                {
                    if (DongleManager.this.mDongleManagerEvents != null)
                        for (int i = 0; i < DongleManager.this.mDongleManagerEvents.size(); i++)
                        {
                            if (DongleManager.this.mDongleManagerEvents.get(i) == null)
                                continue;
                            ((DongleManager.DongleManagerEvent)DongleManager.this.mDongleManagerEvents.get(i)).onRemoteStatusChange(this.val$dongleID, this.val$rcStatus);
                        }
                    DeviceBase.DeviceInfo localDeviceInfo;
                    String str;
                    if (this.val$rcStatus == 1)
                    {
                        localDeviceInfo = DongleManager.this.getDongleInfo(this.val$dongleID);
                        if (localDeviceInfo != null)
                        {
                            MyLog.logD("DongleManager", "dongleMarketInfo: " + localDeviceInfo.dongleMarketInfo + ", rcMarketInfo: " + localDeviceInfo.rcMarketInfo);
                            str = DongleManager.this.getDongleInfo(this.val$dongleID).rcUUID;
                            if (str != null)
                            {
                                MyLog.logD("DongleManager", "uuid = " + str.toLowerCase());
                                if (!"ffffffffffffffffffffffffffffffff".equals(str.toLowerCase()))
                                    break label475;
                                DongleManager.this.mHandler.post(new DongleManager.GetUUIDRunable(DongleManager.this, this.val$dongleID));
                            }
                        }
                    }
                    while (true)
                    {
                        if ((DongleManager.this.mOpts.get(this.val$dongleID) != null) && (!((DongleManager.CtlOpted)DongleManager.this.mOpts.get(this.val$dongleID)).isOpted))
                        {
                            DongleManager.this.setKeyIspMode(this.val$dongleID, DongleKeyDefines.key_mute);
                            DongleManager.this.setKeyIspMode(this.val$dongleID, DongleKeyDefines.key_translate);
                            if (DongleManager.this.mKeyMappingManager != null)
                                DongleManager.this.mKeyMappingManager.mappingKey(this.val$dongleID, 23, DongleKeyDefines.key_dpad_center);
                            if (DongleManager.this.mPowerIrCodeManager != null)
                                DongleManager.this.mPowerIrCodeManager.onRemoteStatusChange(this.val$dongleID);
                            boolean bool1 = ((DongleManager.CtlOpted)DongleManager.this.mOpts.get(this.val$dongleID)).isReportRcUuid;
                            DongleManager.this.mOpts.put(this.val$dongleID, new DongleManager.CtlOpted(DongleManager.this, bool1, true));
                        }
                        DongleManager.this.saveDevices();
                        return;
                        label475: if (DongleManager.this.mOpts.get(this.val$dongleID) == null)
                            continue;
                        MyLog.logD("DongleManager", "dongleInfo.rcStatus == 1 isReportedUUID = " + ((DongleManager.CtlOpted)DongleManager.this.mOpts.get(this.val$dongleID)).isReportRcUuid);
                        if (((DongleManager.CtlOpted)DongleManager.this.mOpts.get(this.val$dongleID)).isReportRcUuid)
                            continue;
                        DongleManager.this.getDongleInfo(this.val$dongleID);
                        Collector.getInstance().reportCtlInsert(str, String.valueOf(localDeviceInfo.productID), String.valueOf(localDeviceInfo.vendorID), String.valueOf(localDeviceInfo.dongleID), localDeviceInfo.dongleMarketInfo, localDeviceInfo.dongleUUID, localDeviceInfo.rcMarketInfo, String.valueOf(localDeviceInfo.dongleType));
                        boolean bool2 = ((DongleManager.CtlOpted)DongleManager.this.mOpts.get(this.val$dongleID)).isOpted;
                        DongleManager.this.mOpts.put(this.val$dongleID, new DongleManager.CtlOpted(DongleManager.this, true, bool2));
                    }
                }
            });
        }

        public void requestForUsbPermission(UsbDevice paramUsbDevice)
        {
            DongleManager.this.mHandler.post(new Runnable(paramUsbDevice)
            {
                public void run()
                {
                    if (XiriUtil.apkInstalled(DongleManager.this.mContext, "com.iflytek.xiri2.system"))
                        MyLog.logD("DongleManager", "com.iflytek.xiri2.system has been installed");
                    try
                    {
                        i = DongleManager.this.mContext.getPackageManager().getPackageInfo(DongleManager.this.mContext.getPackageName(), 0).applicationInfo.uid;
                        Bundle localBundle = new Bundle();
                        localBundle.putInt("uid", i);
                        localBundle.putBoolean("isFeed", true);
                        localBundle.putParcelable("usbdevice", this.val$usbDevice);
                        DongleManager.this.runInSystem(localBundle, "GETUSBPERMISSION");
                        String str;
                        do
                        {
                            do
                                return;
                            while (!TextUtils.isEmpty(SystemSignAPKManager.getInstance(DongleManager.this.mContext).getCurrentSystemSignApkName()));
                            str = TopActivityManager.getInstance(DongleManager.this.mContext).getTopActivityClsName();
                            MyLog.logD("DongleManager", "activityName = " + str + " usbdevice = " + this.val$usbDevice.toString());
                        }
                        while (("com.android.systemui.usb.UsbConfirmActivity".equals(str)) || ("com.android.systemui.usb.UsbPermissionActivity".equals(str)));
                        DongleManager.this.askForUsbPermission(this.val$usbDevice);
                        return;
                    }
                    catch (Exception localException)
                    {
                        while (true)
                            int i = 0;
                    }
                }
            });
        }
    };
    private List<DongleManagerEvent> mDongleManagerEvents;
    private SparseArray<DeviceBase> mDongleObjs = new SparseArray();
    private Handler mHandler = new Handler(Looper.getMainLooper());
    private KeyMappingManager mKeyMappingManager;
    private SparseArray<CtlOpted> mOpts = new SparseArray();
    private PowerIrCodeManager mPowerIrCodeManager;
    SharedPreferences mSharedPreferences;
    private int mSysVersionCode = -123;
    private VendorRemoteBle mVendorBleRemote = VendorRemoteBle.getInstance();
    private String pudateUUID = "ffffffffffffffffffffffffffffffff";
    private SparseArray<DongleIflytekYoung> youngObjs = new SparseArray();

    public DongleManager() {
        mDongleIspEvent = new DeviceBase.DeviceIspEvent()
        {
            public void onKeyDown(int paramInt, DongleKeyDefines paramDongleKeyDefines)
            {
                StringBuilder localStringBuilder = new StringBuilder().append("onKeyDown,mDongleManagerEvent:");
                if (DongleManager.this.mDongleManagerEvent != null);
                for (boolean bool = true; ; bool = false)
                {
                    MyLog.logD("DongleManager", bool);
                    if (DongleManager.this.mDongleManagerEvent != null)
                        DongleManager.this.mDongleManagerEvent.onDongleKeyDown(paramInt, paramDongleKeyDefines);
                    return;
                }
            }

            public void onKeyUp(int paramInt, DongleKeyDefines paramDongleKeyDefines)
            {
                MyLog.logD("DongleManager", "onKeyUp");
                if (DongleManager.this.mDongleManagerEvent != null)
                    DongleManager.this.mDongleManagerEvent.onDongleKeyUp(paramInt, paramDongleKeyDefines);
            }

            public void onRcStatusChange(int paramInt1, int paramInt2)
            {
                MyLog.logD("DongleManager", "onRcStatusChange rcStatus:" + paramInt2);
                if (DongleManager.this.mDongleManagerEvent != null)
                    DongleManager.this.mDongleManagerEvent.onRemoteStatusChange(paramInt1, paramInt2);
            }
        };
    }

    private void addDongleObject(int paramInt, DeviceBase paramDeviceBase)
    {
        synchronized (this.mDongleObjs)
        {
            this.mDongleObjs.put(paramInt, paramDeviceBase);
            return;
        }
    }

    private void addYoungObject(int paramInt, DongleIflytekYoung paramDongleIflytekYoung)
    {
        synchronized (this.youngObjs)
        {
            MyLog.logD("DongleManager", "addYoungObject: " + paramInt);
            this.youngObjs.put(paramInt, paramDongleIflytekYoung);
            return;
        }
    }

    private void askForUsbPermission(UsbDevice paramUsbDevice)
    {
        if (paramUsbDevice == null)
            return;
        UsbManager localUsbManager = (UsbManager)this.mContext.getSystemService("usb");
        if (localUsbManager == null)
        {
            MyLog.logD("DongleManager", "get usb manager service error!");
            return;
        }
        Toast.makeText(this.mContext, "勾选默认情况下用于该设备，并点击“确定”，就可以使用讯飞电视语点啦", 1).show();
        localUsbManager.requestPermission(paramUsbDevice, PendingIntent.getBroadcast(this.mContext, 0, new Intent("com.iflytek.request.USB_PERMISSION"), 0));
    }

    private int calcDongleHashID(UsbDevice paramUsbDevice)
    {
        if (paramUsbDevice == null)
            return -1;
        return paramUsbDevice.hashCode();
    }

    private void dumpDongleInfo(UsbDevice paramUsbDevice)
    {
        if (paramUsbDevice == null)
            return;
        MyLog.logD("DongleManager", "=====================");
        Object[] arrayOfObject1 = new Object[2];
        arrayOfObject1[0] = Integer.valueOf(paramUsbDevice.getVendorId());
        arrayOfObject1[1] = Integer.valueOf(paramUsbDevice.getProductId());
        MyLog.logD("DongleManager", String.format("vid=%04X, pid=%04X", arrayOfObject1));
        int i = paramUsbDevice.getInterfaceCount();
        MyLog.logD("DongleManager", "interface count = " + i);
        for (int j = 0; j < i; j++)
        {
            UsbInterface localUsbInterface = paramUsbDevice.getInterface(j);
            int k = localUsbInterface.getEndpointCount();
            Object[] arrayOfObject2 = new Object[2];
            arrayOfObject2[0] = localUsbInterface.toString();
            arrayOfObject2[1] = Integer.valueOf(k);
            MyLog.logD("DongleManager", String.format("interface %s, epcount=%d", arrayOfObject2));
            for (int m = 0; m < k; m++)
            {
                UsbEndpoint localUsbEndpoint = localUsbInterface.getEndpoint(m);
                MyLog.logD("DongleManager", "ep: " + localUsbEndpoint.toString());
            }
        }
        MyLog.logD("DongleManager", "=====================");
    }

    private DeviceBase getDongleObject(int paramInt)
    {
        synchronized (this.mDongleObjs)
        {
            DeviceBase localDeviceBase = (DeviceBase)this.mDongleObjs.get(paramInt);
            return localDeviceBase;
        }
    }

    private DongleTypes getDongleType(UsbDevice paramUsbDevice)
    {
        StringBuilder localStringBuilder = new StringBuilder().append("getDongleType device:");
        if (paramUsbDevice == null);
        for (boolean bool = true; ; bool = false)
        {
            Log.d("DongleManager", bool);
            Log.d("DongleManager", "usbDevice.getVendorId():" + paramUsbDevice.getVendorId());
            Log.d("DongleManager", "usbDevice.getProductId():" + paramUsbDevice.getProductId());
            if (paramUsbDevice != null)
                break;
            return DongleTypes.dongle_unknown;
        }
        Log.d("DongleManager", "checkIsDongleValid 11111");
        if (DongleIflytekBle.checkIsDongleValid(paramUsbDevice))
            return DongleTypes.dongle_iflytek_ble;
        Log.d("DongleManager", "checkIsDongleValid 2222");
        if (DongleIflytekYoung.checkIsDongleValid(this.mContext, paramUsbDevice))
            return DongleTypes.dongle_iflytek_young;
        return DongleTypes.dongle_unknown;
    }

    public static DongleManager getInstance()
    {
        if (instance == null)
            instance = new DongleManager();
        return instance;
    }

    private DongleIflytekYoung getYoungObject(int paramInt)
    {
        synchronized (this.youngObjs)
        {
            MyLog.logD("DongleManager", "getYoungObject: " + paramInt);
            DongleIflytekYoung localDongleIflytekYoung = (DongleIflytekYoung)this.youngObjs.get(paramInt);
            return localDongleIflytekYoung;
        }
    }

    private void removeDongleObject(int paramInt)
    {
        synchronized (this.mDongleObjs)
        {
            this.mDongleObjs.remove(paramInt);
            return;
        }
    }

    private void removeYoungObject(int paramInt)
    {
        synchronized (this.youngObjs)
        {
            MyLog.logD("DongleManager", "removeYoungObject: " + paramInt);
            this.youngObjs.remove(paramInt);
            return;
        }
    }

    private void runInSystem(Bundle paramBundle, String paramString)
    {
        if (this.mSysVersionCode < 3);
        try
        {
            this.mSysVersionCode = this.mContext.getPackageManager().getPackageInfo("com.iflytek.xiri2.system", 0).versionCode;
            label29: MyLog.logD("SystemSignAPKManager", "execute action=" + paramString + " mSysVersionCode=" + this.mSysVersionCode);
            if (this.mSysVersionCode >= 3)
            {
                Intent localIntent = new Intent();
                localIntent.setAction("com.iflytek.xiri.control");
                if (paramBundle != null)
                    localIntent.putExtras(paramBundle);
                localIntent.putExtra("_action", paramString);
                localIntent.putExtra("versioncode", Constants.getVersionCode(this.mContext));
                localIntent.putExtra("pkgname", this.mContext.getPackageName());
                localIntent.setPackage("com.iflytek.xiri2.system");
                this.mContext.startService(localIntent);
                MyLog.logD("SystemSignAPKManager", "startService intent=" + localIntent.toURI());
            }
            return;
        }
        catch (Exception localException)
        {
            break label29;
        }
    }

    private void saveDevices()
    {
        ArrayList localArrayList = getDongleList();
        JSONArray localJSONArray = new JSONArray();
        Iterator localIterator = localArrayList.iterator();
        while (localIterator.hasNext())
        {
            DeviceBase.DeviceInfo localDeviceInfo = (DeviceBase.DeviceInfo)localIterator.next();
            String str1 = localDeviceInfo.vendorID + "";
            String str2 = localDeviceInfo.productID + "";
            String str3 = localDeviceInfo.rcMarketInfo;
            String str4 = localDeviceInfo.dongleMarketInfo;
            String str5 = str2 + "_" + str1;
            try
            {
                JSONObject localJSONObject = new JSONObject();
                localJSONObject.put("pid", str2);
                localJSONObject.put("vid", str1);
                localJSONObject.put("number", str5);
                localJSONObject.put("rc_ver", str3);
                localJSONObject.put("dongle_ver", str4);
                localJSONArray.put(localJSONObject);
            }
            catch (JSONException localJSONException)
            {
                localJSONException.printStackTrace();
            }
        }
        if (this.mDevicesEditor == null)
        {
            Context localContext = this.mContext;
            this.mSharedPreferences = localContext.getSharedPreferences("DEVICES", 7);
            this.mDevicesEditor = this.mSharedPreferences.edit();
        }
        this.mDevicesEditor.putString("devices", localJSONArray.toString());
        this.mDevicesEditor.commit();
    }

    private void scanAllDongles()
    {
        MyLog.logD("DongleManager", "trying to scan all supported dongles ...");
        UsbManager localUsbManager = (UsbManager)this.mContext.getSystemService("usb");
        if (localUsbManager == null)
            MyLog.logE("DongleManager", "get system USB_SERVICE service error!");
        while (true)
        {
            return;
            try
            {
                HashMap localHashMap = localUsbManager.getDeviceList();
                Object[] arrayOfObject = new Object[1];
                arrayOfObject[0] = Integer.valueOf(localHashMap.size());
                MyLog.logD("DongleManager", String.format("found %d dongle(s)", arrayOfObject));
                Iterator localIterator = localHashMap.values().iterator();
                while (localIterator.hasNext())
                    onUsbDevicePlugIn((UsbDevice)localIterator.next());
            }
            catch (Exception localException)
            {
            }
        }
    }

    private int ver2code(String paramString)
    {
        try
        {
            int i = Integer.parseInt(paramString.split("_")[2].substring(0, 4), 10);
            return i;
        }
        catch (Exception localException)
        {
            localException.printStackTrace();
            MyLog.logE("DongleManager", "version code:" + paramString + " is invalid");
        }
        return -1;
    }

    public void addDongleCallBack(DongleManagerEvent paramDongleManagerEvent)
    {
        MyLog.logD("DongleManager", "addDongleCallBack");
        if (this.mDongleManagerEvents == null)
            this.mDongleManagerEvents = new ArrayList();
        this.mDongleManagerEvents.add(paramDongleManagerEvent);
    }

    public boolean changeKeyScanCode(int paramInt1, DongleKeyDefines paramDongleKeyDefines, int paramInt2)
    {
        return true;
    }

    public boolean deleteIrCodeFromRC(int paramInt1, int paramInt2)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt1);
        if (localDeviceBase == null)
            return false;
        return localDeviceBase.deleteIrFromRC(paramInt2);
    }

    public int getDongleId()
    {
        ArrayList localArrayList = getDongleList();
        if ((localArrayList == null) || (localArrayList.size() == 0))
            return -1;
        return ((DeviceBase.DeviceInfo)localArrayList.get(0)).dongleID;
    }

    public DeviceBase.DeviceInfo getDongleInfo(int paramInt)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return null;
        return (DeviceBase.DeviceInfo)(DeviceBase.DeviceInfo)localDeviceBase.getDeviceInfo().clone();
    }

    public ArrayList<DeviceBase.DeviceInfo> getDongleList()
    {
        ArrayList localArrayList = new ArrayList();
        synchronized (this.mDongleObjs)
        {
            int i = this.mDongleObjs.size();
            for (int j = 0; j < i; j++)
                localArrayList.add((DeviceBase.DeviceInfo)(DeviceBase.DeviceInfo)((DeviceBase)this.mDongleObjs.valueAt(j)).getDeviceInfo().clone());
            return localArrayList;
        }
    }

    public DongleTypes getDongleType(int paramInt)
    {
        if (getYoungObject(paramInt) != null)
            return DongleTypes.dongle_iflytek_young;
        if (getDongleObject(paramInt) != null)
        {
            DeviceBase localDeviceBase = getDongleObject(paramInt);
            if ((localDeviceBase instanceof DongleIflytekBle))
                return DongleTypes.dongle_iflytek_ble;
            if ((localDeviceBase instanceof DongleIflytek24G))
                return DongleTypes.dongle_iflytek_24g;
        }
        return DongleTypes.dongle_unknown;
    }

    public String getDongleUUID(int paramInt)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return null;
        return localDeviceBase.getDeviceInfo().dongleUUID;
    }

    public String getDongleVersion()
    {
        if (this.mOpts.size() == 0)
            return null;
        return readDongleMarketInfo(this.mOpts.keyAt(-1 + this.mOpts.size()));
    }

    public String getRCVersion()
    {
        String str;
        if (this.mOpts.size() == 0)
        {
            str = null;
            return str;
        }
        for (int i = -1 + this.mOpts.size(); ; i--)
        {
            if (i < 0)
                break label54;
            str = readRCMarketInfo(this.mOpts.keyAt(i));
            if (!TextUtils.isEmpty(str))
                break;
        }
        label54: return null;
    }

    public DongleIflytekYoung getYoungObject()
    {
        return this.mDongleIflytekYoung;
    }

    public void init(Context paramContext)
            throws IllegalArgumentException
    {
        MyLog.logD("DongleManager", "init()");
        if (!this.isInited)
        {
            if (paramContext == null)
                throw new IllegalArgumentException("invalid params for dongle manager");
            this.mVendorBleRemote.init(0, new VendorRemoteBle.DeviceOpenCloseEvent()
                    {
                        public void onDeviceClose()
                        {
                            MyLog.logD("DongleManager", "VendorBleRemote onClose");
                            DongleManager.this.removeDongleObject(0);
                            Xiri.getInstance(DongleManager.this.mContext).rcStatusChanged(false);
                            if (DongleManager.this.mDongleManagerEvent != null)
                                DongleManager.this.mDongleManagerEvent.onDongleClose(0);
                        }

                        public void onDeviceOpen()
                        {
                            MyLog.logD("DongleManager", "VendorBleRemote onOpen");
                            DongleManager.this.addDongleObject(0, DongleManager.this.mVendorBleRemote);
                            Xiri.getInstance(DongleManager.this.mContext).rcStatusChanged(true);
                            if (DongleManager.this.mDongleManagerEvent != null)
                                DongleManager.this.mDongleManagerEvent.onDongleOpen(0, DongleManager.this.mVendorBleRemote.getDeviceInfo());
                        }
                    }
                    , this.mDongleIspEvent);
            this.mContext = paramContext.getApplicationContext();
            this.mKeyMappingManager = new KeyMappingManager();
            this.mPowerIrCodeManager = new PowerIrCodeManager(this.mContext);
            IntentFilter localIntentFilter = new IntentFilter();
            localIntentFilter.addAction("android.hardware.usb.action.USB_DEVICE_ATTACHED");
            localIntentFilter.addAction("android.hardware.usb.action.USB_DEVICE_DETACHED");
            localIntentFilter.addAction("com.iflytek.request.USB_PERMISSION");
            localIntentFilter.addAction("android.intent.action.ACTION_SHUTDOWN");
            localIntentFilter.addAction("android.intent.action.SCREEN_OFF");
            localIntentFilter.addAction("android.intent.action.SCREEN_ON");
            this.mContext.registerReceiver(this.bc, localIntentFilter);
            scanAllDongles();
            this.isInited = true;
            Context localContext = this.mContext;
            this.mSharedPreferences = localContext.getSharedPreferences("DEVICES", 7);
            this.mDevicesEditor = this.mSharedPreferences.edit();
        }
    }

    public boolean isSupport(int paramInt, DongleIflytekYoung.FunctionType paramFunctionType)
    {
        if ((getYoungObject() != null) && (getYoungObject().isFunctionSupport(paramFunctionType)));
        while (true)
        {
            return true;
            if (paramFunctionType == DongleIflytekYoung.FunctionType.function_karaoke)
                continue;
            if (paramFunctionType != DongleIflytekYoung.FunctionType.function_translate)
                break;
            String str = readRCMarketInfo(paramInt);
            if (TextUtils.isEmpty(str))
                break;
            int i = ver2code(str);
            MyLog.logD("DongleManager", "isSupport(" + paramInt + ", " + paramFunctionType + "), vercode = " + i);
            if (i < 1013)
                return false;
        }
        return false;
    }

    public boolean isSupport(DongleIflytekYoung.FunctionType paramFunctionType)
    {
        if (paramFunctionType == DongleIflytekYoung.FunctionType.function_karaoke)
            return true;
        if (paramFunctionType == DongleIflytekYoung.FunctionType.function_translate)
        {
            if (this.mOpts.size() == 0)
                return false;
            for (int i = -1 + this.mOpts.size(); ; i--)
            {
                if (i < 0)
                    break label64;
                if (isSupport(this.mOpts.keyAt(i), paramFunctionType))
                    break;
            }
            label64: return false;
        }
        return false;
    }

    public int learnIrCode(int paramInt, IrCodeDataAssist.IrCodeDataInfo paramIrCodeDataInfo)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return -1;
        if (localDeviceBase.isWorking())
            return 7;
        return localDeviceBase.learnIrCode(paramIrCodeDataInfo);
    }

    // ERROR //
    public void notifyInstalledPlugin()
    {
        // Byte code:
        //   0: aload_0
        //   1: monitorenter
        //   2: aload_0
        //   3: getfield 168	com/iflytek/xiri/dongle/DongleManager:mContext	Landroid/content/Context;
        //   6: invokevirtual 404	android/content/Context:getPackageManager	()Landroid/content/pm/PackageManager;
        //   9: aload_0
        //   10: getfield 168	com/iflytek/xiri/dongle/DongleManager:mContext	Landroid/content/Context;
        //   13: invokevirtual 456	android/content/Context:getPackageName	()Ljava/lang/String;
        //   16: iconst_0
        //   17: invokevirtual 412	android/content/pm/PackageManager:getPackageInfo	(Ljava/lang/String;I)Landroid/content/pm/PackageInfo;
        //   20: getfield 764	android/content/pm/PackageInfo:applicationInfo	Landroid/content/pm/ApplicationInfo;
        //   23: getfield 769	android/content/pm/ApplicationInfo:uid	I
        //   26: istore_2
        //   27: new 771	android/os/Bundle
        //   30: dup
        //   31: invokespecial 772	android/os/Bundle:<init>	()V
        //   34: astore_3
        //   35: aload_3
        //   36: ldc_w 773
        //   39: iload_2
        //   40: invokevirtual 777	android/os/Bundle:putInt	(Ljava/lang/String;I)V
        //   43: aload_3
        //   44: ldc_w 779
        //   47: iconst_0
        //   48: invokevirtual 783	android/os/Bundle:putBoolean	(Ljava/lang/String;Z)V
        //   51: aload_0
        //   52: aload_3
        //   53: ldc_w 785
        //   56: invokespecial 200	com/iflytek/xiri/dongle/DongleManager:runInSystem	(Landroid/os/Bundle;Ljava/lang/String;)V
        //   59: aload_0
        //   60: invokespecial 721	com/iflytek/xiri/dongle/DongleManager:scanAllDongles	()V
        //   63: aload_0
        //   64: monitorexit
        //   65: return
        //   66: astore 4
        //   68: aload_0
        //   69: monitorexit
        //   70: aload 4
        //   72: athrow
        //   73: astore_1
        //   74: iconst_0
        //   75: istore_2
        //   76: goto -49 -> 27
        //
        // Exception table:
        //   from	to	target	type
        //   2	27	66	finally
        //   27	63	66	finally
        //   2	27	73	java/lang/Exception
    }

    public void onIrGuiderStatusChanged(boolean paramBoolean)
    {
        if (this.mPowerIrCodeManager != null)
            this.mPowerIrCodeManager.onIrGuiderStatusChanged(paramBoolean);
    }

    public void onUsbDevicePlugIn(UsbDevice paramUsbDevice)
    {
        new Thread(paramUsbDevice)
        {
            public void run()
            {
                MyLog.logD("DongleManager", "usb dongle plug in detect!");
                DongleManager.this.dumpDongleInfo(this.val$usbDevice);
                UsbManager localUsbManager = (UsbManager)DongleManager.this.mContext.getSystemService("usb");
                if ((localUsbManager == null) || (!localUsbManager.hasPermission(this.val$usbDevice)))
                {
                    MyLog.logD("DongleManager", "no permission to operate dongle");
                    DongleManager.this.mDongleManagerEvent.requestForUsbPermission(this.val$usbDevice);
                    return;
                }
                DongleTypes localDongleTypes = DongleManager.this.getDongleType(this.val$usbDevice);
                Log.d("DongleManager", "dongleType :" + localDongleTypes.name());
                if (localDongleTypes == DongleTypes.dongle_unknown)
                {
                    MyLog.logD("DongleManager", "not a supported dongle");
                    return;
                }
                int i = DongleManager.this.calcDongleHashID(this.val$usbDevice);
                if (DongleManager.this.getDongleObject(i) != null)
                {
                    MyLog.logD("DongleManager", "dongle has already been opened");
                    return;
                }
                MyLog.logD("DongleManager", "trying to open usb device, type=" + localDongleTypes);
                int j = DongleManager.6.$SwitchMap$com$iflytek$xiri$dongle$DongleTypes[localDongleTypes.ordinal()];
                int k = 0;
                switch (j)
                {
                    default:
                    case 1:
                    case 2:
                    case 3:
                }
                while (true)
                {
                    if (k != 0)
                        DongleManager.this.mDongleManagerEvent.onDongleOpen(i, DongleManager.this.getDongleObject(i).getDeviceInfo());
                    DongleManager.this.saveDevices();
                    return;
                    DongleIflytek24G localDongleIflytek24G = new DongleIflytek24G();
                    if (localDongleIflytek24G.openDongle(i, localUsbManager, this.val$usbDevice, DongleManager.this.mDongleIspEvent))
                    {
                        DongleManager.this.addDongleObject(i, localDongleIflytek24G);
                        k = 1;
                        continue;
                    }
                    MyLog.logE("DongleManager", "fail to open iflytek 2.4G dongle");
                    k = 0;
                    continue;
                    DongleIflytekBle localDongleIflytekBle = new DongleIflytekBle();
                    if (localDongleIflytekBle.openDongle(i, localUsbManager, this.val$usbDevice, DongleManager.this.mDongleIspEvent))
                    {
                        DongleManager.this.addDongleObject(i, localDongleIflytekBle);
                        k = 1;
                        continue;
                    }
                    MyLog.logE("DongleManager", "fail to open iflytek ble dongle");
                    k = 0;
                    continue;
                    DongleIflytekYoung localDongleIflytekYoung = new DongleIflytekYoung();
                    if (localDongleIflytekYoung.openDongle(i, localUsbManager, this.val$usbDevice, DongleManager.this.mDongleIspEvent))
                    {
                        DongleManager.this.mDongleIflytekYoung = localDongleIflytekYoung;
                        DongleManager.this.addYoungObject(i, localDongleIflytekYoung);
                        k = 0;
                        continue;
                    }
                    MyLog.logE("DongleManager", "fail to open iflytek ble dongle");
                    k = 0;
                }
            }
        }
                .start();
    }

    public void onUsbDevicePlugOut(UsbDevice paramUsbDevice)
    {
        MyLog.logD("DongleManager", "usb dongle plug out detect!");
        int i = calcDongleHashID(paramUsbDevice);
        DeviceBase localDeviceBase = getDongleObject(i);
        if (localDeviceBase == null)
        {
            DongleIflytekYoung localDongleIflytekYoung = getYoungObject(i);
            if (localDongleIflytekYoung != null)
            {
                localDongleIflytekYoung.closeDongle();
                removeYoungObject(i);
            }
            return;
        }
        localDeviceBase.closeDevice();
        removeDongleObject(i);
        this.mDongleManagerEvent.onDongleClose(i);
        saveDevices();
    }

    public int queryKeyIspMode(int paramInt, DongleKeyDefines paramDongleKeyDefines)
    {
        return -1;
    }

    public int queryKeyScanCode(int paramInt, DongleKeyDefines paramDongleKeyDefines)
    {
        return -1;
    }

    public String readDongleMarketInfo(int paramInt)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return null;
        return localDeviceBase.getDeviceInfo().dongleMarketInfo;
    }

    public byte[] readFromDongle(int paramInt1, int paramInt2)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt1);
        if (localDeviceBase == null)
            return null;
        return localDeviceBase.readFromDongle(paramInt2);
    }

    public int readIrCodeFromRC(int paramInt1, int paramInt2, IrCodeDataAssist.IrCodeDataInfo paramIrCodeDataInfo)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt1);
        if (localDeviceBase == null)
            return -1;
        return localDeviceBase.readIrFromRC(paramInt2, paramIrCodeDataInfo);
    }

    public int readIrCodeInfoFromRC(int paramInt1, int paramInt2, IrCodeDataAssist.IrCodeDataInfo paramIrCodeDataInfo)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt1);
        if (localDeviceBase == null)
            return -1;
        return localDeviceBase.readIrInfoFromRC(paramInt2, paramIrCodeDataInfo);
    }

    public String readRCMarketInfo(int paramInt)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return null;
        return localDeviceBase.getDeviceInfo().rcMarketInfo;
    }

    public String readRCUUID(int paramInt)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return null;
        return UUIDAssist.uuid_to_string(localDeviceBase.readRCUUID());
    }

    public boolean restoreAllKeyScanCodes(int paramInt)
    {
        return true;
    }

    public boolean restoreKeyIspMode(int paramInt, DongleKeyDefines paramDongleKeyDefines)
    {
        return true;
    }

    public boolean restoreKeyScanCode(int paramInt, DongleKeyDefines paramDongleKeyDefines)
    {
        return true;
    }

    public boolean saveIrCodeToRC(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt1);
        if (localDeviceBase == null)
            return false;
        return localDeviceBase.saveIrToRC(paramInt2, paramArrayOfByte);
    }

    public boolean sendIrCode(int paramInt, byte[] paramArrayOfByte)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null);
        do
            return false;
        while (localDeviceBase.isWorking());
        return localDeviceBase.sendIrCode(paramArrayOfByte);
    }

    public boolean setDongleUUID(int paramInt, String paramString)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null);
        do
            return false;
        while (!localDeviceBase.writeToDongle(0, UUIDAssist.string_to_uuid(paramString)));
        localDeviceBase.getDeviceInfo().dongleUUID = paramString;
        return true;
    }

    public boolean setKeyIspMode(int paramInt, DongleKeyDefines paramDongleKeyDefines)
    {
        return true;
    }

    public boolean simulateKeyEvent(int paramInt1, int paramInt2, int paramInt3)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt1);
        if (localDeviceBase == null)
            return false;
        return localDeviceBase.simulateKeyEvent(paramInt2, paramInt3);
    }

    public boolean startIspVoiceRecord(int paramInt, DeviceBase.VoiceDataEvent paramVoiceDataEvent)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return false;
        return localDeviceBase.startIspVoiceRecord(paramVoiceDataEvent);
    }

    public void stopIspVoiceRecord(int paramInt)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null)
            return;
        localDeviceBase.stopIspVoiceRecord();
    }

    public boolean writeRCUUID(int paramInt, String paramString)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt);
        if (localDeviceBase == null);
        do
            return false;
        while (!localDeviceBase.saveUUIDToRC(UUIDAssist.string_to_uuid(paramString)));
        localDeviceBase.getDeviceInfo().rcUUID = paramString;
        return true;
    }

    public boolean writeToDongle(int paramInt1, int paramInt2, byte[] paramArrayOfByte)
    {
        if (paramArrayOfByte == null)
            return false;
        return writeToDongle(paramInt1, paramInt2, paramArrayOfByte, paramArrayOfByte.length);
    }

    public boolean writeToDongle(int paramInt1, int paramInt2, byte[] paramArrayOfByte, int paramInt3)
    {
        DeviceBase localDeviceBase = getDongleObject(paramInt1);
        if (localDeviceBase == null)
            return false;
        if ((paramArrayOfByte == null) || (paramInt3 > localDeviceBase.getDongleSectionSize()))
        {
            MyLog.logE("DongleManager", "write to dongle error, invalid parameters!");
            return false;
        }
        if (paramInt2 == 0)
        {
            MyLog.logE("DongleManager", "write to dongle error, section 0 is for uuid only!");
            return false;
        }
        byte[] arrayOfByte = new byte[paramInt3];
        System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, paramInt3);
        return localDeviceBase.writeToDongle(paramInt2, arrayOfByte);
    }

    public class CtlOpted
    {
        public boolean isOpted;
        public boolean isReportRcUuid;

        public CtlOpted(boolean paramBoolean1, boolean arg3)
        {
            this.isReportRcUuid = paramBoolean1;
            boolean bool;
            this.isOpted = bool;
        }
    }

    public static abstract interface DongleManagerEvent
    {
        public abstract void onDongleClose(int paramInt);

        public abstract void onDongleKeyDown(int paramInt, DongleKeyDefines paramDongleKeyDefines);

        public abstract void onDongleKeyUp(int paramInt, DongleKeyDefines paramDongleKeyDefines);

        public abstract void onDongleOpen(int paramInt, DeviceBase.DeviceInfo paramDeviceInfo);

        public abstract void onRemoteStatusChange(int paramInt1, int paramInt2);

        public abstract void requestForUsbPermission(UsbDevice paramUsbDevice);
    }

    private class GetUUIDRunable
            implements Runnable
    {
        private int count;
        private int dongID;
        private Boolean isRunning = Boolean.valueOf(false);
        private Runnable self;
        private HttpRequest uuidGetRequest;

        public GetUUIDRunable(int arg2)
        {
            MyLog.logD("DongleManager", "new GetUUIDRunnable");
            int i;
            this.dongID = i;
            this.self = this;
            this.count = 0;
        }

        public void run()
        {
            MyLog.logD("DongleManager", "mGetUUIDRunnable run");
            if (this.isRunning.booleanValue())
                return;
            this.isRunning = Boolean.valueOf(true);
            this.count = (1 + this.count);
            if (this.uuidGetRequest == null)
                this.uuidGetRequest = new HttpRequest();
            this.uuidGetRequest.open("GET", "http://itv2-id.openspeech.cn/v3/registerapp/", new HttpRequest.ServerListener()
            {
                public void onBitmapOK(Bitmap paramBitmap)
                {
                }

                public void onError()
                {
                    MyLog.logE("DongleManager", "getUUID onError");
                    DongleManager.GetUUIDRunable.access$1002(DongleManager.GetUUIDRunable.this, Boolean.valueOf(false));
                    if (DongleManager.GetUUIDRunable.this.count < 3)
                    {
                        DongleManager.this.mHandler.removeCallbacks(DongleManager.GetUUIDRunable.this.self);
                        DongleManager.this.mHandler.postDelayed(DongleManager.GetUUIDRunable.this.self, 1000L);
                        return;
                    }
                    DeviceBase.DeviceInfo localDeviceInfo = DongleManager.this.getDongleInfo(DongleManager.GetUUIDRunable.this.dongID);
                    Collector.getInstance().reportCtlSetUuid(3, "", String.valueOf(localDeviceInfo.productID), String.valueOf(localDeviceInfo.vendorID), String.valueOf(localDeviceInfo.dongleID), localDeviceInfo.dongleMarketInfo, localDeviceInfo.dongleUUID, localDeviceInfo.rcMarketInfo, String.valueOf(localDeviceInfo.dongleType));
                }

                public void onOK(String paramString)
                {
                    DongleManager.GetUUIDRunable.access$1002(DongleManager.GetUUIDRunable.this, Boolean.valueOf(false));
                    MyLog.logD("DongleManager", "getUUID onOK json=" + paramString);
                    try
                    {
                        String str = new JSONObject(paramString).getString("uuid");
                        DeviceBase.DeviceInfo localDeviceInfo;
                        if ((str != null) && (!"".equals(str)) && (DongleManager.this.getDongleInfo(DongleManager.GetUUIDRunable.this.dongID) != null))
                        {
                            localDeviceInfo = DongleManager.this.getDongleInfo(DongleManager.GetUUIDRunable.this.dongID);
                            if (!DongleManager.this.writeRCUUID(DongleManager.GetUUIDRunable.this.dongID, str))
                                break label251;
                            Collector.getInstance().reportCtlInsert(localDeviceInfo.rcUUID, String.valueOf(localDeviceInfo.productID), String.valueOf(localDeviceInfo.vendorID), String.valueOf(localDeviceInfo.dongleID), localDeviceInfo.dongleMarketInfo, localDeviceInfo.dongleUUID, localDeviceInfo.rcMarketInfo, String.valueOf(localDeviceInfo.dongleType));
                            Collector.getInstance().reportCtlSetUuid(1, str, String.valueOf(localDeviceInfo.productID), String.valueOf(localDeviceInfo.vendorID), String.valueOf(localDeviceInfo.dongleID), localDeviceInfo.dongleMarketInfo, localDeviceInfo.dongleUUID, localDeviceInfo.rcMarketInfo, String.valueOf(localDeviceInfo.dongleType));
                        }
                        while (true)
                        {
                            label241: DongleManager.GetUUIDRunable.access$1202(DongleManager.GetUUIDRunable.this, null);
                            return;
                            label251: Collector.getInstance().reportCtlSetUuid(3, str, String.valueOf(localDeviceInfo.productID), String.valueOf(localDeviceInfo.vendorID), String.valueOf(localDeviceInfo.dongleID), localDeviceInfo.dongleMarketInfo, localDeviceInfo.dongleUUID, localDeviceInfo.rcMarketInfo, String.valueOf(localDeviceInfo.dongleType));
                        }
                    }
                    catch (Exception localException)
                    {
                        break label241;
                    }
                }
            });
            this.uuidGetRequest.send(null);
        }
    }
}