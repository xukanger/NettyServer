package com.just.server.http.monitor;

import org.hyperic.sigar.*;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by yt on 2016/10/17.
 */
public class CalculateEnvTask implements Runnable {

    public static ArrayBlockingQueue<Environment> cache = new ArrayBlockingQueue<Environment>(100);

    private Sigar sigar;

    private Long preTime;

    private Long preRxBytes;

    private Long preTxBytes;

    public static String netName="eth16";

    public CalculateEnvTask() {
        this.sigar = new Sigar();

    }

    @Override
    public void run() {
        Mem mem = getMem();
        CpuPerc cpu = getCpuPerc();
        NetCond netCond = getNetCond();
        if(netCond==null)return;
        Environment environment = new Environment();
        environment.setCPUPercent(String.valueOf((int) (cpu.getUser() * 100)));
        environment.setNetCond(netCond);
        environment.setMemPercent(String.valueOf((int) (mem.getUsedPercent())));
        try {
            cache.put(environment);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private Mem getMem() {
        Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException e) {
            e.printStackTrace();
        }
        return mem;
    }

    private CpuPerc getCpuPerc() {
        CpuPerc data = null;
        try {
            data = sigar.getCpuPerc();
        } catch (SigarException e) {
            e.printStackTrace();
        }
        return data;
    }


    private NetCond getNetCond() {
        NetCond data = null;
        try {
            String name=netName;
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            if(preTime==null){
                NetInterfaceStat statStart = sigar.getNetInterfaceStat(name);
                preTime=System.currentTimeMillis();
                preRxBytes=statStart.getRxBytes();
                preTxBytes=statStart.getTxBytes();
                return null;
            }
            data = new NetCond();
            data.netMask = ifconfig.getNetmask();
            data.ip = ifconfig.getAddress();
            long end = System.currentTimeMillis();
            NetInterfaceStat statEnd = sigar.getNetInterfaceStat(name);
            long rxBytesEnd = statEnd.getRxBytes();
            long txBytesEnd = statEnd.getTxBytes();
            double rxbps = (rxBytesEnd - preRxBytes) / 1024.0 / (end - preTime) * 1000;
            double txbps = (txBytesEnd - preTxBytes) / 1024.0 / (end - preTime) * 1000;
            preTime = end;
            preTxBytes=txBytesEnd;
            preRxBytes=rxBytesEnd;
            data.rxbps = rxbps;
            data.txbps = txbps;
        } catch (SigarException e) {
            e.getMessage();
        }
        return data;

    }

}
