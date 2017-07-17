package com.example.arpinfodemo;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private String   WIP ;
    private String myWifiName ;
    private String myIp;
    private String myMac;
    private TextView connectWifiInfo;
    private ListView arpLv;
    private ArrayList<Map<String, Object>> arpList = new ArrayList<Map<String, Object>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connectWifiInfo = (TextView) findViewById(R.id.connectWifiInfo);
        arpLv = (ListView) findViewById(R.id.arplist);

        getNetworkInfo();
        readArp();
    }

    @Override
    public void onClick(View v) {
         if(v.getId()==R.id.btnRefresh){
             getNetworkInfo();
             readArp();
         }
    }

    private void readArp() {
        try {
            BufferedReader br = new BufferedReader(
                    new FileReader("/proc/net/arp"));
            String line = "";
            String ip = "";
            String flag = "";
            String mac = "";

            while ((line = br.readLine()) != null) {
                try {
                    line = line.trim();
                    if (line.length() < 63) continue;
                    if (line.toUpperCase(Locale.US).contains("IP")) continue;
                    ip = line.substring(0, 17).trim();
                    flag = line.substring(29, 32).trim();
                    mac = line.substring(41, 63).trim();
                    if (mac.contains("00:00:00:00:00:00")) continue;
                    Log.e("scanner", "readArp: mac= "+mac+" ; ip= "+ip+" ;flag= "+flag);
                    String arp = "ip: "+ip+" | "+"mac: "+mac+" | "+"flag: "+flag;
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("arp",arp);
                    arpList.add(map);
                } catch (Exception e) {
                    continue;
                }
            }
            br.close();

            SimpleAdapter sad = new SimpleAdapter(this,arpList,R.layout.list_item,
                    new String[]{"arp"},new int[]{R.id.arpitem});
            arpLv.setAdapter(sad);


        } catch(Exception e) {
        }

    }

    private void getNetworkInfo() {
        try {
            WifiManager wm = null;
            try {
                wm = (WifiManager)this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            } catch (Exception e) {
                wm = null;
            }
            if (wm != null && wm.isWifiEnabled()) {
                WifiInfo wifi = wm.getConnectionInfo();
                if (wifi.getRssi() != -200) {
                    myIp = getWifiIPAddress(wifi.getIpAddress());
                }
                myWifiName = wifi.getSSID(); //获取被连接网络的名称
                myMac =  wifi.getBSSID(); //获取被连接网络的mac地址
                String str = "WIFI: "+myWifiName+"\n"+"WiFiIP: "+myIp+"\n"+"MAC: "+myMac;
                connectWifiInfo.setText(str);
                discover(myIp);// 发送arp请求
            }
        } catch (Exception e) {
            e.getMessage();
        }
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
