package com.example.arpinfodemo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {
    private String   WIP ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void discover(String ip) {
        String newip = "";
        if (!ip.equals("")) {
            String ipseg = ip.substring(0, ip.lastIndexOf(".")+1);
            for (int i=2; i<255; i++) {
                newip = ipseg+String.valueOf(i);
                if (newip.equals(ip)) continue;
                Thread ut = new UDPThread(newip);
                ut.start();
            }
        }
    }

    private void getNetworkInfo(Context context) {
        try {
            WifiManager wm = null;
            if (context != null) {
                try {
                    wm = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
                } catch (Exception e) {
                    wm = null;
                    Log.e("NCCFINDER", "error: "+e.getMessage());
                }
            }
            if (wm != null && wm.isWifiEnabled()) {
                WifiInfo wifi = wm.getConnectionInfo();
                if (wifi.getRssi() != -200) {
                  String   WIP = getWifiIPAddress(wifi.getIpAddress());
                }
            }
        } catch (Exception e) {
            e.getMessage();
        }
    }
    private String getWifiIPAddress(int ipaddr) {
        String ip = "";
        if (ipaddr == 0) return ip;
        byte[] addressBytes = {(byte)(0xff & ipaddr), (byte)(0xff & (ipaddr >> 8)),
                (byte)(0xff & (ipaddr >> 16)), (byte)(0xff & (ipaddr >> 24))};
        try {
            ip = InetAddress.getByAddress(addressBytes).toString();
            if (ip.length() > 1) {
                ip = ip.substring(1, ip.length());
            } else {
                ip = "";
            }
        } catch (UnknownHostException e) {
            ip = "";
        } catch (Exception e) {
            ip = "";
        }
        return ip;
    }

}
