package firebreak.react.rat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ratpack.test.embed.EmbeddedApp;

import static firebreak.react.rat.App.serverSpec;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardResourceTest {

    private EmbeddedApp app;
    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void before() throws Exception {
        app = EmbeddedApp.of(serverSpec());
    }

    @Test
    public void shouldSayHello() throws Exception {
        app.test(client -> {
            String mes = client.get().getBody().getText();
            JsonNode message = mapper.readTree(mes);
            assertThat(message.get("message").asText(),is("Hello from (Dean Martin)"));
        });
    }

    @After
    public void after() throws Exception {
        app.close();
    }
}
