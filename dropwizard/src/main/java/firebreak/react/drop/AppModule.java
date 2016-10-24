package firebreak.react.drop;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import firebreak.react.drop.resources.AuthorisationService;
import firebreak.react.drop.resources.CardResource;
import firebreak.react.drop.resources.GatewayHandlerResource;
import firebreak.react.drop.resources.KafkaAuthorisationService;
import io.dropwizard.setup.Environment;

public class AppModule extends AbstractModule {

    private final AppConfiguration appConfiguration;
    private final Environment environment;

    public AppModule(AppConfiguration appConfiguration, Environment environment) {
        this.appConfiguration = appConfiguration;
        this.environment = environment;
    }

    @Override
    protected void configure() {
        bind(GatewayHandlerResource.class).in(Singleton.class);
        bind(CardResource.class).in(Singleton.class);
        bind(AuthorisationService.class).in(Singleton.class);
        bind(KafkaAuthorisationService.class).in(Singleton.class);
    }

}
