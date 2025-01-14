package connector.mockconnector;

import java.io.Serializable;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import connector.Channel;

public class MockChannel implements Channel {
    public MockChannel other;

    @Override
    public void send(Object object) {
        if(!(object instanceof Serializable))
            throw new IllegalArgumentException("tried sendign none serializables");

        if (other != null)
            other.give(object);
    }

    @Override
    public Object read() {
        try {
            return objects.take();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isOpen() {
        return other != null;
    }

    private BlockingQueue<Object> objects = new LinkedBlockingQueue<>();

    void give(Object object) {
        objects.add(object);
    }
}
