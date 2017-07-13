package com.example.arpinfodemo;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDPThread extends Thread {
	private String target_ip = "";
	
	public static final byte[] NBREQ = { (byte) 0x82, (byte) 0x28, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x1,
		(byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x0, (byte) 0x20, (byte) 0x43, (byte) 0x4B,
		(byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
		(byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
		(byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x41,
		(byte) 0x41, (byte) 0x41, (byte) 0x41, (byte) 0x0, (byte) 0x0, (byte) 0x21, (byte) 0x0, (byte) 0x1 };
	
	public static final short NBUDPP = 137;

	public UDPThread(String target_ip) {
		this.target_ip = target_ip;
	}

	@Override
	public synchronized void run() {
		if (target_ip == null || target_ip.equals("")) return;
		DatagramSocket socket = null;
		InetAddress address = null;
		DatagramPacket packet = null; //单播
		try {
			address = InetAddress.getByName(target_ip);
			packet = new DatagramPacket(NBREQ, NBREQ.length, address, NBUDPP);
			socket = new DatagramSocket();
			socket.setSoTimeout(200);
			socket.send(packet);
			socket.close();
		} catch (SocketException se) {
		} catch (UnknownHostException e) {
		} catch (IOException e) {
		} finally {
			if (socket != null) {
				socket.close();
			}
		}
	}
}
