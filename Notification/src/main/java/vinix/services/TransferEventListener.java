package vinix.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vinix.events.TransferCompletedEvent;

@Component
@RequiredArgsConstructor
public class TransferEventListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "transfer-events", groupId = "notification-group")
    public void handle(TransferCompletedEvent event) {
        notificationService.notifyTransfer(event);
    }
}
