package connector;

import connector.mockconnector.MockConnector;

public class ConnectorFactory {
    public static boolean insertMock = false;

    public static Connector createConnector() {
        if (insertMock)
            return new MockConnector();

        return new StarConnector2();
    }
}
