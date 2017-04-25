package sxwang.me.ohmyyeelight;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import sxwang.me.ohmyyeelight.entity.Device;
import sxwang.me.ohmyyeelight.interaction.Command;
import sxwang.me.ohmyyeelight.interaction.Commander;

/**
 * Created by Shaoxing on 24/04/2017.
 */

public class DeviceController {
    public static final DeviceController DEFAULT_INSTANCE = new DeviceController();

    // UDP
    private static final String SEARCH_HOST = "239.255.255.250";
    private static final int SEARCH_PORT = 1982;
    private static final String ADVERTISEMENT_HOST = "239.255.255.250";
    private static final int ADVERTISEMENT_PORT = 1982;
    private static final String sSearchMessage = "M-SEARCH * HTTP/1.1\r\n" +
            "HOST: 239.255.255.250:1982\r\n" +
            "MAN: \"ssdp:discover\"\r\n" +
            "ST: wifi_bulb\r\n";
    private static int sLocalPort = 9090;
    private static int sTimeout = 3600;

    private volatile boolean mContinueSearching = true;
    private Set<Device> mDeviceSet = new LinkedHashSet<>();
    private List<OnDeviceSetChangeListener> mOnDeviceSetChangeListeners = new LinkedList<>();

    public void searchDevice() {
        mDeviceSet.clear();

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(sLocalPort);
            socket.setSoTimeout(sTimeout);

            byte[] bytes = sSearchMessage.getBytes();
            DatagramPacket dgSend = new DatagramPacket(bytes, bytes.length, InetAddress.getByName(SEARCH_HOST), SEARCH_PORT);
            byte[] recvBuffer = new byte[1024];
            DatagramPacket dgRecv = new DatagramPacket(recvBuffer, recvBuffer.length);

            try {
                socket.send(dgSend);
                while (mContinueSearching) {
                    socket.receive(dgRecv);
                    final String response = new String(dgRecv.getData(), dgRecv.getOffset(), dgRecv.getLength());
                    dgRecv.setLength(recvBuffer.length);

                    Log.d("UDP", "==== received from " + dgRecv.getSocketAddress() + "  ==== " + new Date());
                    Log.d("UDP", response);
                    Log.d("UDP", "==== end ====");

                    Device device = Device.parse(response);
                    mDeviceSet.add(device);
                    for (OnDeviceSetChangeListener listener : mOnDeviceSetChangeListeners) {
                        listener.OnNewDevice(this, device);
                    }
                    Command command = Commander.newBuilder()
                            .withHost(device.getHost())
                            .withPort(device.getPort())
                            .build()
                            .create(Command.class);
                    command.getProp("power", "bright", "ct");
                }
            } catch (SocketTimeoutException e) {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

    public void listenToAdvertisement() {
        try {
            InetAddress group = InetAddress.getByName(ADVERTISEMENT_HOST);

            MulticastSocket socket = new MulticastSocket(ADVERTISEMENT_PORT);
            socket.setSoTimeout(3600 * 1000);
            socket.joinGroup(group);

            byte[] recvBuffer = new byte[1024];
            DatagramPacket dgRecv = new DatagramPacket(recvBuffer, recvBuffer.length);

            socket.receive(dgRecv);

            final String response = new String(dgRecv.getData(), dgRecv.getOffset(), dgRecv.getLength());
            Log.d("UDP > NOTIFY", response);

            socket.leaveGroup(group);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public boolean isContinueSearching() {
        return mContinueSearching;
    }

    public void setContinueSearching(boolean continueSearching) {
        mContinueSearching = continueSearching;
    }

    public Set<Device> getDeviceSet() {
        return mDeviceSet;
    }

    public void addOnDeviceSetChangeListener(@NonNull OnDeviceSetChangeListener listener) {
        mOnDeviceSetChangeListeners.add(listener);
    }

    /**
     * @param listener if null, all the listeners will be removed
     */
    public void removeOnDeviceSetChangeListener(OnDeviceSetChangeListener listener) {
        if (listener == null) {
            mOnDeviceSetChangeListeners.clear();
        } else {
            mOnDeviceSetChangeListeners.remove(listener);
        }
    }


    public interface OnDeviceSetChangeListener {
        void OnNewDevice(DeviceController controller, Device device);
    }
}
