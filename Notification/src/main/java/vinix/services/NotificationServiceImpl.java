package vinix.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import vinix.events.DepositCompletedEvent;
import vinix.events.TransferCompletedEvent;
import vinix.events.WithdrawCompletedEvent;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationServiceImpl.class);

    @Override
    public void notifyTransfer(TransferCompletedEvent event) {
        log.info("event=notify_transfer id={} sourceAccountId={} targetAccountId={} amount={}",
                event.id(),
                event.sourceAccountId(),
                event.targetAccountId(),
                event.amount());
    }

    @Override
    public void notifyDeposit(DepositCompletedEvent event) {
        log.info("event=notify_deposit id={} accountId={} amount={}",
                event.id(),
                event.accountId(),
                event.amount());
    }

    @Override
    public void notifyWithdraw(WithdrawCompletedEvent event) {
        log.info("event=notify_withdraw id={} accountId={} amount={}",
                event.id(),
                event.accountId(),
                event.amount());
    }
}
