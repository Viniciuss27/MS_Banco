@Component
@RequiredArgsConstructor
public class TransferEventListener {
    private final NotificationService notificationService;

    @KafkaListener(topics = "transfer-events", groupId = "notification-group")
    public void handle(TransferCompletedEvent event) {
        notificationService.notifyTransfer(event);
    }
}