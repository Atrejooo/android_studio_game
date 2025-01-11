package connector;

public class ConnectorFactory {
    public static Connector createConnector() {
        return new StarConnector2();
    }
}
