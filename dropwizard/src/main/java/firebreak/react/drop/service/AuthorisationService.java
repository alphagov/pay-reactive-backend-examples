package firebreak.react.drop.service;

import firebreak.react.drop.model.Card;

import java.util.function.Consumer;

public class AuthorisationService {
    private final Long authorisationDelay;

    public AuthorisationService(Long authorisationDelay) {
        this.authorisationDelay = authorisationDelay;
    }

    public void doAuthorise(Card card, Consumer<String> callback){
        try {
            Thread.sleep(authorisationDelay);
            callback.accept("{\"message\": \"authorisation successful\"}");
        } catch (InterruptedException e) {
            callback.accept("{\"message\": \"some error\"}");
        }
    }
}
