package firebreak.react.drop.resources.testrules;

import firebreak.react.kafka.KafkaTestEnvironment;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

public class KafkaEnvironmentRule implements TestRule {

    private KafkaTestEnvironment testEnvironment;

    public KafkaEnvironmentRule() {
        testEnvironment = KafkaTestEnvironment
                .aKafkaTestEnvironment()
                .withTopics("worldpay-outboud", "worldpay-inbound");
        testEnvironment.startup();
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return base;
    }

    public void stop() {
        testEnvironment.shutdown();
    }
}
