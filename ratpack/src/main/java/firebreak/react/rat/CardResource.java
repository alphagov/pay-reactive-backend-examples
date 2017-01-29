package firebreak.react.rat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import firebreak.react.rat.model.Card;
import firebreak.react.rat.model.Charge;
import org.apache.commons.lang3.tuple.Pair;
import ratpack.exec.Promise;
import ratpack.func.Block;
import ratpack.func.Function;
import ratpack.handling.Context;
import ratpack.handling.Handler;
import ratpack.http.TypedData;
import ratpack.http.client.HttpClient;
import ratpack.http.client.ReceivedResponse;
import ratpack.rx.RxRatpack;
import ratpack.server.ServerConfig;
import ratpack.websocket.*;
import ratpack.websocket.internal.DefaultWebSocket;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static ratpack.rx.RxRatpack.observe;

public class CardResource {

    private final ObjectMapper mapper;
    private final Integer authDelay;

    @Inject
    public CardResource(ServerConfig serverConfig, ObjectMapper mapper) {
        this.mapper = mapper;
        AppConfig appConfig = serverConfig.get("/app", AppConfig.class);
        this.authDelay = appConfig.authDelayAsInt();
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

            Observable<Promise<Pair<Integer, String>>> authWorkflow = observe(ctx.getRequest().getBody()
                    .map(bodyData -> validateCardDetails(bodyData))
                    .onError(error -> dispatchResponse(ctx, 400, error.getMessage()))
                    .map(card -> getCharge(chargeId, card))
                    .map(chargeCardPair -> submitToGateway(chargeCardPair)))
                    .map(responsePromise -> interpretGatewayResponse(responsePromise)
                            .map(resultPromise -> Pair.of(200, "success")));

            Observable<Promise<Pair<Integer, String>>> timer = Observable.timer(500, TimeUnit.MILLISECONDS, RxRatpack.scheduler())
                    .map(unit -> {
                        System.out.println("[REACT] triggering timeout");
                        return Promise.value(Pair.of(202, "accepted"));
                    });

            Observable.merge(authWorkflow, timer)
                    .subscribe(conditionalDispatchSubscriber(ctx));
        };
    }

    private Subscriber<Promise<Pair<Integer, String>>> conditionalDispatchSubscriber(final Context ctx) {
        return new Subscriber<Promise<Pair<Integer, String>>>() {
            private AtomicBoolean dispatched = new AtomicBoolean(false);

            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Promise<Pair<Integer, String>> pairPromise) {
                if (!dispatched.get()) {
                    pairPromise.then(resultsPair -> {
                        logDispatch(resultsPair);
                        dispatchResponse(ctx, resultsPair.getLeft(), resultsPair.getRight());
                    });
                    dispatched.set(true);
                } else {
                    pairPromise.then(resultsPair -> {
                        logDispatch(resultsPair);
                    });
                }
            }
        };
    }

    private void logDispatch(Pair<Integer, String> resultsPair) {
        System.out.println(format("processing result pair status = %s, result = %s", resultsPair.getLeft(), resultsPair.getRight()));
    }

    private void dispatchResponse(Context ctx, int status, String message) {
        ctx.getResponse().status(status).send(writeJsonMessage(message));
    }

    private Promise<String> interpretGatewayResponse(Promise<ReceivedResponse> gatewayResponsePromise) {
        return gatewayResponsePromise.map(gatewayResponse -> {
            System.out.println("[REACT] finished processing gateway response");
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
            Thread.sleep(authDelay * 1000);
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
        Card card = readCardData(bodyData.getText());
        if (isBlank(card.getCardHolder()) ||
                isBlank(card.getCardNumber()) ||
                isBlank(card.getCvc()) ||
                isBlank(card.getAddress())) {
            throw new RuntimeException("invalid card details");
        }
        return card;

    }

    public Pair<Charge, Card> getCharge(String chargeId, Card card) {
        return Pair.of(new Charge(chargeId), card);
    }

    public Handler authoriseWebSocket() {
        return ctx -> WebSockets.websocket(ctx, handleAuthorisationWebSocket(ctx));

    }

    private WebSocketHandler<String> handleAuthorisationWebSocket(Context ctx) {
        return new WebSocketHandler<String>() {
            private WebSocket webSocket;

            @Override
            public String onOpen(WebSocket webSocket) throws Exception {
                this.webSocket = webSocket;
                return "{\"message\":\"welcome authorise websocket\"}";
            }

            @Override
            public void onClose(WebSocketClose<String> close) throws Exception {
                if(close.isFromClient()){
                    System.out.println("client closed connection");
                } else {
                    System.out.println("server closed connection");
                }
            }

            @Override
            public void onMessage(WebSocketMessage<String> frame) throws Exception {
                String chargeId = ctx.getPathTokens().get("chargeId");
                Observable.fromCallable(() -> readCardData(frame.getText()))
                        .map(card -> getCharge(chargeId, card))
                        .map(chargeCardPair -> submitToGateway(chargeCardPair))
                        .map(responsePromise -> interpretGatewayResponse(responsePromise))
                        .subscribe(gatewayResponse ->
                                gatewayResponse.then(responseString -> webSocket.send(responseString)));

            }
        };
    }

    private Card readCardData(String text) {
        try {
            return mapper.readValue(text, Card.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
