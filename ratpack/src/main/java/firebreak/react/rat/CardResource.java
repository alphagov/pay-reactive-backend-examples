package firebreak.react.rat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import org.apache.commons.lang3.tuple.Pair;
import ratpack.exec.Operation;
import ratpack.exec.Promise;
import ratpack.func.Block;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.TypedData;
import ratpack.http.client.HttpClient;
import ratpack.http.client.ReceivedResponse;
import ratpack.rx.RxRatpack;
import react.backend.common.model.Card;
import react.backend.common.model.Charge;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static ratpack.rx.RxRatpack.observe;

public class CardResource {

    private final ObjectMapper mapper;

    @Inject
    public CardResource(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public Handler authorise() {
        return ctx ->
                ctx.byMethod(method ->
                        method.post(handleAuthorisation(ctx)));
    }

    public Handler authoriseObserve() {
        return ctx -> ctx.byMethod(method ->
                method.post(handleAuthorisationObserve(ctx)));
    }

    private Block handleAuthorisation(Context ctx) {
        return () -> {
            String chargeId = ctx.getPathTokens().get("chargeId");
            ctx.getRequest().getBody()
                    .map(bodyData -> validateCardDetails(bodyData))
                    .onError(error -> dispatchResponse(ctx, 400, error.getMessage()))
                    .map(card -> getCharge(chargeId, card))
                    .map(chargeCardPair -> submitToGateway(chargeCardPair))
                    .map(gatewayResponsePromise -> interpretGatewayResponse(gatewayResponsePromise))
                    .then(resultPromise ->
                            resultPromise.then(message ->
                                    dispatchResponse(ctx, 200, message))
                    );
        };
    }

    private Block handleAuthorisationObserve(Context ctx) {
        return () -> {
            String chargeId = ctx.getPathTokens().get("chargeId");

            Promise<Promise<String>> map = ctx.getRequest().getBody()
                    .map(bodyData -> validateCardDetails(bodyData))
                    .onError(error -> dispatchResponse(ctx, 400, error.getMessage()))
                    .map(card -> getCharge(chargeId, card))
                    .map(chargeCardPair -> submitToGateway(chargeCardPair))
                    .map(responsePromise -> interpretGatewayResponse(responsePromise));


            Observable<Promise<String>> gatewayObserver = observe(map);

//            gatewayResponsePromise -> {
//                System.out.println(">>> timeing out here ");
//                dispatchResponse(ctx, 202, "accepted");
//

            Promise<List<Promise<String>>> promise = RxRatpack.promise(gatewayObserver
                    .doOnNext(gatewayResponsePromise -> {
                        gatewayResponsePromise
                                .map(responseString -> {
                                    System.out.println(" I'm here 1");
                                    return responseString;
                                });
                    })
                    .timeout(1, TimeUnit.SECONDS)
//                    .onErrorReturn(throwable -> {
//                        System.out.println(" I'm here 2");
//                        return Promise.value("timedout");
//                    })
                    .lift(new OperatorSuppressError<>(throwable -> {
                        System.out.println("I'm here 4");
//                        dispatchResponse(ctx,202,"timed out");

                    })));
            promise.then(promises -> {
                System.out.println("result i got from observerable = " + promises.size());
                dispatchResponse(ctx,202,"blah");
            });
//                    .doAfterTerminate(() -> ctx.getResponse().send())

//                    .onErrorResumeNext(error -> {
//                        System.out.println(">>>>>>>> timeout triggered");
//                        dispatchResponse(ctx,200,"timedout");
//                    })
            ;
        };
    }

    private Observable<? extends Promise<ReceivedResponse>> someFunction() {
        return null;
    }

    private void dispatchResponse(Context ctx, int status, String message) {
        ctx.getResponse().status(status).send(writeJsonMessage(message));
    }

    private Promise<String> interpretGatewayResponse(Promise<ReceivedResponse> gatewayResponsePromise) {
        return gatewayResponsePromise.map(gatewayResponse -> {
            if (gatewayResponse.getStatus().getCode() == 200) {
                return "success";
            } else {
                return "error";
            }
        });
    }

    private Promise<ReceivedResponse> submitToGateway(Pair<Charge, Card> cardAndCharge) {
        try {
            String cardString = mapper.writeValueAsString(cardAndCharge.getValue());
            Thread.sleep(2000);
            return anHttpClient().post(URI.create("http://example.com"), requestSpec -> requestSpec.body(body -> body.text(cardString)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
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

    private Card validateCardDetails(TypedData bodyData) {
        try {
            Card card = mapper.readValue(bodyData.getText(), Card.class);
            if (isBlank(card.getCardHolder()) ||
                    isBlank(card.getCardNumber()) ||
                    isBlank(card.getCvc()) ||
                    isBlank(card.getAddress())) {
                throw new RuntimeException("invalid card details");
            }
            return card;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public Pair<Charge, Card> getCharge(String chargeId, Card card) {
        return Pair.of(new Charge(chargeId), card);
    }
}
