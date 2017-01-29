package firebreak.react.drop.resources;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;
import firebreak.react.drop.App;
import firebreak.react.drop.AppConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.ClassRule;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardResourceHystrixTest {

    @ClassRule
    public static DropwizardAppRule<AppConfiguration> app = new DropwizardAppRule<>(App.class,
            resourceFilePath("test-config.yaml"),
            config("authorisationDelay", "500")
    );

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void shouldAuthoriseAndReturn200OnShortDelay() throws Exception {

        Response response = given().port(app.getLocalPort())
                .contentType(JSON)
                .body(cardJson())
                .post("authoriseHystrix/1111");
        assertThat(response.getStatusCode(), is(200));

    }

    private String cardJson() throws JsonProcessingException {
        ImmutableMap<String, String> cardDetails = ImmutableMap.of("cardHolder", "a name",
                "cardNumber", "1234567890",
                "cvc", "121",
                "address", "an address line");
        return objectMapper.writeValueAsString(cardDetails);
    }

}
