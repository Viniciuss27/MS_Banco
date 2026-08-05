package vinix.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vinix.events.DepositCompletedEvent;

@Component
@RequiredArgsConstructor
public class DepositEventKafka {
    private final KafkaTemplate<String, DepositCompletedEvent> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(DepositCompletedEvent event) {
        kafkaTemplate.send("deposit-events", event);
    }
}
