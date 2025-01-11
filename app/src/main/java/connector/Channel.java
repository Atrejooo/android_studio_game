package connector;

public interface Channel {
    void send(Object object);

    Object read();

    boolean isOpen();
}
