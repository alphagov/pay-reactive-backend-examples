package firebreak.react.drop;

import firebreak.react.drop.resources.CardResource;
import io.dropwizard.Application;
import io.dropwizard.setup.Environment;

public class App extends Application<AppConfiguration> {

    @Override
    public void run(AppConfiguration appConfiguration, Environment environment) throws Exception {
        environment.jersey().register(CardResource.class);
    }

    public static void main(String[] args) throws Exception {
        if (args == null || args.length == 0) {
            args = new String[]{"server"};
        }
        new App().run(args);
    }
}
