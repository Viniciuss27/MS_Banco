package vinix.services;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vinix.events.WithdrawCompletedEvent;

@Component
@RequiredArgsConstructor
public class WithdrawEventKafka {
    private final KafkaTemplate<String, WithdrawCompletedEvent> kafkaTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WithdrawCompletedEvent event) {
        kafkaTemplate.send("withdraw-events", event);
    }
}
