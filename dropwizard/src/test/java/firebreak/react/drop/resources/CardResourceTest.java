package firebreak.react.drop.resources;


import firebreak.react.drop.App;
import firebreak.react.drop.AppConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardResourceTest {

    @ClassRule
    public static DropwizardAppRule<AppConfiguration> app = new DropwizardAppRule<>(App.class);

    @Test
    public void shouldTestRootMessage() throws Exception {
        String message = given().port(app.getLocalPort())
                .contentType(JSON)
                .get()
                .body().jsonPath().getString("message");
        assertThat(message, is("hello async dropwizard"));
    }

}
