package connector;

import android.util.Log;

import java.io.IOException;

class StarConnector2 implements Connector {
    private static final String debugName = "StarConnector";

    private boolean isHost;

    private StarHost host;
    private StarClient client;

    @Override
    public void connect(String hostIp, int port)  {
        stop();
        isHost = false;

        client = new StarClient(hostIp, port);
    }

    @Override
    public void open(int port) {
        open(port, null);
    }

    @Override
    public boolean isHost(){
        return isHost;
    }


    @Override
    public void open(int port, String networkInterface) {
        stop();
        isHost = true;

        host = new StarHost(port, networkInterface);
    }


    @Override
    public Channel getChannel() {
        if (host != null)
            return host.getChannel();
        else if (client != null)
            return client.getChannel();

        return null;
    }

    @Override
    public int getMyId() {
        if (host != null)
            return host.getId();
        else if (client != null)
            return client.getId();
        return -1;
    }


    @Override
    public void stop() {
        if (host != null)
            host.stop();
        else if (client != null)
            client.stop();

        host = null;
        client = null;
    }

    @Override
    public void setObserver(ConnectorObserver connectorObserver) {
        if (host != null) {
            host.setObserver(connectorObserver);
        }
    }
}
