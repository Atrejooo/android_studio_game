package connector;

import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import gameframe.utils.Vec2;

class StarConnection implements Connection {
    private static final String debugName = "StarConnection";

    Socket connectionSocket;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    private HostChannel channel;

    private int clientId;

    void setId(int clientId) {
        this.clientId = clientId;

        send(clientId);
    }

    StarConnection(Socket socket, HostChannel hostChannel) throws NullPointerException {
        if (socket == null)
            throw new NullPointerException("socket was null on Connection object creation");

        if (hostChannel == null)
            throw new NullPointerException("channel was null on Connection object creation");


        this.connectionSocket = socket;
        this.channel = hostChannel;

        try {
            oos = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on oos creation in Connection object creation", e);
        }

        try {
            ois = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on ois creation in Connection object creation", e);
        }

        //reading thread---------------------------------------------------
        new Thread(() -> {
            while (!connectionSocket.isClosed() && channel.isOpen() && connectionSocket.isConnected() && ois != null) {
                try {
                    Object read = ois.readObject();

                    //Log.d(debugName, "read Object in connection: " + read + " " + read.hashCode());
                    if (read != null && channel != null)
                        //passes object to channel
                        channel.addToHostInputQueue(read);
                } catch (EOFException e) {
                    ois = null;
                    Log.e(debugName, "connection/stream closed", e);
                    break;
                } catch (ClassNotFoundException | IOException e) {
                    ois = null;
                    Log.e(debugName, e.getMessage() + " on read attempt on connection", e);
                    throw new RuntimeException(e);
                }
            }

            Log.d(debugName, "client connection closed - stopped receiving thread");
        }).start();


        //sending thread---------------------------------------------------
        new Thread(() -> {
            while (!connectionSocket.isClosed() && channel.isOpen() && connectionSocket.isConnected() && oos != null) {
                Object nextObject;
                try {
                    nextObject = sendQue.take();
                } catch (InterruptedException e) {
                    Log.e(debugName, e.getMessage() + " on connection send attempt", e);
                    continue;
                }

                try {
                    oos.writeObject(nextObject);
                    oos.reset();
                } catch (IOException e) {
                    oos = null;
                    Log.e(debugName, e.getMessage() + " on connection send attempt", e);
                }
            }

            Log.d(debugName, "client connection closed - stopped sending");
        }).start();
    }

    private BlockingQueue<Object> sendQue = new LinkedBlockingQueue<>();

    @Override
    public boolean send(Object object) {
        try {
            sendQue.add(object);
        } catch (Exception e) {
            Log.e(debugName, e.getMessage() + " on send attempt on connection", e);
        }
        return !connectionSocket.isClosed();
    }

    @Override
    public int clientId() {
        //TODO do the id shit
        return clientId;
    }

    void stop() {
        if (!connectionSocket.isClosed()) {
            try {
                connectionSocket.close();
            } catch (IOException e) {
                Log.e(debugName, e.getMessage() + " on connection stop attempt");
            }
        }

        try {
            if (oos != null)
                oos.close();
            if (ois != null)
                ois.close();
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on connection stop attempt");
        }
    }

    public boolean isOpen() {
        return ois != null && oos != null && connectionSocket.isConnected() && !connectionSocket.isClosed();
    }
}