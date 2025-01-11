package connector;

public interface ConnectorObserver {
    void onConnection(Connection newConnection);
    void onDisconnection(Connection lostConnection);
}
