package firebreak.react.rat;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;

public class ServiceModule  extends AbstractModule {
    @Override
    protected void configure() {
        bind(CardResource.class).in(Singleton.class);
    }
}
