package firebreak.react.rat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ServiceModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(ObjectMapper.class).in(Singleton.class);
        bind(CardResource.class).in(Singleton.class);
    }
}
