package firebreak.react.rat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import ratpack.server.ServerConfig;

public class ServiceModule  extends AbstractModule {

    private final ServerConfig serverConfig;

    public ServiceModule(ServerConfig serverConfig) {
        this.serverConfig = serverConfig;
    }

    @Override
    protected void configure() {
        bind(ObjectMapper.class).in(Singleton.class);
        bind(CardResource.class).in(Singleton.class);
    }
}
