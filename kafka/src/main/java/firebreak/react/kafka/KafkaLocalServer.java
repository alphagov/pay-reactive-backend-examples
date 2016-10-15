package firebreak.react.kafka;

import kafka.server.KafkaConfig;
import kafka.server.KafkaServerStartable;

import java.util.Properties;

public class KafkaLocalServer {

    public KafkaServerStartable kafka;

    public KafkaLocalServer(Properties kafkaProperties) {
        KafkaConfig kafkaConfig = new KafkaConfig(kafkaProperties);
        kafka = new KafkaServerStartable(kafkaConfig);
    }

    public void start() {
        kafka.startup();
    }

    public void stop() {
        kafka.shutdown();
    }
}
