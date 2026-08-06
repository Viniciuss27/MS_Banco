package vinix.services;

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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(TransferCompletedEvent event) {
        kafkaTemplate.send("transfer-events", event);
    }
}
