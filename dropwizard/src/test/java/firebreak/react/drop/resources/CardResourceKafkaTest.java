package firebreak.react.drop.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.jayway.restassured.response.Response;
import firebreak.react.drop.resources.testrules.DropwizardAppWithKafkaRule;
import org.junit.After;
import org.junit.Rule;
import org.junit.Test;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.http.ContentType.JSON;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CardResourceKafkaTest {

    private static ObjectMapper objectMapper = new ObjectMapper();

    @Rule
    public DropwizardAppWithKafkaRule appWithKafkaRule = new DropwizardAppWithKafkaRule();

    @Test
    public void shouldAuthroseWorldpayUsingKafka() throws Exception {
        Response response = given().port(appWithKafkaRule.getLocalPort())
                .contentType(JSON)
                .body(cardJson())
                .post("authoriseKafka/1111");

        assertThat(response.getStatusCode(), is(200));
    }

    private String cardJson() throws JsonProcessingException {
        ImmutableMap<String, String> cardDetails = ImmutableMap.of("cardHolder", "Rupert Merdoch",
                "cardNumber", "1234567890",
                "cvc", "121",
                "address", "St Martinis");
        return objectMapper.writeValueAsString(cardDetails);
    }

    @After
    public void after() throws Exception {
        appWithKafkaRule.stop();
    }
}
