package vinix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vinix.events.DepositCompletedEvent;

@Component
@RequiredArgsConstructor
public class DepositEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(DepositEventListener.class);

    @KafkaListener(topics = "deposit-events", groupId = "notification-group")
    public void handle(DepositCompletedEvent event) {

        log.info("event=deposit_completed id={} accountId={} amount={}",
                event.id(),
                event.accountId(),
                event.amount());
    }
}
