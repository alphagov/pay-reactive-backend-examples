package firebreak.react.drop.resources;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.glassfish.jersey.client.rx.rxjava.RxObservable;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static firebreak.react.drop.resources.KafkaClientBuilder.aKafkaClient;


public class GatewayHandlerResource {

    public GatewayHandlerResource() {

        Observable.interval(500, TimeUnit.MILLISECONDS)
                .subscribe(handleWorldpayRequestIfExist());
    }

    private Action1<Long> handleWorldpayRequestIfExist() {
        return eventSequence -> {
            KafkaConsumer<String, String> worldpayAuthoriseConsumer = null;
            try {
                System.out.println("[GatewayHandler] checking for auth requests");
                worldpayAuthoriseConsumer = aKafkaClient()
                        .forTopic("worldpay-outboud")
                        .forGroupId("None")
                        // Never set this to `None` in production. This was done here due to https://github.com/dpkp/kafka-python/issues/690
                        // Just to get the test working
                        .consumer();
                ConsumerRecords<String, String> resultsMaybe = worldpayAuthoriseConsumer.poll(250);
                if (!resultsMaybe.isEmpty()) {
                    List<ConsumerRecord<String, String>> records = resultsMaybe.records(new TopicPartition("worldpay-outboud", 0));
                    handleAuthorise()
                            .apply(records);
                }
            } finally {
                worldpayAuthoriseConsumer.close();
            }
        };
    }

    private Function<List<ConsumerRecord<String, String>>, Void> handleAuthorise() {
        return consumerRecords -> {
            consumerRecords.stream().map(consumerRecord ->
                    RxObservable.newClient()
                            .target("http://example.com")
                            .request()
                            .rx()
                            .get()
                            .map(interpretWorldpayReponse()))
                    .forEach(publishResult());
            return null;
        };
    }

    private Consumer<Observable<Pair<Integer, String>>> publishResult() {
        return resultObservable -> resultObservable.subscribe(resultPair -> {
            KafkaProducer<String, String> producer = null;
            try {
                producer = aKafkaClient().producer();
                producer.send(new ProducerRecord<>("worldpay-inbound", resultPair.getKey().toString(), resultPair.getValue()));
            } finally {
                producer.close();
            }
        });
    }

    private Func1<Response, Pair<Integer, String>> interpretWorldpayReponse() {
        return response -> response.getStatus() == 200 ? Pair.of(200, "success") : Pair.of(400, "auth failed");
    }

}
