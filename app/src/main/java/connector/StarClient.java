package connector;

import android.util.Log;

import java.io.IOException;
import java.net.Socket;

import singeltons.Randoms;

class StarClient {
    private static final String debugName = "StarClient";

    private int id = 0; //identifier

    private int port = 7777;
    private String ip = "localHost";

    private Socket socket;
    private ClientChannel clientChannel;


    int getId() {
        return id;
    }

    StarClient(String hostIp, int port) {
        this.port = port;
        this.ip = hostIp;

        try {
            socket = new Socket(hostIp, port);

            Log.d(debugName, "socket creation: " + socket.toString() + " connected status:" + socket.isConnected());
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on client socket creation", e);
        }
        if (socket == null || !socket.isConnected())
            return;

        try {
            clientChannel = new ClientChannel(socket);

            readId();
        } catch (NullPointerException e) {
            Log.e(debugName, e.getMessage(), e);
            Log.d(debugName, "received id: " + id + " connection success to host: " + port + ", " + hostIp);

        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + " on client channel creation: " + socket, e);
        }
    }

    private void readId() {
        if (!clientChannel.isOpen()) {
            Log.e(debugName, "Channel is closed on id read attempt");
            return;
        }
        try {
            int newId = (int) clientChannel.read();

            id = newId;
        } catch (Exception e) {
            Log.e(debugName, e.getMessage() + " on client read id attempt", e);
            throw new RuntimeException(e);
        }
    }

    void stop() {
        try {
            if (socket != null)
                if (!socket.isClosed())
                    socket.close();
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + "- on stop attempt", e);
        }

        clientChannel = null;
    }

    ClientChannel getChannel() {
        return clientChannel;
    }

    private int randomId() {
        return Randoms.randomPosInt() + 1;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        Log.e(debugName, "is finalized");
    }
}
