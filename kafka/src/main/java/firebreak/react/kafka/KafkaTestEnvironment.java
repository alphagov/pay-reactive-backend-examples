package firebreak.react.kafka;

import com.google.common.io.Resources;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class KafkaTestEnvironment {

    public static final int DEFAULT_ZK_PORT = 2181;
    private KafkaLocalServer kafka;
    private Properties kafkaProperties;
    private ZooKeeperLocalServer zookeeper;
    private int zkPort;

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

    public void startup() {
        startZooKeeper();
        startKafka();
    }

    public void shutdown() {
        stopKafka();
        stopZooKeeper();

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
