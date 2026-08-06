package vinix.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vinix.events.DepositCompletedEvent;

@Component
@RequiredArgsConstructor
public class DepositEventListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "deposit-events", groupId = "notification-group")
    public void handle(DepositCompletedEvent event) {
        notificationService.notifyDeposit(event);
    }
}
