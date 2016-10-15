package firebreak.react.kafka;

import kafka.admin.AdminUtils;
import kafka.admin.RackAwareMode;
import kafka.utils.ZKStringSerializer$;
import kafka.utils.ZkUtils;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.ZkConnection;

import java.util.List;
import java.util.Properties;

import static com.google.common.collect.Lists.newArrayList;

public class KafkaAdmin {
    public static final int SESSION_TIMEOUT = 10 * 1000;
    public static final int CONNECTION_TIMEOUT = 8 * 1000;
    private final boolean isSecureKafkaCluster;
    private final ZkUtils zkUtils;
    private final Properties topicConfig;
    private final ZkClient zkClient;
    private int partitions = 1;
    private int replication = 1;

    public KafkaAdmin(String zkConnectString) {
        isSecureKafkaCluster = false;
        zkClient = new ZkClient(zkConnectString, SESSION_TIMEOUT, CONNECTION_TIMEOUT, ZKStringSerializer$.MODULE$);
        zkUtils = new ZkUtils(zkClient, new ZkConnection(zkConnectString), isSecureKafkaCluster);
        topicConfig = new Properties();
    }

    public KafkaAdmin() {
        this("localhost:2181");
    }

    public boolean hasTopic(String topic) {
        return AdminUtils.topicExists(zkUtils, topic);
    }

    public void close() {
        zkClient.close();
    }

    public void createTopics(List<String> topics) {
        topics.forEach(topic -> {
            AdminUtils.createTopic(zkUtils, topic, partitions, replication, topicConfig, RackAwareMode.Enforced$.MODULE$);
        });
    }
}
