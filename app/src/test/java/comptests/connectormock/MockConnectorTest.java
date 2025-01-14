package comptests.connectormock;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import connector.Connection;
import connector.Connector;
import connector.ConnectorObserver;
import connector.mockconnector.MockChannel;
import connector.mockconnector.MockConnector;


public class MockConnectorTest {
    Connector connector1;
    Connector connector2;

    @BeforeEach
    public void before() {
        MockConnector.mockConnector1 = null;
        MockConnector.mockConnector2 = null;

        connector1 = new MockConnector();
        connector2 = new MockConnector();

        connected = false;
    }

    boolean connected = false;

    @Test
    public void testConnection() {
        connector1.open(1);

        assertNotNull(connector1.getChannel());

        connector1.setObserver(new ConnectorObserver() {
            @Override
            public void onConnection(Connection newConnection) {
                connected = true;
            }

            @Override
            public void onDisconnection(Connection lostConnection) {
                connected = false;
            }
        });

        connector2.connect("", 1);

        assert (connected);
    }

    @Test
    public void testSending() {
        connector1.open(1);

        assertNotNull(connector1.getChannel());

        connector1.setObserver(new ConnectorObserver() {
            @Override
            public void onConnection(Connection newConnection) {
                connected = true;
            }

            @Override
            public void onDisconnection(Connection lostConnection) {
                connected = false;
            }
        });

        connector2.connect("", 1);

        assert (connected);
        assert (connector1.getChannel().isOpen());
        assert (connector2.getChannel().isOpen());

        String massage = "hi I am host";
        connector1.getChannel().send(massage);

        String read = ((String) connector2.getChannel().read());

        assert (massage.equals(read));
    }

    @Test
    public void testSendingOnConnection() {
        connector1.open(1);

        assertNotNull(connector1.getChannel());

        String massage = "hi I am host";

        connector1.setObserver(new ConnectorObserver() {
            @Override
            public void onConnection(Connection newConnection) {
                connected = true;
                newConnection.send(massage);
            }

            @Override
            public void onDisconnection(Connection lostConnection) {
                connected = false;
            }
        });

        connector2.connect("", 1);

        assert (connected);
        assert (((MockChannel) connector1.getChannel()).other == connector2.getChannel());
        assert (((MockChannel) connector2.getChannel()).other == connector1.getChannel());
        assert (connector1.getChannel().isOpen());
        assert (connector2.getChannel().isOpen());

        String read = ((String) connector2.getChannel().read());

        assert (massage.equals(read));
    }


    @Test
    public void testSendingInts() {
        connector1.open(1);

        assertNotNull(connector1.getChannel());

        connector1.setObserver(new ConnectorObserver() {
            @Override
            public void onConnection(Connection newConnection) {
                connected = true;
            }

            @Override
            public void onDisconnection(Connection lostConnection) {
                connected = false;
            }
        });

        connector2.connect("", 1);

        assert (connected);
        assert (((MockChannel) connector1.getChannel()).other == connector2.getChannel());
        assert (((MockChannel) connector2.getChannel()).other == connector1.getChannel());
        assert (connector1.getChannel().isOpen());
        assert (connector2.getChannel().isOpen());

        int[] vals = new int[]{
                1, 2, 634, 82, 346, 76543, 245, 6782
        };
        for (int val : vals) {
            connector2.getChannel().send(val);
        }

        for (int i = 0; i < vals.length; i++) {
            int read = (int) connector1.getChannel().read();

            assertEquals(vals[i], read);
        }
    }


}
