package connector;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.SimpleTimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import connector.Channel;
import connector.StarConnector;

class StarChannel implements Channel {
    private static final String debugName = "StarChannel";
    //host side------------------------------------------------------
    ServerSocket channelServerSocket;
    private BlockingQueue<Object> hostInputQueue = new LinkedBlockingQueue<>();

    StarConnector starConnector;

    StarChannel(ServerSocket channelServerSocket, StarConnector connector) throws NullPointerException {
        if (channelServerSocket == null)
            throw new NullPointerException("serverSocket was null on Channel creation");
        if (connector == null)
            throw new NullPointerException("connector was null on Channel creation");


        this.channelServerSocket = channelServerSocket;

        starConnector = connector;
    }

    void addToHostInputQueue(Object object) {
        if (object != null)
            hostInputQueue.add(object);
        else
            Log.d(debugName, "attempted tp add a null object to hostInputQueue");
    }

    //client side------------------------------------------------------
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    Socket channelSocket;

    StarChannel(Socket channelSocket, StarConnector connector) throws NullPointerException {
        if (channelSocket == null)
            throw new NullPointerException("socket was null on Channel creation");
        if (connector == null)
            throw new NullPointerException("connector was null on Channel creation");

        this.channelSocket = channelSocket;

        try {
            oos = new ObjectOutputStream(channelSocket.getOutputStream());
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on client channel creation: " + channelSocket);
        }

        try {
            ois = new ObjectInputStream(channelSocket.getInputStream());
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on client channel creation: " + channelSocket);
        }

        starConnector = connector;
    }


    //sending and receiving both sides------------------------------------------------------
    @Override
    public void send(Object object) {
        if (!isOpen()) {
            Log.d(debugName, "starChannel is closed, cannot send");
            return;
        }

        //host--------------------------------------------------------
        if (channelServerSocket != null) {
            sendOnHost(object);
            return;
        }

        //client------------------------------------------------------
        sendOnClient(object);
    }

    private void sendOnHost(Object object) {
        for (StarConnection starConnection : starConnector.openStarConnections) {
            starConnection.send(object);
        }
        starConnector.filterConnections();
    }


    private void sendOnClient(Object object) {
        try {
            oos.writeObject(object);
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on object send attempt");
        }
    }


    @Override
    public Object read() {
        if (!isOpen()) {
            Log.d(debugName, "starChannel is closed, cannot read");
            return null;
        }

        //host--------------------------------------------------------
        if (channelServerSocket != null)
            return readOnHost();

        //client------------------------------------------------------
        return readOnClient();
    }

    private Object readOnHost() {
        try {
            return hostInputQueue.take();
        } catch (InterruptedException e) {
            Log.e(debugName, e.getMessage() + " on host read attempt");
        }
        return null;
    }

    private Object readOnClient() {
        try {
            return ois.readObject();
        } catch (ClassNotFoundException e) {
            Log.e(debugName, e.getMessage() + " on object read attempt");
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on object read attempt");
        }

        return null;
    }

    @Override
    public boolean isOpen() {
        if (oos == null || ois == null) {
            return false;
        }

        if (channelServerSocket != null) {
            return !channelServerSocket.isClosed();
        }

        if (channelSocket != null) {
            return !channelSocket.isClosed();
        }

        return false;
    }
}