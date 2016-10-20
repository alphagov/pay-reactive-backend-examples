package firebreak.react.rat;


import com.google.common.collect.ImmutableMap;
import ratpack.server.RatpackServer;

import static firebreak.react.rat.App.serverSpec;

public class Main {
    public static void main(String... args) throws Exception {
        ImmutableMap<String, String> configOverrides = ImmutableMap.of("app.authDelay", "0");
        RatpackServer.start(serverSpec(configOverrides));
    }

}
