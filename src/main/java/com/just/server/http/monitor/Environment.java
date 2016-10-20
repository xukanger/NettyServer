package com.just.server.http.monitor;

/**
 * Created by yt on 2016/10/17.
 */
public class Environment {



    public void setNetCond(NetCond netCond){
        this.netMask=netCond.netMask;
        this.IP=netCond.ip;
        this.txbps=netCond.txbps;
        this.rxbps=netCond.rxbps;
    }

    public String getMemPercent() {
        return memPercent;
    }

    public void setMemPercent(String memPercent) {
        this.memPercent = memPercent;
    }

    public String getCPUPercent() {
        return CPUPercent;
    }

    public void setCPUPercent(String CPUPercent) {
        this.CPUPercent = CPUPercent;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getNetMask() {
        return netMask;
    }

    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }

    public double getRxbps() {
        return rxbps;
    }

    public void setRxbps(double rxbps) {
        this.rxbps = rxbps;
    }

    public double getTxbps() {
        return txbps;
    }

    public void setTxbps(double txbps) {
        this.txbps = txbps;
    }

    String IP;

    String netMask;

    double rxbps;

    double txbps;

    String memPercent;

    String CPUPercent;

    @Override
    public String toString() {
        return "{" +
                "'memPercent'='" + memPercent + '\'' +
                ", 'CPUPercent'='" + CPUPercent + '\'' +
                ", 'IP'='" + IP + '\'' +
                ", 'netMask'='" + netMask + '\'' +
                ", 'rxbps'=" + rxbps +
                ", 'txbps'=" + txbps +
                '}';
    }
}
