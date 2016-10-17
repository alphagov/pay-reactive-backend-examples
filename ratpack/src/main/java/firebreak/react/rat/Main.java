package firebreak.react.rat;


import ratpack.server.RatpackServer;

import static firebreak.react.rat.App.serverSpec;

public class Main {
    public static void main(String... args) throws Exception {
        RatpackServer.start(serverSpec());
    }

}
