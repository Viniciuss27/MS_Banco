package vinix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vinix.events.TransferCompletedEvent;

@Component
@RequiredArgsConstructor
public class TransferEventKafka {

    private final KafkaTemplate<String, TransferCompletedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(TransferEventKafka.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransferCompletedEvent event) {

        log.info("event=transfer_completed id={} sourceAccountId={} targetAccountId={} amount={}",
                event.id(),
                event.sourceAccountId(),
                event.targetAccountId(),
                event.amount());

        kafkaTemplate.send("transfer-events", event.id().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("event=transfer_sent id={} partition={} offset={}",
                                event.id(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("event=transfer_failed id={}", event.id(), ex);
                    }
                });
    }
}
