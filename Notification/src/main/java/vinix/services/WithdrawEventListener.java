package vinix.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import vinix.events.WithdrawCompletedEvent;

@Component
@RequiredArgsConstructor
public class WithdrawEventListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "withdraw-events", groupId = "notification-group")
    public void handle(WithdrawCompletedEvent event) {
        notificationService.notifyWithdraw(event);
    }
}
