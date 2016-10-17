package firebreak.react.rat;

import ratpack.func.Block;
import ratpack.handling.Context;
import ratpack.handling.Handler;

public class CardResource {
    public Handler authorise() {
        return ctx -> {
            ctx.byMethod(method -> method.post(handleAuthorisation(ctx)));
        };
    }

    private Block handleAuthorisation(Context ctx) {
        return () -> ctx.getResponse().status(202).send("{\"message\":\"got it\"}");
    }
}
