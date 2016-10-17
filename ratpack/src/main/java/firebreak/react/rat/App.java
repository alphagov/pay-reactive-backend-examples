package firebreak.react.rat;

import ratpack.func.Action;
import ratpack.guice.Guice;
import ratpack.handling.Chain;
import ratpack.server.RatpackServerSpec;

public class App {

    public static Action<RatpackServerSpec> serverSpec() {
        return server -> server
                .registry(Guice.registry(bindingsSpec -> bindingsSpec.module(ServiceModule.class)))
                .handlers(registerHandlerChain());
    }

    private static Action<Chain> registerHandlerChain() {

        return chain -> {
            CardResource cardResource = chain.getRegistry().get(CardResource.class);
            chain
                    .path("authorise/:chargeId", cardResource.authorise())
                    .get(ctx -> ctx.render("{\"message\": \"Hello from (Dean Martin)\"}"));
        };
    }
}
