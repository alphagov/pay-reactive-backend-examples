package firebreak.react.kafka;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;

public class KafkaTestEnvironmentTest {

    @Test
    public void shouldStartAKafkaTestEnvironmentWithTopics() throws Exception {
        KafkaTestEnvironment testEnvironment = KafkaTestEnvironment.aKafkaTestEnvironment()
                .withTopics("kas-outbound", "kas-inbound");
        testEnvironment.startup();

        assertTrue(testEnvironment.hasTopic("kas-outbound"));
        assertTrue(testEnvironment.hasTopic("kas-inbound"));
        assertFalse(testEnvironment.hasTopic("test-1"));

        testEnvironment.shutdown();
    }
}
