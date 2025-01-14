package connector.mockconnector;

import connector.Connection;

public class MockConnection implements Connection {
    public MockConnector myConnector;

    public MockConnection(MockConnector myConnector){
        this.myConnector = myConnector;
    }

    @Override
    public boolean send(Object object) {
        myConnector.getChannel().send(object);
        return true;
    }

    @Override
    public int clientId() {
        return myConnector.getMyId();
    }
}
