package firebreak.react.drop.resources;


import firebreak.react.drop.model.Card;
import firebreak.react.drop.service.AuthorisationService;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("/")
public class CardResource {

    private final AuthorisationService authService;

    public CardResource(AuthorisationService authService) {
        this.authService = authService;
    }

    @GET
    public void rootResource(@Suspended final AsyncResponse asyncResponse) {
        new Thread(() -> asyncResponse.resume(sayHello()))
                .start();
    }

    @POST
    @Path("authorise/{chargeId}")
    public void authorise(@PathParam("chargeId") String chargeId, Card card, @Suspended final AsyncResponse asyncResponse) {
        asyncResponse.setTimeoutHandler(asyncResponse1 -> asyncResponse1.resume(new OperationAlreadyInProgressRuntimeException(chargeId)));
        asyncResponse.setTimeout(1, TimeUnit.SECONDS);

        new Thread(() -> {
            if (validateCardDetails(card)) {
                String response = authService.authorise(card);
                System.out.println(">>> yey - authorisation happened");
                asyncResponse.resume(Response.ok(response).build());
            } else {
                asyncResponse.resume(Response.status(BAD_REQUEST).entity("{\"message\":\"invalid card details\"}").build());
            }
        }).start();
    }

    private boolean validateCardDetails(Card card) {
        if (isBlank(card.getCardHolder()) ||
                isBlank(card.getCardNumber()) ||
                isBlank(card.getCvc()) ||
                isBlank(card.getAddress())) {
            return false;
        }
        return true;
    }

    private String sayHello() {
        return "{\"message\":\"hello async dropwizard\"}";
    }

}
