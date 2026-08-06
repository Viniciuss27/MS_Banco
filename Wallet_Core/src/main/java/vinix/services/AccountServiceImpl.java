package vinix.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import vinix.dto.request.AccountRequestDTO;
import vinix.dto.request.DepositRequestDTO;
import vinix.dto.request.TransferRequestDTO;
import vinix.dto.request.WithdrawRequestDTO;
import vinix.dto.response.AccountResponseDTO;
import vinix.dto.response.TransactionResponseDTO;
import vinix.entities.Account;
import vinix.entities.Transaction;
import vinix.entities.TransactionType;
import vinix.events.DepositCompletedEvent;
import vinix.events.TransferCompletedEvent;
import vinix.events.WithdrawCompletedEvent;
import vinix.mapper.AccountMapper;
import vinix.mapper.TransactionMapper;
import vinix.repositories.AccountRepository;
import vinix.repositories.TransactionRepository;
import vinix.resources.exceptions.DuplicateDocumentException;
import vinix.services.exceptions.AccountNotFoundException;
import vinix.services.exceptions.InsufficientBalanceException;
import vinix.services.exceptions.InvalidAmountException;
import vinix.services.exceptions.SameAccountTransferException;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final AccountMapper accountMapper;
    private final TransactionMapper transactionMapper;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    @Transactional
    public AccountResponseDTO create(AccountRequestDTO dto) {
    	
        String cleanDocument = dto.document().replaceAll("[^0-9]", "");

        if (accountRepository.findByDocument(cleanDocument).isPresent()) {
            throw new DuplicateDocumentException(
                "Já existe uma conta cadastrada com o documento " + cleanDocument );
        }

        Account account = accountMapper.toEntity(dto);
        return accountMapper.toResponseDTO(accountRepository.save(account));
    }

    @Override
    public AccountResponseDTO findById(Long id) {
        return accountMapper.toResponseDTO(findAccountOrThrow(id));
    }

    @Override
    public List<AccountResponseDTO> findAll() {
        return accountRepository.findAll()
                .stream()
                .map(accountMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public TransactionResponseDTO deposit(Long accountId, DepositRequestDTO dto) {
    	
        validateAmount(dto.amount());

        Account account = findAccountOrThrow(accountId);
        account.setBalance(account.getBalance().add(dto.amount()));

        Transaction transaction = registerTransaction(
                account,
                TransactionType.DEPOSIT,
                dto.amount(),
                "Depósito em conta" );

        eventPublisher.publishEvent(
                new DepositCompletedEvent(transaction.getId() ,account.getId(), dto.amount(), now()));

        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    @Transactional
    public TransactionResponseDTO withdraw(Long accountId, WithdrawRequestDTO dto) {
        validateAmount(dto.amount());

        Account account = findAccountOrThrow(accountId);

        if (account.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientBalanceException(
                "Saldo insuficiente para a conta " + accountId);
        }

        account.setBalance(account.getBalance().subtract(dto.amount()));

        Transaction transaction = registerTransaction(
                account,
                TransactionType.WITHDRAW,
                dto.amount(),
                "Saque em conta" );

        eventPublisher.publishEvent(
                new WithdrawCompletedEvent(transaction.getId(), account.getId(), dto.amount(), now()) );

        return transactionMapper.toResponseDTO(transaction);
    }

    @Override
    @Transactional
    public List<TransactionResponseDTO> transfer(TransferRequestDTO dto) {
        validateAmount(dto.amount());

        if (dto.sourceAccountId().equals(dto.targetAccountId())) {
            throw new SameAccountTransferException(
            		"Não é possível transferir para a mesma conta");
        }
        // evita deadlock: sempre busca na mesma ordem
        Account source;
        Account target;

        if (dto.sourceAccountId() < dto.targetAccountId()) {
            source = findAccountOrThrow(dto.sourceAccountId());
            target = findAccountOrThrow(dto.targetAccountId());
        } else {
            target = findAccountOrThrow(dto.targetAccountId());
            source = findAccountOrThrow(dto.sourceAccountId());
        }

        if (source.getBalance().compareTo(dto.amount()) < 0) {
            throw new InsufficientBalanceException(
                "Saldo insuficiente na conta de origem: " + source.getId());
        }

        source.setBalance(source.getBalance().subtract(dto.amount()));
        target.setBalance(target.getBalance().add(dto.amount()));

        Transaction out = registerTransaction(
                source,
                TransactionType.TRANSFER_OUT,
                dto.amount(),
                "Transferência para conta " + target.getId());

        Transaction in = registerTransaction(
                target,
                TransactionType.TRANSFER_IN,
                dto.amount(),
                "Transferência recebida da conta " + source.getId());

        eventPublisher.publishEvent(
                new TransferCompletedEvent(out.getId(), source.getId(),target.getId(),dto.amount(),now()));

        return transactionMapper.toResponseDTOList(List.of(out, in));
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidAmountException("Valor inválido");
        }
    }

    private LocalDateTime now() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }

    private Transaction registerTransaction(
            Account account,
            TransactionType type,
            BigDecimal amount,
            String description
    ) {
        Transaction transaction = Transaction.builder()
                .account(account)
                .type(type)
                .amount(amount)
                .balanceAfter(account.getBalance())
                .description(description)
                .build();

        return transactionRepository.save(transaction);
    }

    private Account findAccountOrThrow(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException("Conta não encontrada: " + id)
                );
    }
}