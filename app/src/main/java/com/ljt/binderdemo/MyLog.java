package com.ljt.binderdemo;

import android.util.Log;

/**
 * Created by 1 on 2017/9/5.
 */

public class MyLog {
    private static final String HEX_NUMS = "0123456789ABCDEF";
    private static final String TAG = "Dongle";

    private static String getLogString(String paramString1, String paramString2)
    {
        StringBuilder localStringBuilder1 = new StringBuilder().append("[").append(Thread.currentThread().getId()).append("] ");
        if (paramString1 == null);
        for (String str = ""; ; str = paramString1 + ": ")
        {
            StringBuilder localStringBuilder2 = localStringBuilder1.append(str);
            if (paramString2 == null)
                paramString2 = "";
            return paramString2;
        }
    }

    public static void hex_dump(String paramString, byte[] paramArrayOfByte)
    {
        if (paramArrayOfByte == null)
        {
            Log.e("Dongle", "hex_dump null byte array!");
            return;
        }
        hex_dump(paramString, paramArrayOfByte, 0, paramArrayOfByte.length);
    }

    public static void hex_dump(String paramString, byte[] paramArrayOfByte, int paramInt1, int paramInt2)
    {
        if (paramArrayOfByte == null)
        {
            Log.e("Dongle", "hex_dump null byte array!");
            return;
        }
        StringBuilder localStringBuilder = new StringBuilder();
        Object[] arrayOfObject = new Object[1];
        arrayOfObject[0] = Integer.valueOf(paramInt2);
        localStringBuilder.append(String.format("[%d bytes]:", arrayOfObject));
        for (int i = 0; i < paramInt2; i++)
        {
            localStringBuilder.append("0123456789ABCDEF".charAt(0xF & paramArrayOfByte[(paramInt1 + i)] >> 4));
            localStringBuilder.append("0123456789ABCDEF".charAt(0xF & paramArrayOfByte[(paramInt1 + i)]));
            localStringBuilder.append(" ");
        }
        Log.v("Dongle", getLogString(paramString, localStringBuilder.toString()));
    }

    public static void say_d(String paramString1, String paramString2)
    {
        Log.d("Dongle", getLogString(paramString1, paramString2));
    }

    public static void say_e(String paramString1, String paramString2)
    {
        Log.e("Dongle", getLogString(paramString1, paramString2));
    }

    public static void say_i(String paramString1, String paramString2)
    {
        Log.i("Dongle", getLogString(paramString1, paramString2));
    }

    public static void say_v(String paramString1, String paramString2)
    {
        Log.v("Dongle", getLogString(paramString1, paramString2));
    }

    public static void say_w(String paramString1, String paramString2)
    {
        Log.w("Dongle", getLogString(paramString1, paramString2));
    }
}