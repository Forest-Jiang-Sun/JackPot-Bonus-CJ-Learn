package com.aspectgaming.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aspectgaming.common.event.EventMachine;
import com.aspectgaming.common.event.machine.NetworkChangedEvent;

/**
 * @author ligang.yao
 */
public final class Network extends Thread {

    private final long CHECK_TIME = 5; // seconds

    private final Logger log = LoggerFactory.getLogger(Network.class);

    private InetAddress address;
    private String ip;
    private NetworkInterface nic;
    private boolean isConnected;

    private static final Network instance = new Network();

    public static Network getInstance() {
        return instance;
    }

    private Network() {
        checkNetwork();
        start();
    }

    @Override
    public void run() {
        setName("Network");
        setPriority(Thread.MIN_PRIORITY);

        while (!Thread.interrupted()) {
            try {
                TimeUnit.SECONDS.sleep(CHECK_TIME);
            } catch (InterruptedException e) {
                break;
            }
            checkNetwork();
        }
    }

    private void checkNetwork() {
        try {
            InetAddress ia = InetAddress.getLocalHost();
            if (!ia.equals(address)) {
                onChanged(ia);
            }
        } catch (SocketException | UnknownHostException e) {
            address = null;
            ip = null;
            isConnected = false;
            nic = null;

            log.error("Failed to get network status: {}", e);
        }
    }

    private void onChanged(InetAddress addr) throws SocketException {
        log.info("Network address: {}", addr);

        address = addr;
        ip = address.getHostAddress();
        isConnected = !address.isLoopbackAddress();
        nic = NetworkInterface.getByInetAddress(addr);

        EventMachine.getInstance().offerEvent(NetworkChangedEvent.class);
    }

    public InetAddress getAddress() {
        return address;
    }

    public String getIP() {
        return ip;
    }

    public NetworkInterface getNIC() {
        return nic;
    }

    public boolean isConnected() {
        return isConnected;
    }
}
