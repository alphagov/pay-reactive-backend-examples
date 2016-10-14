package firebreak.react.drop.resources;


import firebreak.react.drop.model.Card;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;

@Path("/")
public class CardResource {

    @GET
    public void rootResource(@Suspended final AsyncResponse asyncResponse) {
        new Thread(() -> asyncResponse.resume(sayHello()))
                .start();
    }


    public void authorise(Card card, @Suspended final AsyncResponse asyncResponse) {

    }

    private String sayHello() {
        return "{\"message\":\"hello async dropwizard\"}";
    }

}
