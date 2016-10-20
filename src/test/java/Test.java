import com.just.server.http.monitor.CalculateEnvTask;
import org.hyperic.sigar.*;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by yt on 2016/10/16.
 */
public class Test {

    @org.junit.Test
    public void test() throws SigarException {
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();
        System.out.println(mem.getTotal()/1024L/1024);
        String address = null;
        try {
            address = InetAddress.getLocalHost().getHostAddress();
            // 没有出现异常而正常当取到的IP时，如果取到的不是网卡循回地址时就返回
            // 否则再通过Sigar工具包中的方法来获取
            print(address);
            if (!NetFlags.LOOPBACK_ADDRESS.equals(address)) {
            }
        } catch (UnknownHostException e) {
            // hostname not in DNS or /etc/hosts
        }
        try {
            address = sigar.getNetInterfaceConfig().getAddress();
        } catch (SigarException e) {
            address = NetFlags.LOOPBACK_ADDRESS;
        } finally {
        }
        print(address);

// 取到当前机器的MAC地址
        String[] ifaces = sigar.getNetInterfaceList();
        String hwaddr = null;
        for (int i = 0; i < ifaces.length; i++) {
            NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
            if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress())
                    || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                    || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                continue;
            }
            hwaddr = cfg.getHwaddr();
            print(hwaddr);
            // break;
        }
        print(hwaddr != null ? hwaddr : null);

// 获取网络流量等信息
        String ifNames[] = sigar.getNetInterfaceList();
        for (int i = 0; i < ifNames.length; i++) {
            String name = ifNames[i];
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            print("\nname = " + name);// 网络设备名
            print("Address = " + ifconfig.getAddress());// IP地址
            print("Netmask = " + ifconfig.getNetmask());// 子网掩码
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                print("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            try {
                NetInterfaceStat ifstat = sigar.getNetInterfaceStat(name);
                print("RxPackets = " + ifstat.getRxPackets());// 接收的总包裹数
                print("TxPackets = " + ifstat.getTxPackets());// 发送的总包裹数
                print("RxBytes = " + ifstat.getRxBytes());// 接收到的总字节数
                print("TxBytes = " + ifstat.getTxBytes());// 发送的总字节数
                print("RxErrors = " + ifstat.getRxErrors());// 接收到的错误包数
                print("TxErrors = " + ifstat.getTxErrors());// 发送数据包时的错误数
                print("RxDropped = " + ifstat.getRxDropped());// 接收时丢弃的包数
                print("TxDropped = " + ifstat.getTxDropped());// 发送时丢弃的包数
            } catch (SigarNotImplementedException e) {
            } catch (SigarException e) {
                print(e.getMessage());
            }
        }

// 一些其他的信息
        for (int i = 0; i < ifaces.length; i++) {
            NetInterfaceConfig cfg = sigar.getNetInterfaceConfig(ifaces[i]);
            if (NetFlags.LOOPBACK_ADDRESS.equals(cfg.getAddress())
                    || (cfg.getFlags() & NetFlags.IFF_LOOPBACK) != 0
                    || NetFlags.NULL_HWADDR.equals(cfg.getHwaddr())) {
                continue;
            }
            print("cfg.getAddress() = " + cfg.getAddress());// IP地址
            print("cfg.getBroadcast() = " + cfg.getBroadcast());// 网关广播地址
            print("cfg.getHwaddr() = " + cfg.getHwaddr());// 网卡MAC地址
            print("cfg.getNetmask() = " + cfg.getNetmask());// 子网掩码
            System.out
                    .println("cfg.getDescription() = " + cfg.getDescription());// 网卡描述信息
            print("cfg.getType() = " + cfg.getType());//
            System.out
                    .println("cfg.getDestination() = " + cfg.getDestination());
            print("cfg.getFlags() = " + cfg.getFlags());//
            print("cfg.getMetric() = " + cfg.getMetric());
            print("cfg.getMtu() = " + cfg.getMtu());
            print("cfg.getName() = " + cfg.getName());
        }

    }

    void print(String str){
        System.out.println(str);
    }

    @org.junit.Test
    public void dataTest() throws InterruptedException, SigarException {
        Sigar sigar=new Sigar();
        String ifNames[] = sigar.getNetInterfaceList();
        for (int i = 0; i < ifNames.length; i++) {
            String name = ifNames[i];
            if(!name.equals("eth16"))continue;
            NetInterfaceConfig ifconfig = sigar.getNetInterfaceConfig(name);
            print("\nname = " + name);// 网络设备名
            print("Address = " + ifconfig.getAddress());// IP地址
            print("Netmask = " + ifconfig.getNetmask());// 子网掩码
            if ((ifconfig.getFlags() & 1L) <= 0L) {
                print("!IFF_UP...skipping getNetInterfaceStat");
                continue;
            }
            try {
                System.out.println(name);
                NetInterfaceStat statStart = sigar.getNetInterfaceStat(name);
                long start = System.currentTimeMillis();
                long rxBytesStart = statStart.getRxBytes();
                long txBytesStart = statStart.getTxBytes();
                Thread.sleep(1000);
                long end = System.currentTimeMillis();
                NetInterfaceStat statEnd = sigar.getNetInterfaceStat(name);
                long rxBytesEnd = statEnd.getRxBytes();
                long txBytesEnd = statEnd.getTxBytes();

                double rxbps = (rxBytesEnd - rxBytesStart) /1024.0 / (end - start) * 1000;
                double txbps = (txBytesEnd - txBytesStart) /1024.0/ (end - start) * 1000;
                print(rxbps + " " + txbps);
            } catch (SigarNotImplementedException e) {

            } catch (SigarException e) {
                print(e.getMessage());
            }
        }
    }

    @org.junit.Test
    public void runTest() throws InterruptedException {
        ScheduledExecutorService executorService=Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(new CalculateEnvTask(),0,1, TimeUnit.SECONDS);
        System.out.println(CalculateEnvTask.cache.poll());
        while (true){
            Thread.sleep(1000);
            System.out.println(CalculateEnvTask.cache.poll());
        }

    }
}
