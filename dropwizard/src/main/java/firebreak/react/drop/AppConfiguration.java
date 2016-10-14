package firebreak.react.drop;

import io.dropwizard.Configuration;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class AppConfiguration extends Configuration {

    @Valid
    @NotNull
    private Long authorisationDelay;

    public Long getAuthorisationDelay() {
        return authorisationDelay;
    }
}
