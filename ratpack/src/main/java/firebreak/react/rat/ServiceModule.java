package firebreak.react.rat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import react.backend.common.service.AuthorisationService;

public class ServiceModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(ObjectMapper.class).in(Singleton.class);
        bind(AuthorisationService.class).in(Singleton.class);
        bind(CardResource.class).in(Singleton.class);
    }
}
