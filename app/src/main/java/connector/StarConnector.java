package connector;

import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import singeltons.Randoms;

class StarConnector implements Connector {
    private static final String debugName = "StarConnector";

    private boolean isHost = false;
    private final int id = randomId(); //identifier
    private int port = 7777; //hub port
    private String ip = "localhost";//hub ip


    private ServerSocket serverSocket;
    private Socket socket;

    private StarChannel starChannel;

    private int randomId() {
        return Randoms.randomPosInt() + 1;
    }

    @Override
    public void connect(String hostIp, int port) {
        stop();
        isHost = false;
        this.port = port;
        this.ip = hostIp;

        try {
            connectToServerSocket();
        } catch (IOException e) {

        }
    }

    private void connectToServerSocket() throws IOException {
        socket = new Socket(ip, port);

        try {
            starChannel = new StarChannel(socket, this);
        } catch (NullPointerException e) {
            Log.e(debugName, e.getMessage());
        }

        Log.d(debugName, "id: " + id + " connection success to host: " + port + ", " + ip);
    }

    @Override
    public void open(int port) {
        open(port, null);
    }


    @Override
    public void open(int port, String networkInterface) {
        stop();
        isHost = true;
        this.port = port;

        try {
            openServerSocket(networkInterface);
        } catch (IOException e) {
            Log.e(debugName, e.getMessage());
        }
    }

    @Override
    public boolean isHost() {
        return isHost;
    }

    private void openServerSocket(String networkInterface) throws IOException {
        if (networkInterface == null)
            serverSocket = new ServerSocket(port);
        else
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(networkInterface));

        try {
            starChannel = new StarChannel(serverSocket, this);
        } catch (NullPointerException e) {
            Log.e(debugName, e.getMessage());
        }

        new Thread(new AccepterThread(serverSocket)).start();

        Log.d(debugName, "id: " + id + " opened host socket on port: " + port + " - starts accepting connections");
    }

    List<StarConnection> openStarConnections = new ArrayList<>();

    private void newConnection(Socket socket) {
        if (socket == null) {
            Log.e(debugName, "socket was null on new Connection in host");
            return;
        }

        StarConnection starConnection = new StarConnection(socket, null);
        openStarConnections.add(starConnection);

        starConnection.setId(randomId());

        if (connectorObserver != null) {
            connectorObserver.onConnection(starConnection);
        } else {
            Log.e(debugName, "hosts connectorObserver is null");
        }
    }

    void filterConnections() {
        for (int i = 0; i < openStarConnections.size(); i++) {
            StarConnection starConnection = openStarConnections.get(i);
            if (starConnection != null) {
                if (starConnection.connectionSocket.isClosed()) {
                    openStarConnections.remove(starConnection);

                    if (connectorObserver != null) {
                        connectorObserver.onDisconnection(starConnection);
                    } else {
                        Log.e(debugName, "hosts connectorObserver is null");
                    }

                    i--;
                }
            }
        }
    }


    @Override
    public Channel getChannel() {
        return starChannel;
    }

    @Override
    public int getMyId() {
        return 0;
    }

    @Override
    public void stop() {
        try {
            if (serverSocket != null)
                if (!serverSocket.isClosed())
                    serverSocket.close();
            if (socket != null)
                if (!socket.isClosed())
                    socket.close();
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + "- on stop attempt");
        }

        for (StarConnection starConnection : openStarConnections) {
            if (starConnection != null) {
                starConnection.stop();
            }
        }
        openStarConnections.clear();

        starChannel = null;
    }


    private ConnectorObserver connectorObserver;

    @Override
    public void setObserver(ConnectorObserver connectorObserver) {
        this.connectorObserver = connectorObserver;
    }


    private class AccepterThread implements Runnable {
        private static final String debugAdd = ".AccepterThread";

        ServerSocket accepterThreadServerSocket;

        AccepterThread(ServerSocket accepterThreadServerSocket) throws NullPointerException {
            if (accepterThreadServerSocket == null)
                throw new NullPointerException("serversocket was null on AccepterThread creation");
            this.accepterThreadServerSocket = accepterThreadServerSocket;
        }

        @Override
        public void run() {
            while (!accepterThreadServerSocket.isClosed()) {
                try {
                    Socket socket = accepterThreadServerSocket.accept();

                    newConnection(socket);
                } catch (IOException e) {
                    Log.e(debugName + debugAdd, e.getMessage() + "- on accept attempt");
                }
            }

            Log.d(debugName + debugAdd, "server socket is closed - stopped accepting");
        }
    }
}
