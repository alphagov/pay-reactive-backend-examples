package firebreak.react.kafka;

import org.apache.curator.test.TestingServer;

import java.io.IOException;

public class ZooKeeperLocalServer {

    private final int port;
    TestingServer zkServer;

    public ZooKeeperLocalServer(int port) {
        this.port = port;
    }

    public void start() throws Exception {
        zkServer = new TestingServer(port);
    }

    public void stop() throws IOException {
        zkServer.close();
    }
}
