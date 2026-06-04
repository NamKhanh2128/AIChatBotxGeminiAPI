package com.training.studyfx.server;

import java.net.*;
import java.util.Enumeration;

/**
 * UDP-based LAN auto-discovery.
 * - discoverServer(): broadcasts "STUDYFX_DISCOVER" and waits for a server reply.
 * - startBeacon():   listens for discovery requests and responds with this machine's IP.
 */
public class ServerDiscovery {

    private static final int    DISCOVERY_PORT  = 1236;
    private static final String DISCOVER_MSG    = "STUDYFX_DISCOVER";
    private static final String RESPONSE_PREFIX = "STUDYFX_SERVER:";

    private volatile boolean beaconRunning = false;
    private Thread beaconThread;

    // ─────────────────────────────────────────────────────────────
    // CLIENT SIDE: find an existing server
    // ─────────────────────────────────────────────────────────────

    /**
     * Broadcasts a discovery packet on the LAN.
     * @param timeoutMs how long to wait for a reply (milliseconds).
     * @return "host:port" string if a server answered, null otherwise.
     */
    public static String discoverServer(int timeoutMs) {
        try (DatagramSocket socket = new DatagramSocket()) {
            socket.setBroadcast(true);
            socket.setSoTimeout(timeoutMs);

            byte[] sendData = DISCOVER_MSG.getBytes("UTF-8");

            // Send to 255.255.255.255 (global broadcast)
            sendBroadcast(socket, sendData, InetAddress.getByName("255.255.255.255"));

            // Also send to each interface's subnet broadcast address
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    if (iface.isLoopback() || !iface.isUp()) continue;
                    for (InterfaceAddress addr : iface.getInterfaceAddresses()) {
                        InetAddress broadcast = addr.getBroadcast();
                        if (broadcast != null) {
                            sendBroadcast(socket, sendData, broadcast);
                        }
                    }
                }
            }

            // Wait for a server reply
            byte[] recvData = new byte[256];
            DatagramPacket recvPacket = new DatagramPacket(recvData, recvData.length);
            socket.receive(recvPacket);   // blocks until reply or timeout

            String response = new String(recvPacket.getData(), 0, recvPacket.getLength(), "UTF-8").trim();
            if (response.startsWith(RESPONSE_PREFIX)) {
                String target = response.substring(RESPONSE_PREFIX.length()); // "ip:port"
                System.out.println("Discovery: found server at " + target);
                return target;
            }
        } catch (SocketTimeoutException e) {
            System.out.println("Discovery: no server found on LAN (timeout).");
        } catch (Exception e) {
            System.err.println("Discovery error: " + e.getMessage());
        }
        return null;
    }

    private static void sendBroadcast(DatagramSocket socket, byte[] data, InetAddress dest) {
        try {
            DatagramPacket pkt = new DatagramPacket(data, data.length, dest, DISCOVERY_PORT);
            socket.send(pkt);
        } catch (Exception ignored) {}
    }

    // ─────────────────────────────────────────────────────────────
    // SERVER SIDE: respond to discovery requests
    // ─────────────────────────────────────────────────────────────

    /**
     * Starts a background UDP listener that responds to discovery broadcasts.
     * @param tcpPort the TCP port on which the chat server is listening.
     */
    public void startBeacon(int tcpPort) {
        beaconRunning = true;
        beaconThread = new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(DISCOVERY_PORT)) {
                socket.setBroadcast(true);
                System.out.println("Discovery beacon started on UDP port " + DISCOVERY_PORT);
                byte[] buf = new byte[256];
                while (beaconRunning) {
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength(), "UTF-8").trim();
                    if (DISCOVER_MSG.equals(msg)) {
                        String localIp  = getLocalIp();
                        String response = RESPONSE_PREFIX + localIp + ":" + tcpPort;
                        byte[] responseData = response.getBytes("UTF-8");
                        DatagramPacket reply = new DatagramPacket(
                                responseData, responseData.length,
                                packet.getAddress(), packet.getPort());
                        socket.send(reply);
                        System.out.println("Discovery: replied to " + packet.getAddress()
                                + " with " + response);
                    }
                }
            } catch (Exception e) {
                if (beaconRunning) System.err.println("Beacon error: " + e.getMessage());
            }
        }, "discovery-beacon");
        beaconThread.setDaemon(true);
        beaconThread.start();
    }

    public void stopBeacon() {
        beaconRunning = false;
        if (beaconThread != null) beaconThread.interrupt();
    }

    // ─────────────────────────────────────────────────────────────
    // Utility
    // ─────────────────────────────────────────────────────────────

    /** Returns the first non-loopback IPv4 address of this machine. */
    public static String getLocalIp() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            if (interfaces != null) {
                while (interfaces.hasMoreElements()) {
                    NetworkInterface iface = interfaces.nextElement();
                    if (iface.isLoopback() || !iface.isUp()) continue;
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress addr = addresses.nextElement();
                        if (addr instanceof Inet4Address) {
                            return addr.getHostAddress();
                        }
                    }
                }
            }
        } catch (Exception ignored) {}
        return "localhost";
    }
}
