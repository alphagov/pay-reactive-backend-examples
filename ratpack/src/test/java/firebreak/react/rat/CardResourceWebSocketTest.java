package firebreak.react.rat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import org.glassfish.tyrus.client.ClientManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ratpack.test.embed.EmbeddedApp;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

import static firebreak.react.rat.App.serverSpec;

public class CardResourceWebSocketTest {

    private EmbeddedApp app;
    private ObjectMapper mapper = new ObjectMapper();
    private CountDownLatch messageLatch;

    @Before
    public void before() throws Exception {
        app = EmbeddedApp.of(serverSpec(ImmutableMap.of("app.authDelay", "0")));
    }

    @Test
    public void shouldAuthoriseObserveWithDelay() throws Exception {
        ImmutableMap<String, String> cardData = ImmutableMap.of("cardHolder", "holder", "cardNumber", "number", "cvc", "123", "address", "EC2 4RT");
        try {
            messageLatch = new CountDownLatch(1);

            final ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().build();
            ClientManager client = ClientManager.createClient();
            client.connectToServer(new Endpoint() {

                @Override
                public void onOpen(Session session, EndpointConfig config) {
                    try {
                        session.addMessageHandler((MessageHandler.Whole<String>) message -> {
                            System.out.println("Received message: "+message);
                            messageLatch.countDown();
                        });
                        session.getBasicRemote().sendText(mapper.writeValueAsString(cardData));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, cec, new URI("ws://localhost:5050/authoriseWebSocket/1234"));

        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @After
    public void after() throws Exception {
        app.close();
    }
}
