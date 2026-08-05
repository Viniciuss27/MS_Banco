package vinix.services;

import vinix.events.DepositCompletedEvent;
import vinix.events.TransferCompletedEvent;
import vinix.events.WithdrawCompletedEvent;

public interface NotificationService {
    void notifyTransfer(TransferCompletedEvent event);
    void notifyDeposit(DepositCompletedEvent event);
    void notifyWithdraw(WithdrawCompletedEvent event);
}
