package firebreak.react.rat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ratpack.http.client.ReceivedResponse;
import ratpack.test.embed.EmbeddedApp;

import static firebreak.react.rat.App.serverSpec;
import static java.lang.String.format;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardResourceDelayedAuthTest {

    private EmbeddedApp app;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void before() throws Exception {
        app = EmbeddedApp.of(serverSpec(ImmutableMap.of("app.authDelay", "2")));
    }

    @Test
    public void shouldAuthoriseObserveWithDelay() throws Exception {
        ImmutableMap<String, String> cardData = ImmutableMap.of("cardHolder", "holder", "cardNumber", "number", "cvc", "123", "address", "EC2 4RT");

        app.test(client -> {
            ReceivedResponse receivedResponse = client.requestSpec(requestSpec ->
                    requestSpec.body(body -> body.text(mapper.writeValueAsString(cardData))))
                    .post(format("/authoriseObserve/%s", "7473458"));

            assertThat(receivedResponse.getStatusCode(), is(202));
            JsonNode message = mapper.readTree(receivedResponse.getBody().getText());
            assertThat(message.get("message").asText(), is("accepted"));
            Thread.sleep(3000);
        });

    }

    @After
    public void after() throws Exception {
        app.close();
    }
}
