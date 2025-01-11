package connector;

import android.util.Log;

import java.net.ServerSocket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

class HostChannel implements Channel {
    private static final String debugName = "HostChannel";

    //host side------------------------------------------------------

    ServerSocket channelServerSocket;

    private BlockingQueue<Object> hostInputQueue = new LinkedBlockingQueue<>();

    StarHost starHost;

    HostChannel(ServerSocket channelServerSocket, StarHost starHost) throws NullPointerException {
        if (channelServerSocket == null)
            throw new NullPointerException("serverSocket was null on Channel creation");
        if (starHost == null)
            throw new NullPointerException("starHost was null on Channel creation");


        this.channelServerSocket = channelServerSocket;

        this.starHost = starHost;
    }

    void addToHostInputQueue(Object object) {
        if (object != null) {
            hostInputQueue.add(object);
            //Log.d(debugName, " added object: " + object + " to the host input queue");
        } else
            Log.d(debugName, "attempted to add a null object to hostInputQueue");
    }


    @Override
    public void send(Object object) {
        if (!isOpen()) {
            Log.d(debugName, " is closed, cannot send");
            return;
        }

        if (object == null) {
            Log.e(debugName, " object was null, cannot send");
            return;
        }

        if (channelServerSocket != null) {
            starHost.broadCast(object);
        }
    }

    @Override
    public Object read() {
        if (!isOpen()) {
            Log.d(debugName, "starChannel is closed, cannot read");
            return null;
        }

        try {
            return hostInputQueue.take();
        } catch (InterruptedException e) {
            Log.e(debugName, e.getMessage() + " on host read attempt", e);
        }
        return null;
    }

    @Override
    public boolean isOpen() {
        if (channelServerSocket != null) {
            return !channelServerSocket.isClosed();
        }
        return false;
    }
}