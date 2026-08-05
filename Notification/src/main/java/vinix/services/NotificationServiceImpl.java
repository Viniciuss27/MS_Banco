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
		log.info("Notificação: transferência de {} da conta {} para a conta {}", event.amount(),
				event.sourceAccountId(), event.targetAccountId());
	}

	@Override
	public void notifyDeposit(DepositCompletedEvent event) {
		log.info("Notificação: depósito de {} na conta {}", event.amount(), event.accountId());
	}

	@Override
	public void notifyWithdraw(WithdrawCompletedEvent event) {
		log.info("Notificação: saque de {} na conta {}", event.amount(), event.accountId());
	}
}
