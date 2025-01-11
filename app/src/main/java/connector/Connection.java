package connector;

/**
 * a Connection btween host and client. Only accessable from host
 */
public interface Connection {
    /**
     * send to a specific client
     * @param object to send
     * @return boolean is the connection still open
     */
    boolean send(Object object);

    int clientId();
}
