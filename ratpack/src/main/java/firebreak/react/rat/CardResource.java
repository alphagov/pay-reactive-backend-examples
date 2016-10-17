package firebreak.react.rat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import ratpack.exec.Promise;
import ratpack.func.Block;
import ratpack.func.Function;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.TypedData;
import ratpack.http.client.HttpClient;
import ratpack.http.client.ReceivedResponse;
import react.backend.common.model.Card;
import react.backend.common.model.Charge;
import react.backend.common.service.AuthorisationService;

import java.net.URI;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static ratpack.rx.RxRatpack.observe;

public class CardResource {

    private final AuthorisationService authorisationService;
    private final ObjectMapper mapper;

    @Inject
    public CardResource(AuthorisationService authorisationService, ObjectMapper mapper) {
        this.authorisationService = authorisationService;
        this.mapper = mapper;
    }

    public Handler authorise() {
        return ctx ->
                ctx.byMethod(method ->
                        method.post(handleAuthorisation(ctx)));
    }

    private Block handleAuthorisation(Context ctx) {
        return () -> {
            String chargeId = ctx.getPathTokens().get("chargeId");
            ctx.getRequest().getBody()
                    .map(validateCardDetails())
                    .onError(error -> ctx.getResponse().status(400).send(writeJsonMessage(error.getMessage())))
                    .map(getCharge(chargeId))
                    .map(submitToWorldpay())
                    .map(interpretWorldpayResponse())
                    .then(responsePromise ->
                            responsePromise.then(message ->
                                    ctx.getResponse().status(200).send(writeJsonMessage(message)))
                    );

//            observe(worldpay).subscribe(worldpayResponseFuture ->
//                    worldpayResponseFuture.map(worldpayResponse -> {
//                        if (worldpayResponse.getStatus().getCode() == 200) {
//                            return "success";
//                        } else {
//                            return "error";
//                        }
//                    }).then(response -> {
//                        ctx.getResponse().status(200).send(writeJsonMessage(response));
//                    }));
        };
    }

    private Function<Promise<ReceivedResponse>, Promise<String>> interpretWorldpayResponse() {
        return worldpayResponsePromise ->
                worldpayResponsePromise.map(worldpayResponse -> {
                    if (worldpayResponse.getStatus().getCode() == 200) {
                        return "success";
                    } else {
                        return "error";
                    }
                });
    }

    private Function<Pair<Charge, Card>, Promise<ReceivedResponse>> submitToWorldpay() {
        return cardAndCharge -> {
            String cardString = mapper.writeValueAsString(cardAndCharge.getValue());
            return anHttpClient().post(URI.create("http://example.com"), requestSpec -> requestSpec.body(body -> body.text(cardString)));
        };
    }

    private HttpClient anHttpClient() throws Exception {
        return HttpClient.of(httpClientSpec -> httpClientSpec.poolSize(0));
    }

    private String writeJsonMessage(String message) {
        try {
            return mapper.writeValueAsString(ImmutableMap.of("message", message));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ratpack.func.Function<TypedData, Card> validateCardDetails() {
        return data -> {
            Card card = mapper.readValue(data.getText(), Card.class);
            if (isBlank(card.getCardHolder()) ||
                    isBlank(card.getCardNumber()) ||
                    isBlank(card.getCvc()) ||
                    isBlank(card.getAddress())) {
                throw new RuntimeException("invalid card details");
            }
            return card;
        };

    }

    public Function<Card, Pair<Charge, Card>> getCharge(String chargeId) {
        return card -> Pair.of(new Charge(chargeId), card);
    }
}
