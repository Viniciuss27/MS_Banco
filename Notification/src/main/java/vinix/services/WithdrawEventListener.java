package vinix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vinix.events.WithdrawCompletedEvent;

@Component
@RequiredArgsConstructor
public class WithdrawEventListener {

    private static final Logger log =
            LoggerFactory.getLogger(WithdrawEventListener.class);

    @KafkaListener(topics = "withdraw-events", groupId = "notification-group")
    public void handle(WithdrawCompletedEvent event) {

        log.info("event=withdraw_completed id={} accountId={} amount={}",
                event.id(),
                event.accountId(),
                event.amount());
    }
}