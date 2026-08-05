package vinix;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;

import vinix.dto.request.DepositRequestDTO;
import vinix.dto.request.TransferRequestDTO;
import vinix.dto.request.WithdrawRequestDTO;
import vinix.entities.Account;
import vinix.entities.Transaction;
import vinix.events.DepositCompletedEvent;
import vinix.events.TransferCompletedEvent;
import vinix.events.WithdrawCompletedEvent;
import vinix.mapper.AccountMapper;
import vinix.mapper.TransactionMapper;
import vinix.repositories.AccountRepository;
import vinix.repositories.TransactionRepository;
import vinix.services.AccountServiceImpl;

@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @Mock private AccountRepository accountRepository;
    @Mock private TransactionRepository transactionRepository;
    @Mock private AccountMapper accountMapper;
    @Mock private TransactionMapper transactionMapper;
    @Mock private ApplicationEventPublisher eventPublisher;
    @Mock private KafkaTemplate<String, TransferCompletedEvent> kafkaTemplate;

    @InjectMocks
    private AccountServiceImpl service;

    private Account sourceAccount;
    private Account targetAccount;

    @BeforeEach
    void setup() {

        sourceAccount = new Account();
        sourceAccount.setId(1L);
        sourceAccount.setBalance(new BigDecimal("1000.00"));

        targetAccount = new Account();
        targetAccount.setId(2L);
        targetAccount.setBalance(new BigDecimal("500.00"));
    }

    @Test
    void shouldDepositSuccessfully() {

        DepositRequestDTO dto = new DepositRequestDTO(
        		new BigDecimal("200.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.deposit(1L, dto);
        
        assertEquals(new BigDecimal("1200.00"), sourceAccount.getBalance() );
        verify(transactionRepository) .save(any(Transaction.class));
        verify(eventPublisher).publishEvent(any(DepositCompletedEvent.class));
    }

    @Test
    void shouldWithdrawSuccessfully() {

        WithdrawRequestDTO dto = new WithdrawRequestDTO(
        		new BigDecimal("300.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.withdraw(1L, dto);
        
        assertEquals( new BigDecimal("700.00"),sourceAccount.getBalance());
        verify(transactionRepository).save(any(Transaction.class));
        verify(eventPublisher) .publishEvent(any(WithdrawCompletedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenWithdrawWithoutBalance() {

        WithdrawRequestDTO dto = new WithdrawRequestDTO(
        		new BigDecimal("2000.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        RuntimeException exception =assertThrows(
                        RuntimeException.class, () -> service.withdraw(1L, dto) );

        assertTrue(exception.getMessage().contains("Saldo insuficiente"));
        verify(transactionRepository, never()).save(any());
        verify(eventPublisher, never()) .publishEvent(any());
    }

    @Test
    void shouldTransferSuccessfully() {

        TransferRequestDTO dto = new TransferRequestDTO(
                        1L , 2L, new BigDecimal("300.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(targetAccount));

        when(transactionRepository.save(any(Transaction.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.transfer(dto);
        
        assertEquals(new BigDecimal("700.00"), sourceAccount.getBalance());
        assertEquals(new BigDecimal("800.00"), targetAccount.getBalance());
        verify(eventPublisher).publishEvent(any(TransferCompletedEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenTransferWithoutBalance() {

        TransferRequestDTO dto = new TransferRequestDTO(
                        1L, 2L, new BigDecimal("5000.00"));

        when(accountRepository.findById(1L))
                .thenReturn(Optional.of(sourceAccount));

        when(accountRepository.findById(2L))
                .thenReturn(Optional.of(targetAccount));

        RuntimeException exception = assertThrows(
        		RuntimeException.class, () -> service.transfer(dto));

        assertTrue(exception.getMessage() .contains("Saldo insuficiente") );
        verify(transactionRepository, never()).save(any());
        verify(eventPublisher, never()) .publishEvent(any());
    }

    @Test
    void shouldThrowExceptionWhenAccountNotFound() {

        when(accountRepository.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                 RuntimeException.class,() -> service
                 .deposit(1L, new DepositRequestDTO(BigDecimal.TEN) ));

        assertTrue(exception.getMessage().contains("Conta não encontrada"));
        verify(eventPublisher, never()) .publishEvent(any());
    }
}