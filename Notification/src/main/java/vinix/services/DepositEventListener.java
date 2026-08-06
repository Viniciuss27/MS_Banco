package vinix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vinix.events.DepositCompletedEvent;

@Component
@RequiredArgsConstructor
public class DepositEventListener {

    private final KafkaTemplate<String, DepositCompletedEvent> kafkaTemplate;
    private static final Logger log = LoggerFactory.getLogger(DepositEventListener.class);

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(DepositCompletedEvent event) {

        log.info("event=deposit_completed id={} accountId={} amount={}",
                event.id(),
                event.accountId(),
                event.amount());

        kafkaTemplate.send("deposit-events", event.id().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("event=deposit_sent id={} partition={} offset={}",
                                event.id(),
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset());
                    } else {
                        log.error("event=deposit_failed id={}", event.id(), ex);
                    }});
    }
}
