package com.example.vg_appcostos.Sistemas.Controlador;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlDB_Config {
    public String getMacAddress() throws UnknownHostException, SocketException {
        try{
            InetAddress ipAddress = InetAddress.getLocalHost();
            NetworkInterface networkInterface = NetworkInterface.getByInetAddress(ipAddress);
            byte[] macAddressBytes = networkInterface.getHardwareAddress();
            StringBuilder macAddressBuilder = new StringBuilder();
            for (int macAddressByteIndex = 0; macAddressByteIndex < macAddressBytes.length; macAddressByteIndex++){
                String macAddressHexByte = String.format("%02X",macAddressBytes[macAddressByteIndex]);
                macAddressBuilder.append(macAddressHexByte);
                if (macAddressByteIndex != macAddressBytes.length - 1){
                    macAddressBuilder.append(":");
                }
            }
            return macAddressBuilder.toString();
        }catch(Exception e){
            return "NULL";
        }
    }
    public String getNamePC(){
        String namePc="NULL";
        try{
            InetAddress localHost = InetAddress.getLocalHost();
            namePc=localHost.getHostName();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ControlDB_Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        return namePc;
    }
    public String getIpPc(){
        String ipPc="NULL";
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            ipPc=localHost.getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(ControlDB_Config.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ipPc;
    }
}
