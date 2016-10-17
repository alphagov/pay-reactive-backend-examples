package firebreak.react.drop;

import com.google.common.io.Resources;
import firebreak.react.drop.resources.CardResource;
import react.backend.common.service.AuthorisationService;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

import java.io.File;
import java.net.URISyntaxException;

public class App extends Application<AppConfiguration> {

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {

        environment.jersey().register(new CardResource(new AuthorisationService(appConfiguration.getAuthorisationDelay())));
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            args = new String[]{"server", resourceFilePath("config.yaml")};
        }
        new App().run(args);
    }

    private static String resourceFilePath(String relativePath) throws URISyntaxException {
        return new File(Resources.getResource(relativePath).toURI()).getAbsolutePath();
    }
}
