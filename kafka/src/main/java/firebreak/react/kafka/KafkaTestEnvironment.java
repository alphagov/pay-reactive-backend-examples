package firebreak.react.kafka;

import com.google.common.io.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.String.format;

public class KafkaTestEnvironment {

    public static final int DEFAULT_ZK_PORT = 2181;
    private KafkaLocalServer kafka;
    private Properties kafkaProperties;
    private ZooKeeperLocalServer zookeeper;
    private int zkPort;
    private List<String> topics = newArrayList("test-1");
    private KafkaAdmin kafkaAdmin;

    public static KafkaTestEnvironment aKafkaTestEnvironment() {
        return new KafkaTestEnvironment();
    }

    private KafkaTestEnvironment() {
        this.zkPort = DEFAULT_ZK_PORT;
        this.kafkaProperties = new Properties();
    }

    public KafkaTestEnvironment withZookeeperPort(int zkPort) {
        this.zkPort = zkPort;
        return this;
    }

    public KafkaTestEnvironment withKafkaProperties(File kafkaProps) {
        try {
            kafkaProperties.load(new FileInputStream(kafkaProps));
        } catch (IOException e) {
            throw new RuntimeException("error loading kafka properties from " + kafkaProps, e);
        }
        return this;
    }

    public KafkaTestEnvironment withTopics(String ... topics){
        this.topics = Arrays.asList(topics);
        return this;
    }

    public void startup() {
        startZooKeeper();
        startKafka();
        setupTopics();
    }

    private void setupTopics() {
        kafkaAdmin = new KafkaAdmin(format("localhost:%d", zkPort));
        kafkaAdmin.createTopics(topics);
    }

    public boolean hasTopic(String topic) {
        return kafkaAdmin.hasTopic(topic);
    }

    public void shutdown() {
        stopKafka();
        stopZooKeeper();
        kafkaAdmin.close();
    }

    private void stopKafka() {
        try {
            kafka.stop();
        } catch (Exception e) {
            throw new RuntimeException("error stopping kafka", e);
        }
    }

    private void stopZooKeeper() {
        try {
            zookeeper.stop();
        } catch (IOException e) {
            throw new RuntimeException("error stopping zookeeper", e);
        }
    }

    private void startKafka() {
        if (kafkaProperties.isEmpty()) {
            try {
                kafkaProperties.load(Resources.getResource("kafka/server.properties").openStream());
            } catch (IOException e) {
                throw new RuntimeException("error loading default kafka properties", e);
            }
        }
        kafka = new KafkaLocalServer(kafkaProperties);
        kafka.start();
    }

    private void startZooKeeper() {
        try {
            zookeeper = new ZooKeeperLocalServer(zkPort);
            zookeeper.start();
        } catch (Exception e) {
            throw new RuntimeException("error starting zookeeper", e);
        }
    }
}
