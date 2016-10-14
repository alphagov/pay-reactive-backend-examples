package firebreak.react.drop.service;

import firebreak.react.drop.model.Card;

public class AuthorisationService {
    private final Long authrosationDelay;

    public AuthorisationService(Long authrosationDelay) {
        this.authrosationDelay = authrosationDelay;
    }

    public String authorise(Card card) {
        try {
            Thread.sleep(authrosationDelay);
            return "authorisation successful";
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
