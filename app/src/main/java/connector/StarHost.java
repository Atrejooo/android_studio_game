package connector;

import android.util.Log;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import singeltons.Randoms;

class StarHost {
    private static final String debugName = "StarHost";

    private final int id = randomId(); //identifier
    private int port = 7777; //hub port


    StarHost(int port, String networkInterface) {
        this.port = port;
        try {
            openServerSocket(port, networkInterface);
        } catch (IOException e) {
            Log.e(debugName, e.getMessage());
        }
    }

    int getId() {
        return id;
    }

    ServerSocket serverSocket;
    HostChannel hostChannel;

    private void openServerSocket(int port, String networkInterface) throws IOException {
        if (networkInterface == null)
            serverSocket = new ServerSocket(port);
        else
            serverSocket = new ServerSocket(port, 50, InetAddress.getByName(networkInterface));

        try {
            hostChannel = new HostChannel(serverSocket, this);
        } catch (NullPointerException e) {
            Log.e(debugName, e.getMessage());
        }

        new AcceptorThread(serverSocket);

        Log.d(debugName, "id: " + id + " opened host socket on port: " + port + " - starts accepting connections");
    }

    List<StarConnection> openStarConnections = new ArrayList<>();

    private void newConnection(Socket socket) {
        Log.d(debugName, "new connection attempt in host");

        if (socket == null) {
            Log.e(debugName, "socket was null on new Connection in host");
            return;
        }

        StarConnection starConnection = new StarConnection(socket, hostChannel);
        openStarConnections.add(starConnection);

        starConnection.setId(randomId());

        Log.d(debugName, "Connection initialized, given Id: " + starConnection.clientId());

        if (connectorObserver != null) {
            connectorObserver.onConnection(starConnection);
        } else {
            Log.e(debugName, "hosts connectorObserver is null");
        }
    }

    void broadCast(Object object) {
        filterConnections();

        for (Connection connection : openStarConnections) {
            connection.send(object);
        }
    }

    private void filterConnections() {
        for (int i = 0; i < openStarConnections.size(); i++) {
            StarConnection starConnection = openStarConnections.get(i);
            if (starConnection != null) {
                if (!starConnection.isOpen()) {
                    openStarConnections.remove(starConnection);

                    starConnection.stop();

                    if (connectorObserver != null) {
                        connectorObserver.onDisconnection(starConnection);
                    } else {
                        Log.e(debugName, "hosts connectorObserver is null");
                    }
                    Log.d(debugName, "host disposed Connection with: " + starConnection.clientId() + " connection socket was closed");
                    i--;
                }
            } else {
                openStarConnections.remove(i);
                i--;
            }
        }
    }

    void stop() {
        try {
            if (serverSocket != null)
                if (!serverSocket.isClosed())
                    serverSocket.close();
        } catch (IOException e) {
            Log.e(debugName, e.getMessage() + "- on stop attempt", e);
        }

        for (StarConnection starConnection : openStarConnections) {
            if (starConnection != null) {
                starConnection.stop();
            }
        }
        openStarConnections.clear();

        hostChannel = null;
    }

    HostChannel getChannel() {
        return hostChannel;
    }

    ConnectorObserver connectorObserver;

    void setObserver(ConnectorObserver connectorObserver) {
        this.connectorObserver = connectorObserver;
    }

    private class AcceptorThread {
        private static final String debugAdd = ".AcceptorThread";

        ServerSocket accepterThreadServerSocket;

        AcceptorThread(ServerSocket accepterThreadServerSocket) throws NullPointerException {
            if (accepterThreadServerSocket == null)
                throw new NullPointerException("serversocket was null on AccepterThread creation");
            this.accepterThreadServerSocket = accepterThreadServerSocket;
            new Thread(() -> {
                while (!accepterThreadServerSocket.isClosed()) {
                    try {
                        Socket socket = accepterThreadServerSocket.accept();

                        newConnection(socket);
                    } catch (IOException e) {
                        Log.e(debugName + debugAdd, e.getMessage() + " - on accept attempt", e);
                    }
                }

                Log.d(debugName + debugAdd, "server socket is closed - stopped accepting");
            }).start();
        }
    }

    private int randomId() {
        return Randoms.randomPosInt() + 1;
    }
}
