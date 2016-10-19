package firebreak.react.kafka;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class KafkaTestEnvironmentTest {

    @Test
    public void shouldStartAKafkaTestEnvironmentWithTopics() throws Exception {
        KafkaTestEnvironment testEnvironment = KafkaTestEnvironment
                .aKafkaTestEnvironment()
                .withTopics("kas-outbound", "kas-inbound");
        testEnvironment.startup();

        assertTrue(testEnvironment.hasTopic("kas-outbound"));
        assertTrue(testEnvironment.hasTopic("kas-inbound"));
        assertFalse(testEnvironment.hasTopic("test-1"));

        Producer<String, String> producer = testEnvironment.aProducer();
        Consumer<String, String> consumer = testEnvironment.aConsumerFor("kas-outbound", "gds");
        producer.send(new ProducerRecord<>("kas-outbound", "kasun", "sandor"));

        int found = 0;
        for (int i = 0; i < 10 && found == 0; i++) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            if (!records.isEmpty()) {
                assertThat(records.count(), is(1));
                List<ConsumerRecord<String, String>> consumerRecords = records.records(new TopicPartition("kas-outbound", 0));
                assertThat(consumerRecords.get(0).key(),is("kasun"));
                assertThat(consumerRecords.get(0).value(),is("sandor"));
                found = 1;
            } else {
                Thread.sleep(100);
            }
        }
        consumer.close();
        producer.close();

        testEnvironment.shutdown();
    }
}
