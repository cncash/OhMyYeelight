package sxwang.me.ohmyyeelight.interaction;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Shaoxing on 25/04/2017.
 * <p>
 * An extremely simplified "Retrofit" on TCP, credits to the developers of Retrofit
 */

public class Commander {
    private static final String TAG = "Commander";

    private static Map<String, Object> sCommandCache = new ConcurrentHashMap<>();
    private String mHost;
    private int mPort;
    private MessageEncoder mMessageEncoder;
    private MessageDecoder mMessageDecoder;

    private Commander(Builder builder) {
        mHost = builder.mHost;
        mPort = builder.mPort;
        mMessageEncoder = builder.mMessageEncoder;
        mMessageDecoder = builder.mMessageDecoder;
    }

    public <T> T create(Class<T> clazz) {
        if (!clazz.isInterface()) {
            throw new IllegalArgumentException("clazz must be an interface");
        }
        Object proxy = Proxy.newProxyInstance(Commander.class.getClassLoader(), new Class[]{clazz}, new InvocationHandler() {
            private Socket mSocket;

            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (mMessageEncoder == null) {
                    mMessageEncoder = new DefaultMessageEncoder();
                }
                String request = mMessageEncoder.encodeMessage(method, args);
                Log.i(TAG, "request: " + request);

                if (mSocket == null || mSocket.isClosed()) {
                    mSocket = new Socket(mHost, mPort);
                }

                mSocket.getOutputStream().write(request.getBytes());
                mSocket.getOutputStream().flush();

                BufferedReader reader = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                String s = reader.readLine();
                Log.i(TAG, "response: " + s);
                mSocket.close();
                mSocket = null;

                if (mMessageDecoder == null) {
                    mMessageDecoder = new DefaultMessageDecoder();
                }
                return mMessageDecoder.decodeMessage(s);
            }
        });
        return (T) proxy;
    }

    public <T> T find(Class<T> clazz) {
        String key = mHost + ":" + mPort;
        Object command = sCommandCache.get(key);
        if (command == null || command.getClass() != clazz) {
            command = create(clazz);
            sCommandCache.put(key, command);
        }
        return (T) command;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(Commander copy) {
        Builder builder = new Builder();
        builder.mHost = copy.mHost;
        builder.mPort = copy.mPort;
        builder.mMessageEncoder = copy.mMessageEncoder;
        builder.mMessageDecoder = copy.mMessageDecoder;
        return builder;
    }

    public static final class Builder {
        private String mHost;
        private int mPort;
        private MessageEncoder mMessageEncoder;
        private MessageDecoder mMessageDecoder;

        private Builder() {
        }

        public Builder withHost(String val) {
            mHost = val;
            return this;
        }

        public Builder withPort(int val) {
            mPort = val;
            return this;
        }

        public Builder withMessageEncoder(MessageEncoder val) {
            mMessageEncoder = val;
            return this;
        }

        public Builder withMessageDecoder(MessageDecoder val) {
            mMessageDecoder = val;
            return this;
        }

        public Commander build() {
            return new Commander(this);
        }
    }
}
