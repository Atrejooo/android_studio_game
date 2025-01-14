package connector.mockconnector;

import connector.Channel;
import connector.Connector;
import connector.ConnectorObserver;

public class MockConnector implements Connector {
    public static MockConnector mockConnector1;
    public static MockConnector mockConnector2;

    public MockChannel mockChannel;

    public boolean thisIs1;

    public MockConnector() {
        thisIs1 = false;
        if (mockConnector1 == null) {
            mockConnector1 = this;
            thisIs1 = true;
        } else {
            mockConnector2 = this;
        }
    }

    private boolean isHost = false;

    @Override
    public void connect(String hostIp, int port) {
        isHost = false;

        if(thisIs1){
            if(mockConnector2.getChannel() == null){
                throw new IllegalStateException("other mockConnector is not open");
            }
        }
        else {
            if(mockConnector1.getChannel() == null){
                throw new IllegalStateException("other mockConnector is not open");
            }
        }

        mockChannel = new MockChannel();

        mockConnector1.mockChannel.other = mockConnector2.mockChannel;
        mockConnector2.mockChannel.other = mockConnector1.mockChannel;

        if (thisIs1) {
            if (mockConnector2.connectorObserver != null)
                mockConnector2.connectorObserver.onConnection(new MockConnection(mockConnector2));
        } else {
            if (mockConnector1.connectorObserver != null)
                mockConnector1.connectorObserver.onConnection(new MockConnection(mockConnector1));
        }

    }

    @Override
    public void open(int port) {
        isHost = true;
        mockChannel = new MockChannel();
    }

    @Override
    public void open(int port, String networkInterface) {
        open(port);
    }

    @Override
    public boolean isHost() {
        return isHost;
    }

    @Override
    public Channel getChannel() {
        return mockChannel;
    }

    @Override
    public int getMyId() {
        if (thisIs1)
            return 1;
        else
            return 2;
    }

    @Override
    public void stop() {
        if (mockConnector1 != null && mockConnector2 != null) {
            if (isHost) {
                if (connectorObserver != null)
                    connectorObserver.onDisconnection(new MockConnection(this));
            } else {

                mockConnector2.connectorObserver.onConnection(new MockConnection(mockConnector2));
            }

            mockConnector2.mockChannel.other = null;
            mockConnector1.mockChannel.other = null;
        }

    }

    public ConnectorObserver connectorObserver;

    @Override
    public void setObserver(ConnectorObserver connectorObserver) {
        this.connectorObserver = connectorObserver;
    }
}
