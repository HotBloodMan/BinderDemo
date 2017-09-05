package com.ljt.binderdemo;

/**
 * Created by 1 on 2017/9/5.
 */

public  enum DongleKeyDefines
{
    static
    {
        key_power = new DongleKeyDefines("key_power", 1);
        key_showcome = new DongleKeyDefines("key_showcome", 2);
        key_xiri = new DongleKeyDefines("key_xiri", 3);
        key_setting = new DongleKeyDefines("key_setting", 4);
        key_home = new DongleKeyDefines("key_home", 5);
        key_mouse = new DongleKeyDefines("key_mouse", 6);
        key_dpad_up = new DongleKeyDefines("key_dpad_up", 7);
        key_dpad_down = new DongleKeyDefines("key_dpad_down", 8);
        key_dpad_left = new DongleKeyDefines("key_dpad_left", 9);
        key_dpad_right = new DongleKeyDefines("key_dpad_right", 10);
        key_dpad_center = new DongleKeyDefines("key_dpad_center", 11);
        key_back = new DongleKeyDefines("key_back", 12);
        key_menu = new DongleKeyDefines("key_menu", 13);
        key_volume_up = new DongleKeyDefines("key_volume_up", 14);
        key_volume_down = new DongleKeyDefines("key_volume_down", 15);
        key_mute = new DongleKeyDefines("key_mute", 16);
        key_page_up = new DongleKeyDefines("key_page_up", 17);
        key_page_down = new DongleKeyDefines("key_page_down", 18);
        key_tv_power = new DongleKeyDefines("key_tv_power", 19);
        key_tv_av = new DongleKeyDefines("key_tv_av", 20);
        key_tv_volumn_down = new DongleKeyDefines("key_tv_volumn_down", 21);
        key_tv_volumn_up = new DongleKeyDefines("key_tv_volumn_up", 22);
        key_tv_s1 = new DongleKeyDefines("key_tv_s1", 23);
        key_translate = new DongleKeyDefines("key_translate", 24);
        DongleKeyDefines[] arrayOfDongleKeyDefines = new DongleKeyDefines[25];
        arrayOfDongleKeyDefines[0] = key_unknown;
        arrayOfDongleKeyDefines[1] = key_power;
        arrayOfDongleKeyDefines[2] = key_showcome;
        arrayOfDongleKeyDefines[3] = key_xiri;
        arrayOfDongleKeyDefines[4] = key_setting;
        arrayOfDongleKeyDefines[5] = key_home;
        arrayOfDongleKeyDefines[6] = key_mouse;
        arrayOfDongleKeyDefines[7] = key_dpad_up;
        arrayOfDongleKeyDefines[8] = key_dpad_down;
        arrayOfDongleKeyDefines[9] = key_dpad_left;
        arrayOfDongleKeyDefines[10] = key_dpad_right;
        arrayOfDongleKeyDefines[11] = key_dpad_center;
        arrayOfDongleKeyDefines[12] = key_back;
        arrayOfDongleKeyDefines[13] = key_menu;
        arrayOfDongleKeyDefines[14] = key_volume_up;
        arrayOfDongleKeyDefines[15] = key_volume_down;
        arrayOfDongleKeyDefines[16] = key_mute;
        arrayOfDongleKeyDefines[17] = key_page_up;
        arrayOfDongleKeyDefines[18] = key_page_down;
        arrayOfDongleKeyDefines[19] = key_tv_power;
        arrayOfDongleKeyDefines[20] = key_tv_av;
        arrayOfDongleKeyDefines[21] = key_tv_volumn_down;
        arrayOfDongleKeyDefines[22] = key_tv_volumn_up;
        arrayOfDongleKeyDefines[23] = key_tv_s1;
        arrayOfDongleKeyDefines[24] = key_translate;
        $VALUES = arrayOfDongleKeyDefines;
    }
}