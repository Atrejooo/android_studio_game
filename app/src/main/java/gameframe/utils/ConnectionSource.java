package gameframe.utils;

public class ConnectionSource {
    public int port;
    public String ip;
    public boolean open;

    public static ConnectionSource fromString(String input) {
        // Ensure the input is valid
        if (input == null || input.length() != 17) {
            throw new IllegalArgumentException("Input must be exactly 17 characters long");
        }

        // Extract the IP parts (first 12 characters represent the IP)
        String ipPart = input.substring(0, 12);
        String ip = String.format("%d.%d.%d.%d",
                Integer.parseInt(ipPart.substring(0, 3)),
                Integer.parseInt(ipPart.substring(3, 6)),
                Integer.parseInt(ipPart.substring(6, 9)),
                Integer.parseInt(ipPart.substring(9, 12)));

        // Extract the port (last 5 characters represent the port)
        int port = Integer.parseInt(input.substring(12));

        // Initialize and return the ConnectionSource object
        ConnectionSource connectionSource = new ConnectionSource();
        connectionSource.ip = ip;
        connectionSource.port = port;
        connectionSource.open = false; // Default value

        return connectionSource;
    }

    public String toNumericString() {
        // Convert the IP to a numeric string by padding each segment to 3 digits
        String[] ipSegments = ip.split("\\.");
        if (ipSegments.length != 4) {
            throw new IllegalArgumentException("Invalid IP address format: " + ip);
        }
        StringBuilder numericIp = new StringBuilder();
        for (String segment : ipSegments) {
            numericIp.append(String.format("%03d ", Integer.parseInt(segment)));
        }

        // Concatenate the numeric IP and the port (padded to 5 digits)
        return numericIp + String.format("%05d", port);
    }

    @Override
    public String toString() {
        return "ConnectionSource{" +
                "port=" + port +
                ", ip='" + ip + '\'' +
                ", open=" + open +
                '}';
    }
}
