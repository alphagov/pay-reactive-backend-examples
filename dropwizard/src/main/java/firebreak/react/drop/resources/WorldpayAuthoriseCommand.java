package firebreak.react.drop.resources;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import firebreak.react.drop.model.Card;
import rx.Observable;

import javax.ws.rs.core.Response;

import static javax.ws.rs.client.ClientBuilder.newClient;

public class WorldpayAuthoriseCommand extends HystrixObservableCommand<Response> {

    private final String chargeId;
    private final Card card;

    public WorldpayAuthoriseCommand(String chargeId, Card card) {
        super(HystrixCommandGroupKey.Factory.asKey("WorldpayGroup"));
        this.chargeId = chargeId;
        this.card = card;
    }

    @Override
    protected Observable<Response> construct() {
        return Observable.create(subscriber -> {
            try {
                //use the card and chargeId here
                Response response = newClient().target("http://example.com")
                        .request()
                        .get();
                subscriber.onNext(response);
                subscriber.onCompleted();
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }

}
