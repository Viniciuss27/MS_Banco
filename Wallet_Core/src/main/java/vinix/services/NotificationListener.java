package vinix.services;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import lombok.RequiredArgsConstructor;
import vinix.events.DepositCompletedEvent;
import vinix.events.TransferCompletedEvent;
import vinix.events.WithdrawCompletedEvent;

@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final NotificationService notificationService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleTransfer(TransferCompletedEvent event) {
        notificationService.notifyTransfer(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleDeposit(DepositCompletedEvent event) {
        notificationService.notifyDeposit(event);
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleWithdraw(WithdrawCompletedEvent event) {
        notificationService.notifyWithdraw(event);
    }
}
