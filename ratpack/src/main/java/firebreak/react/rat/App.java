package firebreak.react.rat;

import ratpack.func.Action;
import ratpack.guice.Guice;
import ratpack.handling.Chain;
import ratpack.server.RatpackServerSpec;

import java.util.Map;

public class App {

    public static Action<RatpackServerSpec> serverSpec(Map<String, String> configOverrides) {
        return server -> server
                .serverConfig(config -> config
                        .props(configOverrides)
                        .require("/app", AppConfig.class))
                .registry(Guice.registry(bindingsSpec ->
                        bindingsSpec.module(new ServiceModule(bindingsSpec.getServerConfig()))))
                .handlers(registerHandlerChain());
    }

    private static Action<Chain> registerHandlerChain() {

        return chain -> {
            CardResource cardResource = chain.getRegistry().get(CardResource.class);
            chain
                    .path("authorise/:chargeId", cardResource.authorise())
                    .path("authoriseObserve/:chargeId", cardResource.authoriseObserve())
                    .path("authoriseWebSocket/:chargeId", cardResource.authoriseWebSocket())
                    .get(ctx -> ctx.render("{\"message\": \"Hello from (Dean Martin)\"}"));
        };
    }
}
