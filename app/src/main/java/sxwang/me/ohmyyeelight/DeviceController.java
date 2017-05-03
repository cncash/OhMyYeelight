package sxwang.me.ohmyyeelight;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
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
    private static final String SEARCH_MESSAGE = "M-SEARCH * HTTP/1.1\r\n" +
            "HOST: 239.255.255.250:1982\r\n" +
            "MAN: \"ssdp:discover\"\r\n" +
            "ST: wifi_bulb\r\n";
    private static int sTimeout = 3600;

    private volatile boolean mContinueSearching = true;
    private Set<Device> mDeviceSet = new LinkedHashSet<>();
    private List<OnDeviceSetChangeListener> mOnDeviceSetChangeListeners = new LinkedList<>();

    public void searchDevice() {
        mDeviceSet.clear();

        DatagramChannel channel = null;
        try {
            final String TAG = "UDP CHANNEL";
            channel = DatagramChannel.open();
            channel.configureBlocking(false);
            Selector selector = Selector.open();
            channel.register(selector, SelectionKey.OP_READ);

            byte[] recv = new byte[1024];
            ByteBuffer buffer = ByteBuffer.wrap(recv);

            channel.send(ByteBuffer.wrap(SEARCH_MESSAGE.getBytes()), new InetSocketAddress(SEARCH_HOST, SEARCH_PORT));
            while (mContinueSearching) {
                if (selector.select(100) > 0) {
                    for (SelectionKey key : selector.selectedKeys()) {
                        if (key.isValid() && key.isReadable() && key.channel() == channel) {
                            buffer.clear();
                            SocketAddress address = channel.receive(buffer);
                            if (address != null) {
                                String s = new String(recv, 0, buffer.position());
                                Log.d(TAG, "==== received from " + address + "  ==== " + new Date());
                                Log.d(TAG, s);
                                Log.d(TAG, "==== end ====");

                                Device device = Device.parse(s);
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
                        }
                    }
                }
            }
            selector.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                } catch (IOException ignored) {
                }
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
            Log.d("NOTIFY", response);

            socket.leaveGroup(group);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public void toggle() {
        for (Device device : mDeviceSet) {
            Command command = Commander.newBuilder()
                    .withHost(device.getHost())
                    .withPort(device.getPort())
                    .build()
                    .find(Command.class);
            if (command != null) {
                command.toggle();
            }
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
