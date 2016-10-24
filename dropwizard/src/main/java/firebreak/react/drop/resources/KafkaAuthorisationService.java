package firebreak.react.drop.resources;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import firebreak.react.drop.model.Card;
import firebreak.react.drop.model.Charge;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static firebreak.react.drop.resources.KafkaClientBuilder.aKafkaClient;
import static java.lang.String.format;

public class KafkaAuthorisationService {

    private static final long DEFAULT_POLL_TIMEOUT = 100;

    public void doGatewayAuthorise(String chargeId, Card card, Consumer<String> callback) {
        preOperation(chargeId).
                switchMap(operationKafka(card))
                .subscribe(postKafkaOperation(callback));
    }


    private Observable<Charge> preOperation(String chargeId) {
        return Observable.fromCallable(() -> new Charge(chargeId));
    }

    private Func1<Charge, Observable<String>> operationKafka(Card card) {
        return charge -> {
            KafkaProducer<String, String> producer = null;
            try {
                producer = aKafkaClient().producer();
                ImmutableMap<String, String> requestData = ImmutableMap.of("chargeId", charge.getChargeId(), "card", jsonString(card));
                producer.send(new ProducerRecord<>("worldpay-outboud", 0, "authRequest", jsonString(requestData)));

                final KafkaConsumer<String, String> consumer = aKafkaClient().forTopic("worldpay-inbound")
                        .forGroupId("None")
                        // Never set this to `None` in production. This was done here due to https://github.com/dpkp/kafka-python/issues/690
                        // Just to get the test working quickly
                        .consumer();
                return Observable.fromCallable(() -> getWorldpayResponse(consumer))
                        .timeout(3, TimeUnit.SECONDS);
            } finally {
                producer.close();
            }
        };
    }

    private Subscriber<String> postKafkaOperation(Consumer<String> callback) {
        return new Subscriber<String>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                System.out.println("[ERROR] exception occured during kafka gateway operation, " + e);
                callback.accept(format("{\"message\": \"%s\"}", e.getMessage()));
            }

            @Override
            public void onNext(String gatewayResponse) {
                callback.accept(format("{\"message\": \"%s\"}", gatewayResponse));
            }
        };
    }

    private String getWorldpayResponse(KafkaConsumer<String, String> consumer) {
        try {
            boolean found = false;
            String gatewayResponse = "empty";
            while (!found) {
                ConsumerRecords<String, String> resultsMaybe = consumer.poll(DEFAULT_POLL_TIMEOUT);
                if (!resultsMaybe.isEmpty()) {
                    List<ConsumerRecord<String, String>> records = resultsMaybe.records(new TopicPartition("worldpay-inbound", 0));
                    gatewayResponse = records.get(0).value();
                    found = true;
                }
            }
            return gatewayResponse;
        } finally {
            consumer.close();
        }
    }

    private String jsonString(Object card) {
        try {
            return new ObjectMapper().writeValueAsString(card);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
