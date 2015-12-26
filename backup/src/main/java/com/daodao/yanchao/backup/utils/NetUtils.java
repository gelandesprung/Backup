package com.daodao.yanchao.backup.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yanchao on 12/15/15.
 */
public class NetUtils {
    /**
     * 检查网络是否可用
     *
     * @param paramContext
     * @return
     */
    public static boolean checkEnable(Context paramContext) {
        boolean i = false;
        NetworkInfo localNetworkInfo = ((ConnectivityManager) paramContext
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if ((localNetworkInfo != null) && (localNetworkInfo.isAvailable()))
            return true;
        return false;
    }

    /**
     * 将ip的整数形式转换成ip形式
     *
     * @param ipInt
     * @return
     */
    public static String int2ip(int ipInt) {
        StringBuilder sb = new StringBuilder();
        sb.append(ipInt & 0xFF).append(".");
        sb.append((ipInt >> 8) & 0xFF).append(".");
        sb.append((ipInt >> 16) & 0xFF).append(".");
        sb.append((ipInt >> 24) & 0xFF);
        return sb.toString();
    }

    /**
     * 获取当前ip地址
     *
     * @param context
     * @return
     */
    public static String getLocalIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int i = wifiInfo.getIpAddress();
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
        return int2ip(i);
    }

    public static List<String> getIPBound(Context context){
        List<String> bounds = new ArrayList<>();
        WifiManager wifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();

        int netmask = dhcpInfo.netmask;
        int count = getPoolMax(netmask);
        for (int i=1;i<=count;i++){
            bounds.add(int2ip(netmask+i));
        }
        return bounds;
    }
    public static byte[] intToBytes(int ipInt) {
        byte[] ipAddr = new byte[4];
        ipAddr[0] = (byte) ((ipInt >>> 24) & 0xFF);
        ipAddr[1] = (byte) ((ipInt >>> 16) & 0xFF);
        ipAddr[2] = (byte) ((ipInt >>> 8) & 0xFF);
        ipAddr[3] = (byte) (ipInt & 0xFF);
        return ipAddr;
    }
    /**
     * 计算子网大小
     */
    public static int getPoolMax(int netmask)
    {
        if(netmask<=0||netmask>=32)
        {
            return 0;
        }
        int bits=32-netmask;
        return (int) Math.pow(2,bits) -2;
    }
    /**
     * 根据掩码位计算掩码
     */
    public static String getMask(int masks)
    {
        if(masks == 1)
            return "128.0.0.0";
        else if(masks == 2)
            return "192.0.0.0";
        else if(masks == 3)
            return "224.0.0.0";
        else if(masks == 4)
            return "240.0.0.0";
        else if(masks == 5)
            return "248.0.0.0";
        else if(masks == 6)
            return "252.0.0.0";
        else if(masks == 7)
            return "254.0.0.0";
        else if(masks == 8)
            return "255.0.0.0";
        else if(masks ==9)
            return "255.128.0.0";
        else if(masks == 10)
            return "255.192.0.0";
        else if(masks == 11)
            return "255.224.0.0";
        else if(masks == 12)
            return "255.240.0.0";
        else if(masks == 13)
            return "255.248.0.0";
        else if(masks == 14)
            return "255.252.0.0";
        else if(masks == 15)
            return "255.254.0.0";
        else if(masks == 16)
            return "255.255.0.0";
        else if(masks == 17)
            return "255.255.128.0";
        else if(masks == 18)
            return "255.255.192.0";
        else if(masks == 19)
            return "255.255.224.0";
        else if(masks == 20)
            return "255.255.240.0";
        else if(masks == 21)
            return "255.255.248.0";
        else if(masks == 22)
            return "255.255.252.0";
        else if(masks == 23)
            return "255.255.254.0";
        else if(masks == 24)
            return "255.255.255.0";
        else if(masks == 25)
            return "255.255.255.128";
        else if(masks == 26)
            return "255.255.255.192";
        else if(masks == 27)
            return "255.255.255.224";
        else if(masks == 28)
            return "255.255.255.240";
        else if(masks == 29)
            return "255.255.255.248";
        else if(masks == 30)
            return "255.255.255.252";
        else if(masks == 31)
            return "255.255.255.254";
        else if(masks == 32)
            return "255.255.255.255";
        return "";
    }
}
