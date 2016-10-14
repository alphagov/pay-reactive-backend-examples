package firebreak.react.drop;

import firebreak.react.drop.resources.CardResource;
import firebreak.react.drop.service.AuthorisationService;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class App extends Application<AppConfiguration> {

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {

        environment.jersey().register(new CardResource(new AuthorisationService(appConfiguration.getAuthorisationDelay())));
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            args = new String[]{"server"};
        }
        new App().run(args);
    }
}
