package firebreak.react.drop.resources.testrules;

import firebreak.react.drop.App;
import firebreak.react.drop.AppConfiguration;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import static io.dropwizard.testing.ConfigOverride.config;
import static io.dropwizard.testing.ResourceHelpers.resourceFilePath;

public class DropwizardAppWithKafkaRule implements TestRule {

    private final DropwizardAppRule<AppConfiguration> appRule;
    private final KafkaEnvironmentRule kafkaRule;
    private final RuleChain ruleChain;

    public DropwizardAppWithKafkaRule() {
        kafkaRule = new KafkaEnvironmentRule();
        appRule = new DropwizardAppRule<>(App.class,
                resourceFilePath("test-config.yaml"),
                config("authorisationDelay", "1")
        );
        ruleChain = RuleChain.outerRule(kafkaRule).around(appRule);
    }

    @Override
    public Statement apply(Statement base, Description description) {
        return ruleChain.apply(new Statement() {
            @Override
            public void evaluate() throws Throwable {
                System.out.println("starting dropwizard app");
                base.evaluate();
            }
        }, description);
    }

    public void stop() {
        kafkaRule.stop();
    }

    public int getLocalPort() {
        return appRule.getLocalPort();
    }
}
