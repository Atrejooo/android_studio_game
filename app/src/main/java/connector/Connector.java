package connector;

import java.io.IOException;

/**
 * Create and use in a separate Thread!!!
 * <p>
 * Connector component is a Connection Manager that lets you open, or connect (as a client) to, a host.
 * Provides a Channel object on connection that handles all exeptions and disconnections.
 * Stop it at any moment. Calling open or connect after one of the two has already been called
 * causes a reset of the connector.
 * <p>
 * Networks contain one host and multiple clients. Each client can connect to the host.
 */
public interface Connector {
    void connect(String hostIp, int port);

    void open(int port);

    void open(int port, String networkInterface);

    boolean isHost();

    Channel getChannel();

    int getMyId();

    void stop();

    void setObserver(ConnectorObserver connectorObserver);
}
