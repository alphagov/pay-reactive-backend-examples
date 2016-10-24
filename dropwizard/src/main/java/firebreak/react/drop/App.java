package firebreak.react.drop;

import com.google.common.io.Resources;
import com.google.inject.Guice;
import com.google.inject.Injector;
import firebreak.react.drop.resources.CardResource;
import firebreak.react.drop.resources.GatewayHandlerResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.net.URISyntaxException;

public class App extends Application<AppConfiguration> {

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {
        final Injector injector = Guice.createInjector(new AppModule(appConfiguration, environment));
        environment.jersey().register(injector.getInstance(CardResource.class));
        injector.getInstance(GatewayHandlerResource.class);
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            args = new String[]{"server", resourceFilePath("config.yaml")};
        }
        new App().run(args);
    }

    public static String resourceFilePath(String relativePath) throws URISyntaxException {
        return new File(Resources.getResource(relativePath).toURI()).getAbsolutePath();
    }
}
