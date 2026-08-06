package vinix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vinix.events.TransferCompletedEvent;

@Component
@RequiredArgsConstructor
public class TransferEventListener {

    private final NotificationService notificationService;
    private static final Logger log = LoggerFactory.getLogger(TransferEventListener.class);

    @KafkaListener(topics = "transfer-events", groupId = "notification-group")
    public void handle(TransferCompletedEvent event) {

        log.info("event=transfer_received id={} sourceAccountId={} targetAccountId={} amount={}",
                event.id(),
                event.sourceAccountId(),
                event.targetAccountId(),
                event.amount());

        try {
            notificationService.notifyTransfer(event);

            log.info("event=notification_processed id={}", event.id());

        } catch (Exception ex) {
            log.error("event=notification_failed id={}", event.id(), ex);
            throw ex; 
        }
    }
}
