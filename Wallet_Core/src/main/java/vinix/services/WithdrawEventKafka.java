package vinix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger log = LoggerFactory.getLogger(WithdrawEventKafka.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(WithdrawCompletedEvent event) {

        log.info("event=withdraw_completed id={} accountId={} amount={}",
                event.id(),
                event.accountId(),
                event.amount());

        kafkaTemplate.send("withdraw-events", event.id().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("event=withdraw_sent id={} partition={} offset={}",
                                event.id(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("event=withdraw_failed id={}", event.id(), ex);
                    }
                });
    }
}