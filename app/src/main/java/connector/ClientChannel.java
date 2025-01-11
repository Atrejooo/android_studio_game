package connector;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientChannel implements Channel {
    private static final String debugName = "ClientChannel";

    //client side------------------------------------------------------
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private Socket channelSocket;

    ClientChannel(Socket channelSocket) throws NullPointerException, IOException {
        if (channelSocket == null || !channelSocket.isConnected() || channelSocket.isClosed())
            throw new NullPointerException("socket was null on ClientChannel creation");

        this.channelSocket = channelSocket;

        oos = new ObjectOutputStream(channelSocket.getOutputStream());


        ois = new ObjectInputStream(channelSocket.getInputStream());
    }


    //sending and receiving both sides------------------------------------------------------
    @Override
    public void send(Object object) {
        if (!isOpen()) {
            Log.d(debugName, " channel is closed, cannot send");
            return;
        }

        if (object == null) {
            Log.e(debugName, "object send attempt in client channel, object was null");
            return;
        }

        try {
            oos.writeObject(object);
            oos.reset();
        } catch (IOException e) {
            oos = null;
            Log.e(debugName, e.getMessage() + " on object send attempt", e);
        }
    }

    @Override
    public Object read() {
        if (!isOpen()) {
            Log.d(debugName, " clientChannel is closed, cannot read");
            return null;
        }

        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            Log.e(debugName, e.getMessage() + " on object read attempt", e);
            ois = null;
        } catch (IOException e) {
            ois = null;
            Log.e(debugName, e.getMessage() + " on object read attempt", e);
        }

        return null;
    }

    @Override
    public boolean isOpen() {
        if (oos == null || ois == null) {
            return false;
        }

        if (channelSocket != null) {
            return !channelSocket.isClosed();
        }

        return false;
    }
}
