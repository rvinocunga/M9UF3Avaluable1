
package m9uf3avaluablestresenratlla;

import java.net.InetAddress;

public class GameSession {
    
    private InetAddress ipAddress;
    private int port;

    public GameSession(InetAddress ipAddress, int port) {
        this.ipAddress = ipAddress;
        this.port = port;
    }

    public InetAddress getIpAddress() {
        return ipAddress;
    }

    public int getPort() {
        return port;
    }
}