package firebreak.react.drop.resources;

import firebreak.react.drop.model.Card;
import firebreak.react.drop.model.Charge;
import org.glassfish.jersey.client.rx.rxjava.RxObservable;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import javax.ws.rs.core.Response;
import java.util.function.Consumer;

public class AuthorisationService {
    private final Long authorisationDelay;

    public AuthorisationService() {
        //forratpack
        authorisationDelay = 2L;
    }

    public AuthorisationService(Long authorisationDelay) {
        this.authorisationDelay = authorisationDelay;
    }

    public void doAuthorise(String chargeId, Card card, Consumer<String> callback) {
        preOperation(chargeId).
                switchMap(operation(card))
                .subscribe(postOperation(chargeId, callback));
    }

    private Action1<Response> postOperation(String chargeId, Consumer<String> callback) {
        return response -> {
            try {
                Thread.sleep(authorisationDelay);
                callback.accept("{\"message\": \"All good\"}");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private Func1<Charge, Observable<Response>> operation(Card card) {
        return charge -> RxObservable.newClient()
                .target("http://example.com")
                .request()
                .rx()
                .get();
    }

    private Observable<Charge> preOperation(String chargeId) {
        return Observable.fromCallable(() -> new Charge(chargeId));
    }
}
