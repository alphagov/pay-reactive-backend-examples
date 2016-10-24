package firebreak.react.drop.resources;


import com.google.inject.Inject;
import firebreak.react.drop.model.Card;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Response;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Path("/")
public class CardResource {

    private final AuthorisationService authService;
    private final KafkaAuthorisationService kafkaAuthorisationService;

    @Inject
    public CardResource(AuthorisationService authService, KafkaAuthorisationService kafkaAuthorisationService) {
        this.authService = authService;
        this.kafkaAuthorisationService = kafkaAuthorisationService;
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
            validateCardDetails()
                    .andThen(authoriseAndRespond(chargeId, card, asyncResponse))
                    .apply(card);
        }).start();
    }

    @POST
    @Path("authoriseKafka/{chargeId}")
    public void authoriseKafka(@PathParam("chargeId") String chargeId, Card card, @Suspended final AsyncResponse asyncResponse) {
        asyncResponse.setTimeoutHandler(asyncResponse1 -> asyncResponse1.resume(new OperationAlreadyInProgressRuntimeException(chargeId)));
        asyncResponse.setTimeout(1, TimeUnit.SECONDS);

        new Thread(() -> {
            validateCardDetails()
                    .andThen(authoriseKafkaAndRespond(chargeId, card, asyncResponse))
                    .apply(card);
        }).start();
    }

    private Function<Boolean, Void> authoriseKafkaAndRespond(String chargeId, Card card, AsyncResponse asyncResponse) {
        return valid -> {
            if (!valid) {
                asyncResponse.resume(Response.status(BAD_REQUEST).entity("{\"message\":\"invalid card details\"}").build());
            } else {
                kafkaAuthorisationService.doGatewayAuthorise(chargeId, card,
                        response -> asyncResponse.resume(Response.ok(response).build()));
            }
            return null;
        };
    }

    private Function<Boolean, Void> authoriseAndRespond(String chargeId, Card card, AsyncResponse asyncResponse) {
        return valid -> {
            if (!valid) {
                asyncResponse.resume(Response.status(BAD_REQUEST).entity("{\"message\":\"invalid card details\"}").build());
            } else {
                authService.doAuthorise(chargeId, card,
                        response -> asyncResponse.resume(Response.ok(response).build()));
            }
            return null;
        };
    }

    private Function<Card, Boolean> validateCardDetails() {
        return card -> {
            if (isBlank(card.getCardHolder()) ||
                    isBlank(card.getCardNumber()) ||
                    isBlank(card.getCvc()) ||
                    isBlank(card.getAddress())) {
                return false;
            }
            return true;
        };

    }

    private String sayHello() {
        return "{\"message\":\"hello async dropwizard\"}";
    }

}
