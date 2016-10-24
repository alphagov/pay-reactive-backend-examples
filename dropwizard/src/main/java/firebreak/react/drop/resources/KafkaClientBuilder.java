package firebreak.react.drop.resources;

import com.sun.deploy.util.StringUtils;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

public class KafkaClientBuilder {

    public static final int DEFAULT_KAFKA_PORT = 9092;
    private String groupId = "test-group";
    private String topicName = "test-1";
    private List<String> kafkaHostPorts = newArrayList(format("localhost:%d", DEFAULT_KAFKA_PORT));

    public static KafkaClientBuilder aKafkaClient() {
        return new KafkaClientBuilder();
    }

    public KafkaClientBuilder forTopic(String topicName) {
        this.topicName = topicName;
        return this;
    }

    public KafkaClientBuilder forGroupId(String groupId) {
        this.groupId = groupId;
        return this;
    }

    public <K, V> KafkaConsumer<K, V> consumer() {

        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaHostPortString());
        props.put("group.id", groupId);
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<K, V> consumer = new KafkaConsumer<>(props);
        consumer.assign(Arrays.asList(new TopicPartition(topicName, 0)));
        // instead of consumer.assign() it is better to use consumer.subscribe() in production.
        // This was done to get a test working without delay. See https://github.com/dpkp/kafka-python/issues/690
        return consumer;
    }

    private String kafkaHostPortString() {
        return StringUtils.join(kafkaHostPorts, ";");
    }

    public <K, V> KafkaProducer<K, V> producer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", kafkaHostPortString());
        props.put("broker.id", "0");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(props);
    }
}
